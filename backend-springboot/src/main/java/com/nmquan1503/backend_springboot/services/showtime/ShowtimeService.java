package com.nmquan1503.backend_springboot.services.showtime;

import com.nmquan1503.backend_springboot.dtos.requests.showtime.ShowtimeCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.showtime.ShowtimeUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeOptionResponse;
import com.nmquan1503.backend_springboot.entities.movie.Movie;
import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.entities.theater.Room;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.showtime.ShowtimeMapper;
import com.nmquan1503.backend_springboot.repositories.showtime.ShowtimeRepository;
import com.nmquan1503.backend_springboot.services.movie.MovieService;
import com.nmquan1503.backend_springboot.services.theater.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeService {

    ShowtimeRepository showtimeRepository;

    MovieService movieService;
    RoomService roomService;
    ShowtimeStatusService showtimeStatusService;
    com.nmquan1503.backend_springboot.repositories.reservation.ReservationRepository reservationRepository;

    ShowtimeMapper showtimeMapper;

    public ShowtimeDetailResponse getShowtimeDetail(Long showtimeId) {
        return showtimeMapper.toShowtimeDetailResponse(fetchById(showtimeId));
    }

    public org.springframework.data.domain.Page<ShowtimeDetailResponse> getAllShowtimes(String search, org.springframework.data.domain.Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return showtimeRepository.findByMovieTitleContainingIgnoreCase(search.trim(), pageable)
                    .map(showtimeMapper::toShowtimeDetailResponse);
        }
        return showtimeRepository.findAll(pageable)
                .map(showtimeMapper::toShowtimeDetailResponse);
    }

    public List<ShowtimeOptionResponse> getShowtimeOptionsByMovieId(Long movieId) {
        List<Showtime> showtimes = showtimeRepository.findByMovieIdAndStartTimeBetween(
                movieId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(14)
        );
        return showtimes.stream().map(
                showtimeMapper::toShowtimeSummaryResponse
        ).toList();
    }

    public Showtime fetchById(Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new GeneralException(ResponseCode.SHOWTIME_NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createShowtime(ShowtimeCreationRequest request) {
        if (!roomService.existsByRoomId(request.getRoomId())) {
            throw new GeneralException(ResponseCode.ROOM_NOT_FOUND);
        }
        Movie movie = movieService.fetchByMovieId(request.getMovieId());
        if (showtimeRepository.isScheduleConflict(request.getRoomId(), request.getStartTime(), movie.getDuration())) {
            throw new GeneralException(ResponseCode.SCHEDULE_CONFLICT);
        }
        Showtime showtime = Showtime.builder()
                .movie(movie)
                .room(Room.builder().id(request.getRoomId()).build())
                .startTime(request.getStartTime())
                .status(showtimeStatusService.fetchByName("AVAILABLE"))
                .build();
        showtimeRepository.save(showtime);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateShowtime(Long showtimeId, ShowtimeUpdateRequest request) {
        Showtime showtime = fetchById(showtimeId);
        if (!showtimeStatusService.existsByShowtimeStatusId(request.getShowtimeStatusId())) {
            throw new GeneralException(ResponseCode.SHOWTIME_STATUS_NOT_FOUND);
        }
        showtime.setStatus(showtimeStatusService.fetchById(request.getShowtimeStatusId()));
        showtimeRepository.save(showtime);
    }

    public List<Showtime> fetchUpcomingShowtimeByMovieId(Long movieId) {
        return showtimeRepository.findByMovieIdAndStartTimeGreaterThanEqual(movieId, LocalDateTime.now());
    }

    public List<Showtime> fetchByMovieIdAndStartTimeGreaterThanEqual(Long movieId, LocalDateTime from) {
        return showtimeRepository.findByMovieIdAndStartTimeGreaterThanEqual(movieId, from);
    }

    public List<Showtime> fetchByMovieIdAndStartTimeWithinPeriod(Long movieId, LocalDateTime from, LocalDateTime to) {
        return showtimeRepository.findByMovieIdAndStartTimeGreaterThanEqualAndStartTimeLessThan(movieId, from, to);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional
    public void deleteShowtime(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new GeneralException(ResponseCode.SHOWTIME_NOT_FOUND));
        Long movieId = showtime.getMovie().getId();
        List<Showtime> showtimes = showtimeRepository.findByMovieId(movieId);
        for (Showtime s : showtimes) {
            if (reservationRepository.existsByShowtimeId(s.getId())) {
                throw new GeneralException(ResponseCode.SHOWTIME_HAS_BOOKINGS);
            }
        }
        showtimeRepository.deleteAll(showtimes);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional
    public void deleteShowtimesBulk(List<Long> showtimeIds) {
        for (Long showtimeId : showtimeIds) {
            if (!showtimeRepository.existsById(showtimeId)) {
                throw new GeneralException(ResponseCode.SHOWTIME_NOT_FOUND);
            }
            if (reservationRepository.existsByShowtimeId(showtimeId)) {
                throw new GeneralException(ResponseCode.SHOWTIME_HAS_BOOKINGS);
            }
        }
        showtimeRepository.deleteAllById(showtimeIds);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.transaction.annotation.Transactional
    public void deleteShowtimesByMovieTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Tên phim không được để trống");
        }
        List<Showtime> showtimes = showtimeRepository.findByMovieTitleContainingIgnoreCase(title.trim());
        for (Showtime showtime : showtimes) {
            if (reservationRepository.existsByShowtimeId(showtime.getId())) {
                throw new GeneralException(ResponseCode.SHOWTIME_HAS_BOOKINGS);
            }
        }
        showtimeRepository.deleteAll(showtimes);
    }

}
