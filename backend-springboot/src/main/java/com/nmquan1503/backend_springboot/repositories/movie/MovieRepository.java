package com.nmquan1503.backend_springboot.repositories.movie;

import com.nmquan1503.backend_springboot.entities.movie.Movie;
import com.nmquan1503.backend_springboot.repositories.movie.custom.CustomMovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long>, CustomMovieRepository {

    @Query(
            value = """
                    SELECT movie.*
                    FROM movies movie
                    WHERE (
                        COALESCE(TRIM(:query), '') = ''
                        OR unaccent_immutable(LOWER(movie.title))
                            LIKE '%' || unaccent_immutable(LOWER(TRIM(:query))) || '%'
                    )
                    AND (
                        :categoryId IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM movie_category movie_category
                            WHERE movie_category.movie_id = movie.id
                              AND movie_category.category_id = :categoryId
                        )
                    )
                    ORDER BY
                        CASE
                            WHEN COALESCE(TRIM(:query), '') = '' THEN 3
                            WHEN unaccent_immutable(LOWER(movie.title)) = unaccent_immutable(LOWER(TRIM(:query))) THEN 0
                            WHEN unaccent_immutable(LOWER(movie.title)) LIKE unaccent_immutable(LOWER(TRIM(:query))) || '%' THEN 1
                            ELSE 2
                        END,
                        movie.released_date DESC,
                        movie.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM movies movie
                    WHERE (
                        COALESCE(TRIM(:query), '') = ''
                        OR unaccent_immutable(LOWER(movie.title))
                            LIKE '%' || unaccent_immutable(LOWER(TRIM(:query))) || '%'
                    )
                    AND (
                        :categoryId IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM movie_category movie_category
                            WHERE movie_category.movie_id = movie.id
                              AND movie_category.category_id = :categoryId
                        )
                    )
                    """,
            nativeQuery = true
    )
    Page<Movie> searchCatalog(
            @Param("query") String query,
            @Param("categoryId") Byte categoryId,
            Pageable pageable
    );
}
