package com.nmquan1503.backend_springboot.services.reservation;

import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.repositories.reservation.SeatHoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;

    public void claimSeats(
            Long showtimeId,
            Long reservationId,
            List<Long> seatIds,
            LocalDateTime expiresAt
    ) {
        List<Long> sortedSeatIds = seatIds.stream().sorted().toList();
        for (Long seatId : sortedSeatIds) {
            if (seatHoldRepository.claimSeat(showtimeId, seatId, reservationId, expiresAt) != 1) {
                throw new GeneralException(ResponseCode.SEAT_NOT_AVAILABLE);
            }
        }
    }

    public List<Long> getActiveSeatIds(Long showtimeId) {
        return seatHoldRepository.findActiveSeatIds(showtimeId);
    }

    public void markReservationAsBooked(Long reservationId) {
        seatHoldRepository.markReservationAsBooked(reservationId);
    }

    public void releaseReservation(Long reservationId) {
        seatHoldRepository.releaseReservation(reservationId);
    }

    public void cleanupExpiredHolds() {
        seatHoldRepository.deleteExpiredHolds();
    }
}
