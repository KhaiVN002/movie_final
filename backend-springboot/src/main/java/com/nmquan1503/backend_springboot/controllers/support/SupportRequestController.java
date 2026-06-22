package com.nmquan1503.backend_springboot.controllers.support;

import com.nmquan1503.backend_springboot.dtos.requests.support.SupportRequestCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.services.support.SupportRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support-requests")
@RequiredArgsConstructor
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @Valid @RequestBody SupportRequestCreationRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                supportRequestService.create(request, httpRequest)
        ));
    }
}
