package com.nmquan1503.backend_springboot.repositories.reservation;

import com.nmquan1503.backend_springboot.entities.reservation.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO seat_holds (
                showtime_id, seat_id, reservation_id, status,
                expires_at, created_at, updated_at
            )
            VALUES (
                :showtimeId, :seatId, :reservationId, 'HELD',
                :expiresAt, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            ON CONFLICT (showtime_id, seat_id)
            DO UPDATE SET
                reservation_id = EXCLUDED.reservation_id,
                status = 'HELD',
                expires_at = EXCLUDED.expires_at,
                updated_at = CURRENT_TIMESTAMP
            WHERE seat_holds.status = 'HELD'
              AND seat_holds.expires_at <= CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int claimSeat(
            @Param("showtimeId") Long showtimeId,
            @Param("seatId") Long seatId,
            @Param("reservationId") Long reservationId,
            @Param("expiresAt") LocalDateTime expiresAt
    );

    @Query(value = """
            SELECT seat_id
            FROM seat_holds
            WHERE showtime_id = :showtimeId
              AND (
                    status = 'BOOKED'
                    OR (status = 'HELD' AND expires_at > CURRENT_TIMESTAMP)
              )
            """, nativeQuery = true)
    List<Long> findActiveSeatIds(@Param("showtimeId") Long showtimeId);

    @Modifying
    @Query(value = """
            UPDATE seat_holds
            SET status = 'BOOKED',
                updated_at = CURRENT_TIMESTAMP
            WHERE reservation_id = :reservationId
              AND status = 'HELD'
            """, nativeQuery = true)
    int markReservationAsBooked(@Param("reservationId") Long reservationId);

    @Modifying
    @Query(value = """
            DELETE FROM seat_holds
            WHERE reservation_id = :reservationId
              AND status = 'HELD'
            """, nativeQuery = true)
    int releaseReservation(@Param("reservationId") Long reservationId);

    @Modifying
    @Query(value = """
            DELETE FROM seat_holds
            WHERE status = 'HELD'
              AND expires_at <= CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int deleteExpiredHolds();
}
