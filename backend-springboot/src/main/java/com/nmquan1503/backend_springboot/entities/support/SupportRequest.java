package com.nmquan1503.backend_springboot.entities.support;

import com.nmquan1503.backend_springboot.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(nullable = false, length = 1000)
    String message;

    @Column(name = "is_read", nullable = false)
    boolean read;

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(name = "read_at")
    LocalDateTime readAt;

    @Column(name = "ip_address", length = 64)
    String ipAddress;

    @Column(name = "user_agent", length = 500)
    String userAgent;
}
