package com.nmquan1503.backend_springboot.repositories.reservation;

import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);

    java.util.List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    @Query("SELECT DISTINCT r.showtime.movie.id FROM Reservation r")
    java.util.List<Long> findMovieIdsWithReservations();

    boolean existsByShowtimeId(Long showtimeId);
}
