package com.nmquan1503.backend_springboot.controllers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminMovieRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminPageResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieDetailResponse;
import com.nmquan1503.backend_springboot.services.admin.AdminQueryService;
import com.nmquan1503.backend_springboot.services.movie.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMovieController {

    private final MovieService movieService;
    private final AdminQueryService adminQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminMovieRow>>> getMovies(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releasedDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getMovies(keyword, page, size, sort, direction)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDetailResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(movieService.getMovieDetailByMovieId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(
            @RequestPart("data") @Valid MovieCreationRequest request,
            @RequestPart(value = "poster", required = false) MultipartFile poster,
            @RequestPart(value = "backdrop", required = false) MultipartFile[] backdrop
    ) {
        movieService.createMovie(request, poster, backdrop);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/quick")
    public ResponseEntity<ApiResponse<MovieDetailResponse>> quickCreate(
            @RequestBody @Valid MovieCreationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(movieService.createMovie(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long id,
            @RequestPart("data") @Valid MovieUpdateRequest request,
            @RequestPart(value = "poster", required = false) MultipartFile poster,
            @RequestPart(value = "backdrop", required = false) MultipartFile[] backdrop
    ) {
        movieService.updateMovie(id, request, poster, backdrop);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
