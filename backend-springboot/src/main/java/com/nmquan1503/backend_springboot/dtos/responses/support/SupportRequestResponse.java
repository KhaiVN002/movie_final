package com.nmquan1503.backend_springboot.dtos.responses.support;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupportRequestResponse {
    Long id;
    Long userId;
    String userName;
    String userEmail;
    String message;
    boolean read;
    LocalDateTime createdAt;
    LocalDateTime readAt;
}
