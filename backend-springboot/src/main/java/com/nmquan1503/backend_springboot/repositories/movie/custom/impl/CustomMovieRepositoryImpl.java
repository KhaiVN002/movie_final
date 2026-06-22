package com.nmquan1503.backend_springboot.repositories.movie.custom.impl;

import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieListItemResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MoviePreviewResponse;
import com.nmquan1503.backend_springboot.entities.movie.*;
import com.nmquan1503.backend_springboot.entities.reservation.QReservation;
import com.nmquan1503.backend_springboot.entities.showtime.QShowtime;
import com.nmquan1503.backend_springboot.entities.ticket.QTicket;
import com.nmquan1503.backend_springboot.repositories.movie.custom.CustomMovieRepository;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomMovieRepositoryImpl implements CustomMovieRepository {

    JPAQueryFactory queryFactory;

    @Override
    public List<Movie> findTrendingMovies(int limit) {
        QMovie movie = QMovie.movie;
        QShowtime showtime = QShowtime.showtime;
        List<Movie> movies = queryFactory
            .select(movie)
            .from(movie)
            .join(showtime).on(showtime.movie.id.eq(movie.id))
            .where(showtime.startTime.between(LocalDateTime.now(), LocalDateTime.now().plusDays(14)))
            .groupBy(movie.id)
            .orderBy(showtime.count().desc())
            .limit(limit)
            .fetch();

        return movies;
    }

    @Override
    public Page<Movie> findAllSortByFinalScore(Pageable pageable) {
        QMovie movie = QMovie.movie;
        QShowtime showtime = QShowtime.showtime;

        // Lấy danh sách movie cùng số showtime trong 14 ngày tới
        List<Movie> movies = queryFactory
                .select(movie)
                .from(movie)
                .join(showtime).on(showtime.movie.id.eq(movie.id))
                .where(showtime.startTime.between(LocalDateTime.now(), LocalDateTime.now().plusDays(14)))
                .groupBy(movie.id)
                .orderBy(showtime.count().desc())  // trending = nhiều showtime nhất
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Tính tổng số movie (dùng subquery để đếm movie có showtime)
        Long total = queryFactory
                .select(movie.countDistinct())
                .from(movie)
                .join(showtime).on(showtime.movie.id.eq(movie.id))
                .where(showtime.startTime.between(LocalDateTime.now(), LocalDateTime.now().plusDays(14)))
                .fetchOne();

        total = total == null ? 0 : total;

        return new PageImpl<>(movies, pageable, total);
    }

    @Override
    public Page<Movie> findNowShowingSortByFinalScore(Pageable pageable) {
        QMovie movie = QMovie.movie;
        QShowtime showtime = QShowtime.showtime;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        LocalDateTime oneMonthLater = now.plusMonths(1);

        // Lấy danh sách movie đang chiếu (có showtime trước và sau hiện tại)
        List<Long> movieIds = queryFactory
                .select(showtime.movie.id)
                .from(showtime)
                .where(showtime.startTime.between(oneMonthAgo, oneMonthLater))
                .groupBy(showtime.movie.id)
                .having(
                        Expressions.numberTemplate(Long.class,
                                "SUM(CASE WHEN {0} < {1} THEN 1 ELSE 0 END)", showtime.startTime, now
                        ).gt(0),
                        Expressions.numberTemplate(Long.class,
                                "SUM(CASE WHEN {0} > {1} THEN 1 ELSE 0 END)", showtime.startTime, now
                        ).gt(0)
                )
                .fetch();

        // Lấy movie và sắp xếp theo số lượng showtime sắp tới (trending)
        List<Movie> movies = queryFactory
                .select(movie)
                .from(movie)
                .join(showtime).on(showtime.movie.id.eq(movie.id))
                .where(movie.id.in(movieIds)
                        .and(showtime.startTime.after(now))) // chỉ tính showtime sắp tới
                .groupBy(movie.id)
                .orderBy(showtime.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(movies, pageable, movieIds.size());
    }

    @Override
    public Page<Movie> findUpComingSortByFinalScore(Pageable pageable) {
        QMovie movie = QMovie.movie;

        List<Movie> movies = queryFactory
                .select(movie)
                .from(movie)
                .where(movie.releasedDate.after(java.time.LocalDate.now()))
                .orderBy(movie.releasedDate.asc(), movie.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(movie.count())
                .from(movie)
                .where(movie.releasedDate.after(java.time.LocalDate.now()))
                .fetchOne();

        return new PageImpl<>(movies, pageable, total == null ? 0 : total);
    }

    @Override
    public List<Movie> findAllSortByFinalScore() {
        QMovie movie = QMovie.movie;
        QShowtime showtime = QShowtime.showtime;

        LocalDateTime now = LocalDateTime.now();

        return queryFactory
                .select(movie)
                .from(movie)
                .join(showtime).on(showtime.movie.id.eq(movie.id))
                .where(showtime.startTime.after(now)) // chỉ tính showtime trong tương lai
                .groupBy(movie.id)
                .orderBy(showtime.count().desc()) // xếp theo số lượng showtime
                .fetch();
    }

    @Override
    public List<Long> findIdsByListIds(List<Long> ids) {
        QMovie movie = QMovie.movie;
        return queryFactory
                .select(movie.id)
                .from(movie)
                .where(movie.id.in(ids))
                .fetch();
    }
}
