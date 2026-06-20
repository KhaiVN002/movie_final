package com.nmquan1503.backend_springboot.repositories.showtime;

import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.repositories.showtime.custom.CustomShowtimeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long>, CustomShowtimeRepository {

    List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime from, LocalDateTime to);

    List<Showtime> findByMovieIdAndStartTimeGreaterThanEqual(Long movieId, LocalDateTime from);

    List<Showtime> findByMovieIdAndStartTimeGreaterThanEqualAndStartTimeLessThan(Long movieId, LocalDateTime from, LocalDateTime to);

    org.springframework.data.domain.Page<Showtime> findByMovieTitleContainingIgnoreCase(String title, org.springframework.data.domain.Pageable pageable);

    List<Showtime> findByMovieTitleContainingIgnoreCase(String title);

    List<Showtime> findByMovieId(Long movieId);

}
