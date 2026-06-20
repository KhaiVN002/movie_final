package com.nmquan1503.backend_springboot.services.ticket;

import com.nmquan1503.backend_springboot.dtos.responses.ticket.TicketPriceResponse;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationSeat;
import com.nmquan1503.backend_springboot.entities.theater.RoomType;
import com.nmquan1503.backend_springboot.entities.theater.SeatType;
import com.nmquan1503.backend_springboot.entities.ticket.TicketPrice;
import com.nmquan1503.backend_springboot.mappers.ticket.TicketMapper;
import com.nmquan1503.backend_springboot.mappers.ticket.TicketPriceMapper;
import com.nmquan1503.backend_springboot.repositories.ticket.TicketPriceRepository;
import com.nmquan1503.backend_springboot.services.reservation.ReservationSeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketPriceService {

    TicketPriceRepository ticketPriceRepository;

    ReservationSeatService reservationSeatService;

    TicketPriceMapper ticketPriceMapper;

    public List<TicketPriceResponse> getAllBaseTicketPrice() {
        return ticketPriceMapper.toListTicketPriceResponse(ticketPriceRepository.findAll());
    }

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public void updateTicketPrice(Integer id, double price) {
        TicketPrice ticketPrice = ticketPriceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket price not found"));
        ticketPrice.setPrice(price);
        ticketPriceRepository.save(ticketPrice);
    }

    public double getTicketPrice(RoomType roomType, SeatType seatType) {
        double currentBasePrice = ticketPriceRepository.findCurrentTicketPrice();
        return currentBasePrice + roomType.getExtraFee() + seatType.getExtraFee();
    }

    public double getTotalTicketPrice(Reservation reservation) {
        List<ReservationSeat> reservationSeats = reservationSeatService.fetchByReservationId(reservation.getId());
        double total = 0.0;
        HashMap<SeatType, Byte> countSeat = new HashMap<>();
        for (ReservationSeat reservationSeat : reservationSeats) {
            SeatType seatType = reservationSeat.getSeat().getType();
            countSeat.put(seatType, (byte)(countSeat.getOrDefault(seatType, (byte)0) + 1));
        }
        double currentBasePrice = ticketPriceRepository.findCurrentTicketPrice();
        double roomTypeExtraFee = reservation.getShowtime().getRoom().getType().getExtraFee();
        for (Map.Entry<SeatType, Byte> entry : countSeat.entrySet()) {
            total += entry.getValue() * (currentBasePrice + entry.getKey().getExtraFee() + roomTypeExtraFee);
        }
        return total;
    }

}
