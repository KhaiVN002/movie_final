package com.nmquan1503.backend_springboot.services.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieImageResponse;
import com.nmquan1503.backend_springboot.mappers.movie.MovieImageMapper;
import com.nmquan1503.backend_springboot.repositories.movie.MovieImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieImageService {

    MovieImageRepository movieImageRepository;

    MovieImageMapper movieImageMapper;

    public Page<MovieImageResponse> getMovieImageByMovieId(Long movieId, Pageable pageable) {
        return movieImageRepository.findByMovieId(movieId, pageable)
                .map(img -> {
                    MovieImageResponse res = movieImageMapper.toMovieImageResponse(img);
                    res.setImageURL(resolveImageUrl(res.getImageURL()));
                    return res;
                });
    }

    private String resolveImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String path = url;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try {
            org.springframework.web.servlet.support.ServletUriComponentsBuilder builder = 
                org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath();
            return builder.path(path).toUriString();
        } catch (Exception e) {
            return "http://localhost:8080/api" + path;
        }
    }

}
