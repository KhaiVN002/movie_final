package com.nmquan1503.backend_springboot.controllers.showtime;

import com.nmquan1503.backend_springboot.dtos.requests.showtime.ShowtimeCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.showtime.ShowtimeUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeOptionResponse;
import com.nmquan1503.backend_springboot.services.showtime.ShowtimeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeController {

    ShowtimeService showtimeService;

    @GetMapping("/movie/{movieId}")
    ResponseEntity<ApiResponse<List<ShowtimeOptionResponse>>> getShowtimeOptionsByMovieId(
            @PathVariable Long movieId
    ) {
        ApiResponse<List<ShowtimeOptionResponse>> response = ApiResponse.success(
            showtimeService.getShowtimeOptionsByMovieId(movieId)
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> createShowtime(
            @RequestBody ShowtimeCreationRequest request
    ) {
        showtimeService.createShowtime(request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> updateShowtime(
            @PathVariable Long showtimeId,
            @RequestBody ShowtimeUpdateRequest request
    ) {
        showtimeService.updateShowtime(showtimeId, request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/detail/{showtimeId}")
    ResponseEntity<ApiResponse<ShowtimeDetailResponse>> getShowtimeDetail(
            @PathVariable Long showtimeId
    ) {
        ApiResponse<ShowtimeDetailResponse> response = ApiResponse.success(
                showtimeService.getShowtimeDetail(showtimeId)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ShowtimeDetailResponse>>> getAllShowtimes(
            @RequestParam(value = "search", required = false) String search,
            @org.springframework.data.web.PageableDefault(sort = "startTime", direction = org.springframework.data.domain.Sort.Direction.DESC, size = 10) org.springframework.data.domain.Pageable pageable
    ) {
        ApiResponse<org.springframework.data.domain.Page<ShowtimeDetailResponse>> response = ApiResponse.success(
                showtimeService.getAllShowtimes(search, pageable)
        );
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> deleteShowtime(
            @PathVariable Long showtimeId
    ) {
        showtimeService.deleteShowtime(showtimeId);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> deleteShowtimesBulk(
            @RequestBody List<Long> showtimeIds
    ) {
        showtimeService.deleteShowtimesBulk(showtimeIds);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/by-movie-title")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> deleteShowtimesByMovieTitle(
            @RequestParam("title") String title
    ) {
        showtimeService.deleteShowtimesByMovieTitle(title);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

}

