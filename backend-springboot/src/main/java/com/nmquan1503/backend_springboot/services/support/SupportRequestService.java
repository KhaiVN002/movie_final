package com.nmquan1503.backend_springboot.services.support;

import com.nmquan1503.backend_springboot.dtos.requests.support.SupportRequestCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.support.SupportRequestResponse;
import com.nmquan1503.backend_springboot.entities.support.SupportRequest;
import com.nmquan1503.backend_springboot.entities.user.User;
import com.nmquan1503.backend_springboot.repositories.support.SupportRequestRepository;
import com.nmquan1503.backend_springboot.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final JwtUtil jwtUtil;

    public Long create(SupportRequestCreationRequest request, HttpServletRequest httpRequest) {
        Long userId = getOptionalUserId(httpRequest);
        SupportRequest supportRequest = SupportRequest.builder()
                .user(userId == null ? null : User.builder().id(userId).build())
                .message(request.getMessage().trim())
                .read(false)
                .createdAt(LocalDateTime.now())
                .ipAddress(resolveIpAddress(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build();
        return supportRequestRepository.save(supportRequest).getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<SupportRequestResponse> getRequests(Boolean unreadOnly, Pageable pageable) {
        Page<SupportRequest> page = Boolean.TRUE.equals(unreadOnly)
                ? supportRequestRepository.findByReadOrderByCreatedAtDesc(false, pageable)
                : supportRequestRepository.findAllByOrderByCreatedAtDesc(pageable);
        return page.map(this::toResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long countUnread() {
        return supportRequestRepository.countByReadFalse();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void markAsRead(Long id) {
        supportRequestRepository.findById(id).ifPresent(request -> {
            request.setRead(true);
            request.setReadAt(LocalDateTime.now());
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void markAllAsRead() {
        LocalDateTime now = LocalDateTime.now();
        supportRequestRepository.findByReadOrderByCreatedAtDesc(false, Pageable.unpaged())
                .forEach(request -> {
                    request.setRead(true);
                    request.setReadAt(now);
                });
    }

    private SupportRequestResponse toResponse(SupportRequest request) {
        User user = request.getUser();
        return SupportRequestResponse.builder()
                .id(request.getId())
                .userId(user == null ? null : user.getId())
                .userName(user == null ? "Khách" : user.getFullName())
                .userEmail(user == null ? null : user.getEmail())
                .message(request.getMessage())
                .read(request.isRead())
                .createdAt(request.getCreatedAt())
                .readAt(request.getReadAt())
                .build();
    }

    private Long getOptionalUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.verifyToken(token)) {
                return null;
            }
            return Long.valueOf(jwtUtil.getClaimSetFromToken(token).getSubject());
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String resolveIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        return forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
    }
}
