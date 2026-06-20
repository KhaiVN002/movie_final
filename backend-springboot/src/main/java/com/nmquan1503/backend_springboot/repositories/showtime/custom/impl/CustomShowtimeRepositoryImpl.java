package com.nmquan1503.backend_springboot.repositories.showtime.custom.impl;

import com.nmquan1503.backend_springboot.repositories.showtime.custom.CustomShowtimeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomShowtimeRepositoryImpl implements CustomShowtimeRepository {

    JPAQueryFactory queryFactory;
    EntityManager entityManager;

    @Override
    public boolean isScheduleConflict(Integer roomId, LocalDateTime startTime, Short duration) {
        String sql = """
            SELECT COUNT(*)
            FROM showtimes s
            JOIN movies m ON m.id = s.movie_id
            WHERE s.room_id = :roomId
              AND s.start_time + (m.duration || ' minutes')::interval >= CAST(:startTime AS timestamp)
              AND s.start_time <= CAST(:startTime AS timestamp) + (:duration || ' minutes')::interval
            """;

        Number count = (Number) entityManager.createNativeQuery(sql)
                .setParameter("roomId", roomId)
                .setParameter("startTime", startTime)
                .setParameter("duration", duration)
                .getSingleResult();

        return count != null && count.longValue() > 0;
    }
}
