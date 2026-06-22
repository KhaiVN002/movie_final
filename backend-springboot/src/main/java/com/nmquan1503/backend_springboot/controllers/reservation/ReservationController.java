package com.nmquan1503.backend_springboot.controllers.reservation;

import com.nmquan1503.backend_springboot.dtos.requests.reservation.ReservationCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.reservation.ReservationDetailResponse;
import com.nmquan1503.backend_springboot.services.reservation.ReservationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationController {

    ReservationService reservationService;

    @PostMapping
    ResponseEntity<ApiResponse<Long>> createReservation(
            @Valid @RequestBody ReservationCreationRequest request
    ) {
        ApiResponse<Long> response = ApiResponse.success(
                reservationService.createReservation(request)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/detail/{reservationId}")
    ResponseEntity<ApiResponse<ReservationDetailResponse>> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        ApiResponse<ReservationDetailResponse> response = ApiResponse.success(
                reservationService.getReservationDetail(reservationId)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/my")
    ResponseEntity<ApiResponse<java.util.List<ReservationDetailResponse>>> getMyReservations() {
        ApiResponse<java.util.List<ReservationDetailResponse>> response = ApiResponse.success(
                reservationService.getMyReservations()
        );
        return ResponseEntity.ok().body(response);
    }

}
