package com.nmquan1503.backend_springboot.configurations;

import com.nmquan1503.backend_springboot.dtos.internal.MovieReviewStats;
import com.nmquan1503.backend_springboot.dtos.requests.user.UserCreationRequest;
import com.nmquan1503.backend_springboot.entities.location.Ward;
import com.nmquan1503.backend_springboot.entities.movie.Movie;
import com.nmquan1503.backend_springboot.entities.movie.MovieCategory;
import com.nmquan1503.backend_springboot.entities.movie.MovieReview;
// import com.nmquan1503.backend_springboot.entities.movie.MovieViewHistory;
import com.nmquan1503.backend_springboot.entities.payment.PaymentMethod;
import com.nmquan1503.backend_springboot.entities.payment.PaymentTransaction;
import com.nmquan1503.backend_springboot.entities.payment.PaymentTransactionStatus;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationSeat;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationStatus;
import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import com.nmquan1503.backend_springboot.entities.ticket.Ticket;
import com.nmquan1503.backend_springboot.entities.ticket.TicketStatus;
import com.nmquan1503.backend_springboot.entities.user.Gender;
import com.nmquan1503.backend_springboot.entities.user.User;
import com.nmquan1503.backend_springboot.repositories.movie.MovieCategoryRepository;
import com.nmquan1503.backend_springboot.repositories.movie.MovieRepository;
import com.nmquan1503.backend_springboot.repositories.movie.MovieReviewRepository;
// import com.nmquan1503.backend_springboot.repositories.movie.MovieViewHistoryRepository;
import com.nmquan1503.backend_springboot.repositories.payment.PaymentTransactionRepository;
import com.nmquan1503.backend_springboot.repositories.reservation.ReservationRepository;
import com.nmquan1503.backend_springboot.repositories.reservation.ReservationSeatRepository;
import com.nmquan1503.backend_springboot.repositories.showtime.ShowtimeRepository;
import com.nmquan1503.backend_springboot.repositories.theater.SeatRepository;
import com.nmquan1503.backend_springboot.repositories.ticket.TicketRepository;
import com.nmquan1503.backend_springboot.repositories.user.UserRepository;
// import com.nmquan1503.backend_springboot.services.ApplicationInitService;
import com.nmquan1503.backend_springboot.services.movie.MovieReviewService;
// import com.nmquan1503.backend_springboot.services.movie.MovieScoreService;
import com.nmquan1503.backend_springboot.services.payment.PaymentTransactionStatusService;
import com.nmquan1503.backend_springboot.utils.VnPayUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    // MovieScoreService movieScoreService;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
//            movieScoreService.updateScore();
        };
    }

}
