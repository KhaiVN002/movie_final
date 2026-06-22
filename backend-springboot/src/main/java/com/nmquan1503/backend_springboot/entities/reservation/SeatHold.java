package com.nmquan1503.backend_springboot.entities.reservation;

import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "seat_holds",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_seat_holds_showtime_seat", columnNames = {"showtime_id", "seat_id"}),
                @UniqueConstraint(name = "uk_seat_holds_reservation_seat", columnNames = {"reservation_id", "seat_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    Reservation reservation;

    @Column(nullable = false, length = 16)
    String status;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;
}
