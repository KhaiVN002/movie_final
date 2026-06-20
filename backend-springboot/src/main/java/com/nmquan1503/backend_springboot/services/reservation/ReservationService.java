package com.nmquan1503.backend_springboot.services.reservation;

import com.nmquan1503.backend_springboot.dtos.requests.product.ProductOrderRequest;
import com.nmquan1503.backend_springboot.dtos.requests.reservation.ReservationCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.product.ProductReservationItemResponse;
import com.nmquan1503.backend_springboot.dtos.responses.reservation.ReservationDetailResponse;
import com.nmquan1503.backend_springboot.entities.product.Product;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationProduct;
import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import com.nmquan1503.backend_springboot.entities.user.User;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.product.ProductMapper;
import com.nmquan1503.backend_springboot.mappers.reservation.ReservationMapper;
import com.nmquan1503.backend_springboot.mappers.reservation.ReservationProductMapper;
import com.nmquan1503.backend_springboot.mappers.theater.SeatMapper;
import com.nmquan1503.backend_springboot.repositories.reservation.ReservationRepository;
import com.nmquan1503.backend_springboot.services.authentication.AuthenticationService;
import com.nmquan1503.backend_springboot.services.product.BranchProductService;
import com.nmquan1503.backend_springboot.services.product.ProductService;
import com.nmquan1503.backend_springboot.services.showtime.ShowtimeService;
import com.nmquan1503.backend_springboot.services.theater.SeatService;
import com.nmquan1503.backend_springboot.services.ticket.TicketPriceService;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationService {

    ReservationRepository reservationRepository;

    ShowtimeService showtimeService;
    AuthenticationService authenticationService;
    ReservationStatusService reservationStatusService;
    SeatService seatService;
    ReservationSeatService reservationSeatService;
    ProductService productService;
    ReservationProductService reservationProductService;
    TicketPriceService ticketPriceService;
    BranchProductService branchProductService;

    ReservationMapper reservationMapper;
    SeatMapper seatMapper;
    ProductMapper productMapper;
    ReservationProductMapper reservationProductMapper;

    EntityManager entityManager;

    static Integer count = 0;

    ConcurrentHashMap<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();

    private int hash(Long showtimeId) {
        showtimeId = (showtimeId ^ (showtimeId >>> 30)) * 0xbf58476d1ce4e5b9L;
        showtimeId = (showtimeId ^ (showtimeId >>> 27)) * 0x94d049bb133111ebL;
        showtimeId = showtimeId ^ (showtimeId >>> 31);
        return (int)(showtimeId % 50);
    }

    public Long createReservation(ReservationCreationRequest request) {
        int index = hash(request.getShowtimeId());
        ReentrantLock lock = locks.computeIfAbsent(index, id -> new ReentrantLock());

        lock.lock();

        try {
            System.out.println("Start:"+ count);
            count +=1;

            return processCreateReservation(request);
        }
        finally {
            System.out.println("Count:"+count);
            lock.unlock();
        }
    }

    @Transactional
    private Long processCreateReservation(ReservationCreationRequest request) {
        Long userId = authenticationService.getCurrentUserId();
//            Long userId = (long)3;

        Showtime showtime = showtimeService.fetchById(request.getShowtimeId());
        if (!showtime.getStatus().getName().equals("AVAILABLE")) {
            throw new GeneralException(ResponseCode.SHOWTIME_NOT_AVAILABLE);
        }

        List<Seat> seats = seatService.fetchByIds(request.getSeatIds());
        Set<Long> lockedSeatIds = seatService.getLockedSeatsByShowtimeId(request.getShowtimeId());
        for (Seat seat : seats) {
            if (!seat.getRoom().getId().equals(showtime.getRoom().getId())) {
                throw new GeneralException(ResponseCode.SEAT_NOT_FOUND);
            }
            if (lockedSeatIds.contains(seat.getId())) {
                throw new GeneralException(ResponseCode.SEAT_NOT_AVAILABLE);
            }
        }
//        if (!seatService.lockListSeats(request.getShowtimeId(), request.getSeatIds(), userId)) {
//            throw new GeneralException(ResponseCode.SEAT_NOT_AVAILABLE);
//        }

        Reservation reservation = Reservation.builder()
                .showtime(showtimeService.fetchById(request.getShowtimeId()))
                .user(User.builder().id(userId).build())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(5))
                .status(reservationStatusService.fetchByName("PENDING"))
                .build();
        reservation = reservationRepository.save(reservation);

        reservationSeatService.save(reservation, seats);
//
//        ReservationDetailResponse response = reservationMapper.toReservationDetailResponse(reservation);
//        response.setSeats(seatMapper.toListSeatDetailResponse(seats));
//
//        response.setTicketPrice(ticketPriceService.getTotalTicketPrice(reservation));

        if (request.getProductOrders() != null) {
            List<Byte> productIds = request.getProductOrders().stream().map(
                    ProductOrderRequest::getProductId
            ).toList();
//            List<Product> products = productService.fetchByIds(productIds);
            if (!branchProductService.existsByBranchIdAndProductIds(
                    showtime.getRoom().getBranch().getId(),
                    productIds
            )) {
                throw new GeneralException(ResponseCode.PRODUCT_NOT_FOUND);
            }

            reservationProductService.save(reservation, request.getProductOrders());
//
//            List<ProductReservationItemResponse> items = new ArrayList<>();
//            for (Product product : products) {
//                for (ProductOrderRequest productOrder : request.getProductOrders()) {
//                    if (product.getId().equals(productOrder.getProductId())) {
//                        items.add(ProductReservationItemResponse.builder()
//                                .product(productMapper.toProductCheckoutResponse(product))
//                                .quantity(productOrder.getQuantity())
//                                .build()
//                        );
//                    }
//                }
//            }
//            response.setItems(items);
        }

        return reservation.getId();
    }

    public Reservation fetchById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GeneralException(ResponseCode.RESERVATION_NOT_FOUND));
        if (reservation.getEndTime().isBefore(LocalDateTime.now()) && reservation.getStatus().getName().equals("PENDING")) {
            reservation.setStatus(reservationStatusService.fetchByName("EXPIRED"));
            reservation = reservationRepository.save(reservation);
        }
        return reservation;
    }

    public double getTotalAmountByReservation(Reservation reservation) {
        double total = ticketPriceService.getTotalTicketPrice(reservation);
        List<ReservationProduct> reservationProducts = reservationProductService.fetchByReservationId(reservation.getId());
        for (ReservationProduct reservationProduct : reservationProducts) {
            total += reservationProduct.getQuantity() * reservationProduct.getProduct().getPrice();
        }
        return total;
    }

    public void markReservationAsPaid(Reservation reservation) {
        reservation.setStatus(reservationStatusService.fetchByName("PAID"));
        reservationRepository.save(reservation);
    }

    public ReservationDetailResponse getReservationDetail(Long reservationId) {
        Long userId = authenticationService.getCurrentUserId();
        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(() -> new GeneralException(ResponseCode.RESERVATION_NOT_FOUND));
        ReservationDetailResponse response = reservationMapper.toReservationDetailResponse(reservation);
        List<Seat> seats = seatService.fetchSeatsByReservationId(reservationId);
        response.setSeats(seatMapper.toListSeatDetailResponse(seats));
        List<ReservationProduct> reservationProducts = reservationProductService.fetchByReservationId(reservationId);
        response.setItems(reservationProductMapper.toListProductReservationItemResponse(reservationProducts));
        response.setTicketPrice(ticketPriceService.getTotalTicketPrice(reservation));
        return response;
    }

    public List<ReservationDetailResponse> getMyReservations() {
        Long userId = authenticationService.getCurrentUserId();
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);
        List<ReservationDetailResponse> responses = new ArrayList<>();
        for (Reservation reservation : reservations) {
            // Check expiry for pending reservations
            if (reservation.getEndTime().isBefore(LocalDateTime.now()) && reservation.getStatus().getName().equals("PENDING")) {
                reservation.setStatus(reservationStatusService.fetchByName("EXPIRED"));
                reservationRepository.save(reservation);
            }
            ReservationDetailResponse resDto = reservationMapper.toReservationDetailResponse(reservation);
            List<Seat> seats = seatService.fetchSeatsByReservationId(reservation.getId());
            resDto.setSeats(seatMapper.toListSeatDetailResponse(seats));
            List<ReservationProduct> reservationProducts = reservationProductService.fetchByReservationId(reservation.getId());
            resDto.setItems(reservationProductMapper.toListProductReservationItemResponse(reservationProducts));
            resDto.setTicketPrice(ticketPriceService.getTotalTicketPrice(reservation));
            responses.add(resDto);
        }
        return responses;
    }

}
