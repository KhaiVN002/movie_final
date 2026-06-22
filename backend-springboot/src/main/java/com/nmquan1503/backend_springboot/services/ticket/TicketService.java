package com.nmquan1503.backend_springboot.services.ticket;

import com.nmquan1503.backend_springboot.dtos.responses.product.ProductTicketItemResponse;
import com.nmquan1503.backend_springboot.dtos.responses.ticket.TicketDetailResponse;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationProduct;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import com.nmquan1503.backend_springboot.entities.ticket.Ticket;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.product.ProductMapper;
import com.nmquan1503.backend_springboot.mappers.showtime.ShowtimeMapper;
import com.nmquan1503.backend_springboot.mappers.theater.SeatMapper;
import com.nmquan1503.backend_springboot.mappers.ticket.TicketMapper;
import com.nmquan1503.backend_springboot.repositories.ticket.TicketRepository;
import com.nmquan1503.backend_springboot.services.authentication.AuthenticationService;
import com.nmquan1503.backend_springboot.services.reservation.ReservationProductService;
import com.nmquan1503.backend_springboot.services.theater.SeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {

    TicketRepository ticketRepository;

    TicketStatusService ticketStatusService;
    SeatService seatService;
    ReservationProductService reservationProductService;
    AuthenticationService authenticationService;

    TicketMapper ticketMapper;
    SeatMapper seatMapper;
    ProductMapper productMapper;

    public void createTicket(Reservation reservation) {
        if (ticketRepository.existsByReservationId(reservation.getId())) {
            return;
        }
        Ticket ticket = Ticket.builder()
                .reservation(reservation)
                .status(ticketStatusService.fetchByName("ISSUED"))
                .build();
        ticketRepository.save(ticket);
    }

    public TicketDetailResponse getTicketDetail(Long ticketId) {
        Long userId = authenticationService.getCurrentUserId();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new GeneralException(ResponseCode.TICKET_NOT_FOUND));
        if (!ticket.getReservation().getUser().getId().equals(userId)) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }

        TicketDetailResponse response = ticketMapper.toTicketDetailResponse(ticket);

        List<Seat> seats = seatService.fetchSeatsByTicketId(ticketId);
        response.setSeats(seatMapper.toListSeatDetailResponse(seats));
        List<ReservationProduct> reservationProducts = reservationProductService.fetchByReservationId(ticket.getReservation().getId());

        List<ProductTicketItemResponse> items = reservationProducts.stream().map(
                reservationProduct -> ProductTicketItemResponse.builder()
                        .product(productMapper.toProductSimpleResponse(reservationProduct.getProduct()))
                        .quantity(reservationProduct.getQuantity())
                        .build()
        ).toList();
        response.setItems(items);
        return response;
    }

    public List<Ticket> fetchByMovieId(Long movieId) {
        return ticketRepository.findByReservationShowtimeMovieId(movieId);
    }

    public List<Ticket> fetchByMovieIdAndTimeWithinPeriod(Long movieId, LocalDateTime from, LocalDateTime to) {
        return ticketRepository.findByReservationShowtimeMovieIdAndReservationStartTimeGreaterThanEqualAndReservationStartTimeLessThan(
                movieId,
                from,
                to
        );
    }

}
