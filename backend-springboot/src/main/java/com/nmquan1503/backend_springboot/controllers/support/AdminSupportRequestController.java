package com.nmquan1503.backend_springboot.controllers.support;

import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.support.SupportRequestResponse;
import com.nmquan1503.backend_springboot.services.support.SupportRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/support-requests")
@RequiredArgsConstructor
public class AdminSupportRequestController {

    private final SupportRequestService supportRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupportRequestResponse>>> getRequests(
            @RequestParam(required = false) Boolean unreadOnly,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                supportRequestService.getRequests(unreadOnly, pageable)
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> countUnread() {
        return ResponseEntity.ok(ApiResponse.success(supportRequestService.countUnread()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        supportRequestService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        supportRequestService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
