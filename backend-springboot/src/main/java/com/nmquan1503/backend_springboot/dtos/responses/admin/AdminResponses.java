package com.nmquan1503.backend_springboot.dtos.responses.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class AdminResponses {

    private AdminResponses() {
    }

    public record AdminPageResponse<T>(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean last
    ) {
    }

    public record AdminDashboardResponse(
            long movieCount,
            long upcomingShowtimeCount,
            long userCount,
            long reservationCount,
            long paidReservationCount,
            BigDecimal revenueToday,
            BigDecimal revenueLast7Days,
            List<AdminPaymentRow> recentPayments
    ) {
    }

    public record AdminMovieRow(
            Long id,
            String title,
            String posterURL,
            LocalDate releasedDate,
            Integer duration,
            String originalLanguage,
            String ageRating,
            Long showtimeCount,
            Long bookingCount,
            Double averageRating,
            Long ratingCount
    ) {
    }

    public record AdminShowtimeRow(
            Long id,
            Long movieId,
            String movieTitle,
            Short branchId,
            String branchName,
            Integer roomId,
            String roomName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String status,
            Long reservationCount
    ) {
    }

    public record AdminBranchRow(
            Short id,
            String name,
            String status,
            String specificAddress,
            String ward,
            String district,
            String province,
            Long roomCount,
            Long seatCount
    ) {
    }

    public record AdminRoomRow(
            Integer id,
            Short branchId,
            String branchName,
            String name,
            Short rowCount,
            Short columnCount,
            String roomType,
            String status,
            Long seatCount
    ) {
    }

    public record AdminSeatRow(
            Long id,
            Integer roomId,
            String roomName,
            Short branchId,
            String branchName,
            String name,
            Short positionX,
            Short positionY,
            String type,
            String status
    ) {
    }

    public record AdminProductRow(
            Byte id,
            String name,
            String thumbnailURL,
            BigDecimal price,
            Long branchCount
    ) {
    }

    public record AdminBranchProductRow(
            Short id,
            Short branchId,
            String branchName,
            Byte productId,
            String productName,
            String thumbnailURL,
            String status
    ) {
    }

    public record AdminUserRow(
            Long id,
            String fullName,
            String avatarURL,
            String email,
            String phone,
            LocalDate birthday,
            String gender,
            LocalDateTime creationTime,
            String roles
    ) {
    }

    public record AdminRoleRow(
            Byte id,
            String name,
            Long userCount,
            Long permissionCount
    ) {
    }

    public record AdminBookingRow(
            Long id,
            String customerName,
            String customerEmail,
            String customerPhone,
            String movieTitle,
            String branchName,
            String roomName,
            LocalDateTime bookedAt,
            LocalDateTime showtimeStart,
            String seats,
            Long productQuantity,
            Long ticketCount,
            String reservationStatus,
            String paymentStatus,
            BigDecimal amount,
            String paymentMethod,
            String transactionId
    ) {
    }

    public record AdminPaymentRow(
            Long id,
            String transactionId,
            Long reservationId,
            String customerName,
            String customerEmail,
            String movieTitle,
            String paymentMethod,
            LocalDateTime time,
            BigDecimal amount,
            String status
    ) {
    }

    public record AdminLookupOptionResponse(
            Integer id,
            String name,
            String code
    ) {
    }

    public record AdminLookupGroupResponse(
            String key,
            String label,
            List<AdminLookupOptionResponse> options
    ) {
    }
}
