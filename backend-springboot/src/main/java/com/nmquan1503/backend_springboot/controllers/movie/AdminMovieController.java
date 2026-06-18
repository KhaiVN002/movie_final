package com.nmquan1503.backend_springboot.controllers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieUpdateRequest;
import com.nmquan1503.backend_springboot.services.movie.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;

    // ✅ CREATE (có upload ảnh)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void create(
            @RequestPart("data") @Valid MovieCreationRequest request,
            @RequestPart(value = "poster", required = false) MultipartFile poster,
            @RequestPart(value = "backdrop", required = false) MultipartFile backdrop
    ) {
        movieService.createMovie(request, poster, backdrop);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void update(
            @PathVariable Long id,
            @RequestBody MovieUpdateRequest request
    ) {
        movieService.updateMovie(id, request);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}