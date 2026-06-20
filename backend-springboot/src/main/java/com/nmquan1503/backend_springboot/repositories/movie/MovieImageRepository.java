package com.nmquan1503.backend_springboot.repositories.movie;

import com.nmquan1503.backend_springboot.entities.movie.MovieImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {

    Page<MovieImage> findByMovieId(Long movieId, Pageable pageable);
    java.util.List<MovieImage> findByMovieId(Long movieId);
    void deleteByMovieId(Long movieId);

}
