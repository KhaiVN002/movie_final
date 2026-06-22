package com.nmquan1503.backend_springboot.repositories.ticket;

import com.nmquan1503.backend_springboot.entities.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByReservationId(Long reservationId);

    List<Ticket> findByReservationShowtimeMovieId(Long movieId);

    List<Ticket> findByReservationShowtimeMovieIdAndReservationStartTimeGreaterThanEqualAndReservationStartTimeLessThan(
            Long movieId,
            LocalDateTime from,
            LocalDateTime to
    );

}
