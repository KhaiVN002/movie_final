package com.nmquan1503.backend_springboot.controllers.admin;

import com.nmquan1503.backend_springboot.dtos.requests.admin.AdminUserRolesUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.BranchProductCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.BranchProductUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.ProductCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.ProductUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.requests.showtime.ShowtimeCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.showtime.ShowtimeUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminBookingRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminBranchProductRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminBranchRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminDashboardResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminLookupGroupResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminPageResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminPaymentRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminProductRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminRoleRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminRoomRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminSeatRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminShowtimeRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminUserRow;
import com.nmquan1503.backend_springboot.services.admin.AdminQueryService;
import com.nmquan1503.backend_springboot.services.product.BranchProductService;
import com.nmquan1503.backend_springboot.services.product.ProductService;
import com.nmquan1503.backend_springboot.services.showtime.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final AdminQueryService adminQueryService;
    private final ShowtimeService showtimeService;
    private final ProductService productService;
    private final BranchProductService branchProductService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminQueryService.getDashboard()));
    }

    @GetMapping("/showtimes")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminShowtimeRow>>> getShowtimes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Short branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getShowtimes(keyword, status, branchId, fromDate, toDate, page, size, sort, direction)
        ));
    }

    @PostMapping("/showtimes")
    public ResponseEntity<ApiResponse<Void>> createShowtime(@RequestBody @Valid ShowtimeCreationRequest request) {
        showtimeService.createShowtime(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/showtimes/{showtimeId}")
    public ResponseEntity<ApiResponse<Void>> updateShowtime(
            @PathVariable Long showtimeId,
            @RequestBody ShowtimeUpdateRequest request
    ) {
        showtimeService.updateShowtime(showtimeId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/showtimes/{showtimeId}")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable Long showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/showtimes/bulk")
    public ResponseEntity<ApiResponse<Void>> deleteShowtimesBulk(@RequestBody List<Long> showtimeIds) {
        showtimeService.deleteShowtimesBulk(showtimeIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/showtimes/by-movie-title")
    public ResponseEntity<ApiResponse<Void>> deleteShowtimesByMovieTitle(@RequestParam("title") String title) {
        showtimeService.deleteShowtimesByMovieTitle(title);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/branches")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminBranchRow>>> getBranches(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getBranches(keyword, status, page, size, sort, direction)
        ));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminRoomRow>>> getRooms(
            @RequestParam(required = false) Short branchId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "branchName") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getRooms(branchId, status, page, size, sort, direction)
        ));
    }

    @GetMapping("/seats")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminSeatRow>>> getSeats(
            @RequestParam(required = false) Short branchId,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "branchName") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getSeats(branchId, roomId, status, page, size, sort, direction)
        ));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminProductRow>>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getProducts(keyword, page, size, sort, direction)
        ));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Void>> createProduct(@RequestBody @Valid ProductCreationRequest request) {
        productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Byte productId,
            @RequestBody ProductUpdateRequest request
    ) {
        productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/product-branches")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminBranchProductRow>>> getBranchProducts(
            @RequestParam(required = false) Short branchId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "branchName") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getBranchProducts(branchId, status, page, size, sort, direction)
        ));
    }

    @PostMapping("/product-branches")
    public ResponseEntity<ApiResponse<Void>> createBranchProduct(@RequestBody @Valid BranchProductCreationRequest request) {
        branchProductService.createBranchProduct(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/product-branches/{branchProductId}")
    public ResponseEntity<ApiResponse<Void>> updateBranchProduct(
            @PathVariable Short branchProductId,
            @RequestBody BranchProductUpdateRequest request
    ) {
        branchProductService.updateBranchProduct(branchProductId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminUserRow>>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Byte roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationTime") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getUsers(keyword, roleId, page, size, sort, direction)
        ));
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<Void>> updateUserRoles(
            @PathVariable Long userId,
            @RequestBody @Valid AdminUserRolesUpdateRequest request
    ) {
        adminQueryService.updateUserRoles(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminRoleRow>>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getRoles(page, size, sort, direction)
        ));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminBookingRow>>> getBookings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookedAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getBookings(keyword, status, fromDate, toDate, page, size, sort, direction)
        ));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminPaymentRow>>> getPayments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminQueryService.getPayments(keyword, status, fromDate, toDate, page, size, sort, direction)
        ));
    }

    @GetMapping("/lookups")
    public ResponseEntity<ApiResponse<List<AdminLookupGroupResponse>>> getLookups() {
        return ResponseEntity.ok(ApiResponse.success(adminQueryService.getLookups()));
    }
}
