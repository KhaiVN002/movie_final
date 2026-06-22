package com.nmquan1503.backend_springboot.services.reservation;

import com.nmquan1503.backend_springboot.dtos.requests.product.ProductOrderRequest;
import com.nmquan1503.backend_springboot.dtos.requests.reservation.ReservationCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.reservation.ReservationDetailResponse;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationProduct;
import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import com.nmquan1503.backend_springboot.entities.user.User;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.reservation.ReservationMapper;
import com.nmquan1503.backend_springboot.mappers.reservation.ReservationProductMapper;
import com.nmquan1503.backend_springboot.mappers.theater.SeatMapper;
import com.nmquan1503.backend_springboot.repositories.reservation.ReservationRepository;
import com.nmquan1503.backend_springboot.services.authentication.AuthenticationService;
import com.nmquan1503.backend_springboot.services.product.BranchProductService;
import com.nmquan1503.backend_springboot.services.showtime.ShowtimeService;
import com.nmquan1503.backend_springboot.services.theater.SeatService;
import com.nmquan1503.backend_springboot.services.ticket.TicketPriceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationService {

    static final long REDIS_LOCK_TTL_SECONDS = 30;

    ReservationRepository reservationRepository;

    ShowtimeService showtimeService;
    AuthenticationService authenticationService;
    ReservationStatusService reservationStatusService;
    SeatService seatService;
    SeatHoldService seatHoldService;
    ReservationSeatService reservationSeatService;
    ReservationProductService reservationProductService;
    TicketPriceService ticketPriceService;
    BranchProductService branchProductService;

    ReservationMapper reservationMapper;
    SeatMapper seatMapper;
    ReservationProductMapper reservationProductMapper;

    @Transactional
    public Long createReservation(ReservationCreationRequest request) {
        List<Long> seatIds = request.getSeatIds().stream().distinct().sorted().toList();
        if (seatIds.size() != request.getSeatIds().size()) {
            throw new GeneralException(ResponseCode.DUPLICATE_SEAT);
        }

        String redisOwnerToken = UUID.randomUUID().toString();
        try {
            boolean redisLockAcquired = seatService.tryLockSeats(
                    request.getShowtimeId(),
                    seatIds,
                    redisOwnerToken,
                    REDIS_LOCK_TTL_SECONDS
            );
            if (!redisLockAcquired) {
                throw new GeneralException(ResponseCode.SEAT_NOT_AVAILABLE);
            }
            releaseRedisLockAfterTransaction(request.getShowtimeId(), seatIds, redisOwnerToken);
        } catch (GeneralException exception) {
            throw exception;
        } catch (RuntimeException redisException) {
            // PostgreSQL's unique seat hold remains the source of truth if Redis is unavailable.
            log.warn("Redis seat lock unavailable for showtime {}. Falling back to database lock.",
                    request.getShowtimeId());
            log.debug("Redis seat lock failure", redisException);
        }

        return createReservationInDatabase(request, seatIds);
    }

    private void releaseRedisLockAfterTransaction(
            Long showtimeId,
            List<Long> seatIds,
            String ownerToken
    ) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                try {
                    seatService.unlockSeats(showtimeId, seatIds, ownerToken);
                } catch (RuntimeException redisException) {
                    log.warn("Could not release Redis seat lock for showtime {}. It will expire automatically.",
                            showtimeId);
                    log.debug("Redis seat unlock failure", redisException);
                }
            }
        });
    }

    private Long createReservationInDatabase(ReservationCreationRequest request, List<Long> seatIds) {
        Long userId = authenticationService.getCurrentUserId();
        Showtime showtime = showtimeService.fetchById(request.getShowtimeId());
        if (!"AVAILABLE".equals(showtime.getStatus().getName())) {
            throw new GeneralException(ResponseCode.SHOWTIME_NOT_AVAILABLE);
        }

        List<Seat> seats = seatService.fetchByIds(seatIds);
        for (Seat seat : seats) {
            if (!seat.getRoom().getId().equals(showtime.getRoom().getId())) {
                throw new GeneralException(ResponseCode.SEAT_NOT_FOUND);
            }
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(5);
        Reservation reservation = Reservation.builder()
                .showtime(showtime)
                .user(User.builder().id(userId).build())
                .startTime(startTime)
                .endTime(endTime)
                .status(reservationStatusService.fetchByName("PENDING"))
                .build();
        reservation = reservationRepository.saveAndFlush(reservation);

        // Atomic PostgreSQL upserts make this safe across backend instances.
        seatHoldService.claimSeats(showtime.getId(), reservation.getId(), seatIds, endTime);
        reservationSeatService.save(reservation, seats);

        if (request.getProductOrders() != null && !request.getProductOrders().isEmpty()) {
            List<Byte> productIds = request.getProductOrders().stream()
                    .map(ProductOrderRequest::getProductId)
                    .toList();
            if (!branchProductService.existsByBranchIdAndProductIds(
                    showtime.getRoom().getBranch().getId(),
                    productIds
            )) {
                throw new GeneralException(ResponseCode.PRODUCT_NOT_FOUND);
            }
            reservationProductService.save(reservation, request.getProductOrders());
        }

        return reservation.getId();
    }

    @Transactional
    public Reservation fetchById(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new GeneralException(ResponseCode.RESERVATION_NOT_FOUND));
        expireIfNeeded(reservation);
        return reservation;
    }

    @Transactional
    public Reservation fetchByIdForUpdate(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new GeneralException(ResponseCode.RESERVATION_NOT_FOUND));
        expireIfNeeded(reservation);
        return reservation;
    }

    public double getTotalAmountByReservation(Reservation reservation) {
        double total = ticketPriceService.getTotalTicketPrice(reservation);
        List<ReservationProduct> reservationProducts =
                reservationProductService.fetchByReservationId(reservation.getId());
        for (ReservationProduct reservationProduct : reservationProducts) {
            total += reservationProduct.getQuantity() * reservationProduct.getProduct().getPrice();
        }
        return total;
    }

    public void markReservationAsPaid(Reservation reservation) {
        reservation.setStatus(reservationStatusService.fetchByName("PAID"));
        reservationRepository.save(reservation);
        seatHoldService.markReservationAsBooked(reservation.getId());
    }

    @Transactional
    public ReservationDetailResponse getReservationDetail(Long reservationId) {
        Long userId = authenticationService.getCurrentUserId();
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .filter(item -> item.getUser().getId().equals(userId))
                .orElseThrow(() -> new GeneralException(ResponseCode.RESERVATION_NOT_FOUND));
        expireIfNeeded(reservation);

        ReservationDetailResponse response = reservationMapper.toReservationDetailResponse(reservation);
        List<Seat> seats = seatService.fetchSeatsByReservationId(reservationId);
        response.setSeats(seatMapper.toListSeatDetailResponse(seats));
        List<ReservationProduct> reservationProducts =
                reservationProductService.fetchByReservationId(reservationId);
        response.setItems(reservationProductMapper.toListProductReservationItemResponse(reservationProducts));
        response.setTicketPrice(ticketPriceService.getTotalTicketPrice(reservation));
        return response;
    }

    @Transactional
    public List<ReservationDetailResponse> getMyReservations() {
        Long userId = authenticationService.getCurrentUserId();
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);
        List<ReservationDetailResponse> responses = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (isExpiredPendingReservation(reservation)) {
                reservation = reservationRepository.findByIdForUpdate(reservation.getId())
                        .orElseThrow(() -> new GeneralException(ResponseCode.RESERVATION_NOT_FOUND));
            }
            expireIfNeeded(reservation);

            ReservationDetailResponse response = reservationMapper.toReservationDetailResponse(reservation);
            List<Seat> seats = seatService.fetchSeatsByReservationId(reservation.getId());
            response.setSeats(seatMapper.toListSeatDetailResponse(seats));
            List<ReservationProduct> reservationProducts =
                    reservationProductService.fetchByReservationId(reservation.getId());
            response.setItems(reservationProductMapper.toListProductReservationItemResponse(reservationProducts));
            response.setTicketPrice(ticketPriceService.getTotalTicketPrice(reservation));
            responses.add(response);
        }
        return responses;
    }

    private void expireIfNeeded(Reservation reservation) {
        if (isExpiredPendingReservation(reservation)) {
            reservation.setStatus(reservationStatusService.fetchByName("EXPIRED"));
            reservationRepository.save(reservation);
            seatHoldService.releaseReservation(reservation.getId());
        }
    }

    private boolean isExpiredPendingReservation(Reservation reservation) {
        return "PENDING".equals(reservation.getStatus().getName())
                && !reservation.getEndTime().isAfter(LocalDateTime.now());
    }
}
