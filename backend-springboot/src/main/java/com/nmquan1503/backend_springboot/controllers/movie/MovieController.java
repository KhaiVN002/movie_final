package com.nmquan1503.backend_springboot.controllers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieBannerResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieListItemResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MoviePreviewResponse;
import com.nmquan1503.backend_springboot.services.movie.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {

    MovieService movieService;

    @GetMapping("/previews/trending")
    ResponseEntity<ApiResponse<List<MoviePreviewResponse>>> getTrendingMoviePreviews() {
        ApiResponse<List<MoviePreviewResponse>> response = ApiResponse.success(movieService.getTrendingMoviePreviews());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/list/all")
    ResponseEntity<ApiResponse<Page<MovieListItemResponse>>> getMovieListItems(
            Pageable pageable
    ) {
        ApiResponse<Page<MovieListItemResponse>> response = ApiResponse.success(
                movieService.getMovieListItems(pageable)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/catalog/search")
    ResponseEntity<ApiResponse<Page<MovieListItemResponse>>> searchMovieCatalog(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(required = false) Byte categoryId,
            Pageable pageable
    ) {
        ApiResponse<Page<MovieListItemResponse>> response = ApiResponse.success(
                movieService.searchMovieCatalog(query, categoryId, pageable)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/now-showing")
    ResponseEntity<ApiResponse<Page<MovieListItemResponse>>> getNowShowingMovieListItems(
            Pageable pageable
    ) {
        ApiResponse<Page<MovieListItemResponse>> response = ApiResponse.success(
                movieService.getNowShowingMovieListItems(pageable)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/list/upcoming")
    ResponseEntity<ApiResponse<Page<MovieListItemResponse>>> getUpComingMovieListItem(
            Pageable pageable
    ) {
        ApiResponse<Page<MovieListItemResponse>> response = ApiResponse.success(
                movieService.getUpComingMovieListItems(pageable)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/details/{movieId}")
    ResponseEntity<ApiResponse<MovieDetailResponse>> getMovieDetailByMovieId(
            @PathVariable Long movieId
    ) {
        ApiResponse<MovieDetailResponse> response = ApiResponse.success(
                movieService.getMovieDetailByMovieId(movieId)
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    ResponseEntity<ApiResponse<MovieDetailResponse>> createMovie(
            @RequestBody MovieCreationRequest request
    ) {
        ApiResponse<MovieDetailResponse> response = ApiResponse.success(movieService.createMovie(request));
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{movieId}")
    ResponseEntity<ApiResponse<MovieDetailResponse>> updateMovie(
            @PathVariable Long movieId,
            @RequestBody MovieUpdateRequest request
    ) {
        ApiResponse<MovieDetailResponse> response = ApiResponse.success(
                movieService.updateMovie(movieId, request)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/banner/{movieId}")
    ResponseEntity<ApiResponse<MovieBannerResponse>> getMovieBanner(
            @PathVariable Long movieId
    ) {
        ApiResponse<MovieBannerResponse> response = ApiResponse.success(
                movieService.getMovieBanner(movieId)
        );
        return ResponseEntity.ok().body(response);
    }

}
