package com.nmquan1503.backend_springboot.repositories.reservation;

import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

    java.util.List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT r.showtime.movie.id FROM Reservation r")
    java.util.List<Long> findMovieIdsWithReservations();

    boolean existsByShowtimeId(Long showtimeId);
}
