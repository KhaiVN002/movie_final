package com.nmquan1503.backend_springboot.services.admin;

import com.nmquan1503.backend_springboot.dtos.requests.admin.AdminUserRolesUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminBookingRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminBranchProductRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminBranchRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminDashboardResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminLookupGroupResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminLookupOptionResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminMovieRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminPageResponse;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminPaymentRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminProductRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminRoleRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminRoomRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminSeatRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminShowtimeRow;
import com.nmquan1503.backend_springboot.dtos.responses.admin.AdminResponses.AdminUserRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminQueryService {

    private static final int MAX_PAGE_SIZE = 100;
    private final NamedParameterJdbcTemplate jdbc;

    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardResponse getDashboard() {
        Map<String, Object> empty = Map.of();
        long movieCount = queryLong("SELECT COUNT(*) FROM movies", empty);
        long upcomingShowtimeCount = queryLong("SELECT COUNT(*) FROM showtimes WHERE start_time >= CURRENT_TIMESTAMP", empty);
        long userCount = queryLong("SELECT COUNT(*) FROM users", empty);
        long reservationCount = queryLong("SELECT COUNT(*) FROM reservations", empty);
        long paidReservationCount = queryLong("""
                SELECT COUNT(*)
                FROM reservations r
                JOIN reservation_status rs ON rs.id = r.status_id
                WHERE rs.name = 'PAID'
                """, empty);
        BigDecimal revenueToday = queryBigDecimal("""
                SELECT COALESCE(SUM(pt.amount), 0)
                FROM payment_transactions pt
                JOIN payment_transaction_status pts ON pts.id = pt.status_id
                WHERE pts.name = 'SUCCESS'
                  AND pt.time::date = CURRENT_DATE
                """, empty);
        BigDecimal revenueLast7Days = queryBigDecimal("""
                SELECT COALESCE(SUM(pt.amount), 0)
                FROM payment_transactions pt
                JOIN payment_transaction_status pts ON pts.id = pt.status_id
                WHERE pts.name = 'SUCCESS'
                  AND pt.time >= CURRENT_TIMESTAMP - INTERVAL '7 days'
                """, empty);
        List<AdminPaymentRow> recentPayments = jdbc.query("""
                SELECT pt.id, pt.transaction_id, pt.reservation_id, u.full_name AS customer_name,
                       u.email AS customer_email, m.title AS movie_title, pm.name AS payment_method,
                       pt.time, pt.amount, pts.name AS status
                FROM payment_transactions pt
                JOIN reservations rv ON rv.id = pt.reservation_id
                JOIN users u ON u.id = rv.user_id
                JOIN showtimes st ON st.id = rv.showtime_id
                JOIN movies m ON m.id = st.movie_id
                JOIN payment_methods pm ON pm.id = pt.payment_method_id
                JOIN payment_transaction_status pts ON pts.id = pt.status_id
                ORDER BY pt.time DESC
                LIMIT 5
                """, paymentMapper());
        return new AdminDashboardResponse(
                movieCount,
                upcomingShowtimeCount,
                userCount,
                reservationCount,
                paidReservationCount,
                revenueToday,
                revenueLast7Days,
                recentPayments
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminMovieRow> getMovies(String keyword, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append(" AND LOWER(m.title) LIKE :keyword ");
            params.put("keyword", like(keyword));
        }

        String from = """
                FROM movies m
                JOIN languages l ON l.id = m.original_language_id
                LEFT JOIN age_ratings ar ON ar.id = m.age_rating_id
                LEFT JOIN (
                    SELECT movie_id, COUNT(*) AS showtime_count
                    FROM showtimes
                    GROUP BY movie_id
                ) st ON st.movie_id = m.id
                LEFT JOIN (
                    SELECT s.movie_id, COUNT(r.id) AS booking_count
                    FROM showtimes s
                    JOIN reservations r ON r.showtime_id = s.id
                    GROUP BY s.movie_id
                ) bk ON bk.movie_id = m.id
                LEFT JOIN (
                    SELECT movie_id, AVG(rating)::float AS average_rating, COUNT(*) AS rating_count
                    FROM movie_reviews
                    GROUP BY movie_id
                ) rv ON rv.movie_id = m.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "title", "LOWER(m.title)",
                "releasedDate", "m.released_date",
                "duration", "m.duration",
                "showtimeCount", "COALESCE(st.showtime_count, 0)",
                "bookingCount", "COALESCE(bk.booking_count, 0)"
        );
        String orderBy = orderBy(sorts, sort, direction, "m.released_date", "DESC");
        return page("""
                SELECT m.id, m.title, m.poster_url, m.released_date, m.duration,
                       l.name AS original_language, ar.code AS age_rating,
                       COALESCE(st.showtime_count, 0) AS showtime_count,
                       COALESCE(bk.booking_count, 0) AS booking_count,
                       COALESCE(rv.average_rating, 0) AS average_rating,
                       COALESCE(rv.rating_count, 0) AS rating_count
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy,
                page,
                size,
                movieMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminShowtimeRow> getShowtimes(
            String keyword,
            String status,
            Short branchId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size,
            String sort,
            String direction
    ) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append(" AND LOWER(m.title) LIKE :keyword ");
            params.put("keyword", like(keyword));
        }
        if (hasText(status)) {
            where.append(" AND ss.name = :status ");
            params.put("status", status);
        }
        if (branchId != null) {
            where.append(" AND b.id = :branchId ");
            params.put("branchId", branchId);
        }
        LocalDateTime effectiveFrom = fromDate != null ? fromDate : LocalDateTime.now();
        where.append(" AND s.start_time >= :fromDate ");
        params.put("fromDate", effectiveFrom);
        if (toDate != null) {
            where.append(" AND s.start_time <= :toDate ");
            params.put("toDate", toDate);
        }

        String from = """
                FROM showtimes s
                JOIN movies m ON m.id = s.movie_id
                JOIN rooms r ON r.id = s.room_id
                JOIN branches b ON b.id = r.branch_id
                JOIN showtime_status ss ON ss.id = s.status_id
                LEFT JOIN (
                    SELECT showtime_id, COUNT(*) AS reservation_count
                    FROM reservations
                    GROUP BY showtime_id
                ) rv ON rv.showtime_id = s.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "startTime", "s.start_time",
                "movieTitle", "LOWER(m.title)",
                "branchName", "LOWER(b.name)",
                "roomName", "LOWER(r.name)",
                "status", "ss.name",
                "reservationCount", "COALESCE(rv.reservation_count, 0)"
        );
        return page("""
                SELECT s.id, s.movie_id, m.title AS movie_title, b.id AS branch_id, b.name AS branch_name,
                       r.id AS room_id, r.name AS room_name, s.start_time,
                       s.start_time + (m.duration || ' minutes')::interval AS end_time,
                       ss.name AS status, COALESCE(rv.reservation_count, 0) AS reservation_count
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "s.start_time", "ASC"),
                page,
                size,
                showtimeMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminBranchRow> getBranches(String keyword, String status, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append(" AND LOWER(b.name) LIKE :keyword ");
            params.put("keyword", like(keyword));
        }
        if (hasText(status)) {
            where.append(" AND bs.name = :status ");
            params.put("status", status);
        }
        String from = """
                FROM branches b
                LEFT JOIN branch_status bs ON bs.id = b.status_id
                LEFT JOIN wards w ON w.id = b.ward_id
                LEFT JOIN districts d ON d.id = w.district_id
                LEFT JOIN provinces p ON p.id = d.province_id
                LEFT JOIN (
                    SELECT branch_id, COUNT(*) AS room_count
                    FROM rooms
                    GROUP BY branch_id
                ) rc ON rc.branch_id = b.id
                LEFT JOIN (
                    SELECT r.branch_id, COUNT(s.id) AS seat_count
                    FROM rooms r
                    LEFT JOIN seats s ON s.room_id = r.id
                    GROUP BY r.branch_id
                ) sc ON sc.branch_id = b.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "name", "LOWER(b.name)",
                "status", "bs.name",
                "roomCount", "COALESCE(rc.room_count, 0)",
                "seatCount", "COALESCE(sc.seat_count, 0)"
        );
        return page("""
                SELECT b.id, b.name, bs.name AS status, b.specific_address,
                       w.name AS ward, d.name AS district, p.name AS province,
                       COALESCE(rc.room_count, 0) AS room_count,
                       COALESCE(sc.seat_count, 0) AS seat_count
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "LOWER(b.name)", "ASC"),
                page,
                size,
                branchMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminRoomRow> getRooms(Short branchId, String status, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (branchId != null) {
            where.append(" AND b.id = :branchId ");
            params.put("branchId", branchId);
        }
        if (hasText(status)) {
            where.append(" AND rs.name = :status ");
            params.put("status", status);
        }
        String from = """
                FROM rooms r
                JOIN branches b ON b.id = r.branch_id
                JOIN room_types rt ON rt.id = r.room_type_id
                JOIN room_status rs ON rs.id = r.status_id
                LEFT JOIN (
                    SELECT room_id, COUNT(*) AS seat_count
                    FROM seats
                    GROUP BY room_id
                ) sc ON sc.room_id = r.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "name", "LOWER(r.name)",
                "branchName", "LOWER(b.name)",
                "roomType", "LOWER(rt.name)",
                "status", "rs.name",
                "seatCount", "COALESCE(sc.seat_count, 0)"
        );
        return page("""
                SELECT r.id, b.id AS branch_id, b.name AS branch_name, r.name,
                       r.row_count, r.column_count, rt.name AS room_type,
                       rs.name AS status, COALESCE(sc.seat_count, 0) AS seat_count
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "LOWER(b.name), LOWER(r.name)", "ASC"),
                page,
                size,
                roomMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminSeatRow> getSeats(Short branchId, Integer roomId, String status, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (branchId != null) {
            where.append(" AND b.id = :branchId ");
            params.put("branchId", branchId);
        }
        if (roomId != null) {
            where.append(" AND r.id = :roomId ");
            params.put("roomId", roomId);
        }
        if (hasText(status)) {
            where.append(" AND ss.name = :status ");
            params.put("status", status);
        }
        String from = """
                FROM seats s
                JOIN rooms r ON r.id = s.room_id
                JOIN branches b ON b.id = r.branch_id
                JOIN seat_types st ON st.id = s.seat_type_id
                JOIN seat_status ss ON ss.id = s.status_id
                """ + where;
        Map<String, String> sorts = Map.of(
                "name", "s.name",
                "roomName", "LOWER(r.name)",
                "branchName", "LOWER(b.name)",
                "type", "st.name",
                "status", "ss.name"
        );
        return page("""
                SELECT s.id, r.id AS room_id, r.name AS room_name, b.id AS branch_id,
                       b.name AS branch_name, s.name, s.x_position, s.y_position,
                       st.name AS type, ss.name AS status
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "LOWER(b.name), LOWER(r.name), s.y_position, s.x_position", "ASC"),
                page,
                size,
                seatMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminProductRow> getProducts(String keyword, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append(" AND LOWER(p.name) LIKE :keyword ");
            params.put("keyword", like(keyword));
        }
        String from = """
                FROM products p
                LEFT JOIN (
                    SELECT product_id, COUNT(*) AS branch_count
                    FROM product_branch
                    GROUP BY product_id
                ) pb ON pb.product_id = p.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "name", "LOWER(p.name)",
                "price", "p.price",
                "branchCount", "COALESCE(pb.branch_count, 0)"
        );
        return page("""
                SELECT p.id, p.name, p.thumbnail_url, p.price,
                       COALESCE(pb.branch_count, 0) AS branch_count
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "LOWER(p.name)", "ASC"),
                page,
                size,
                productMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminBranchProductRow> getBranchProducts(Short branchId, String status, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (branchId != null) {
            where.append(" AND b.id = :branchId ");
            params.put("branchId", branchId);
        }
        if (hasText(status)) {
            where.append(" AND ps.name = :status ");
            params.put("status", status);
        }
        String from = """
                FROM product_branch pb
                JOIN branches b ON b.id = pb.branch_id
                JOIN products p ON p.id = pb.product_id
                JOIN product_status ps ON ps.id = pb.status_id
                """ + where;
        Map<String, String> sorts = Map.of(
                "branchName", "LOWER(b.name)",
                "productName", "LOWER(p.name)",
                "status", "ps.name"
        );
        return page("""
                SELECT pb.id, b.id AS branch_id, b.name AS branch_name,
                       p.id AS product_id, p.name AS product_name, p.thumbnail_url,
                       ps.name AS status
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "LOWER(b.name), LOWER(p.name)", "ASC"),
                page,
                size,
                branchProductMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminUserRow> getUsers(String keyword, Byte roleId, int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append("""
                     AND (
                        LOWER(u.full_name) LIKE :keyword OR
                        LOWER(u.email) LIKE :keyword OR
                        u.phone LIKE :keyword
                     )
                    """);
            params.put("keyword", like(keyword));
        }
        if (roleId != null) {
            where.append(" AND EXISTS (SELECT 1 FROM user_role urx WHERE urx.user_id = u.id AND urx.role_id = :roleId) ");
            params.put("roleId", roleId);
        }
        String from = """
                FROM users u
                LEFT JOIN genders g ON g.id = u.gender_id
                LEFT JOIN (
                    SELECT ur.user_id, STRING_AGG(r.name, ', ' ORDER BY r.name) AS roles
                    FROM user_role ur
                    JOIN roles r ON r.id = ur.role_id
                    GROUP BY ur.user_id
                ) rr ON rr.user_id = u.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "fullName", "LOWER(u.full_name)",
                "email", "LOWER(u.email)",
                "phone", "u.phone",
                "creationTime", "u.creation_time"
        );
        return page("""
                SELECT u.id, u.full_name, u.avatar_url, u.email, u.phone,
                       u.birthday, g.name AS gender, u.creation_time,
                       COALESCE(rr.roles, '') AS roles
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "u.creation_time", "DESC"),
                page,
                size,
                userMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminRoleRow> getRoles(int page, int size, String sort, String direction) {
        Map<String, Object> params = new HashMap<>();
        String from = """
                FROM roles r
                LEFT JOIN (
                    SELECT role_id, COUNT(*) AS user_count
                    FROM user_role
                    GROUP BY role_id
                ) uc ON uc.role_id = r.id
                LEFT JOIN (
                    SELECT role_id, COUNT(*) AS permission_count
                    FROM role_permission
                    GROUP BY role_id
                ) pc ON pc.role_id = r.id
                """;
        Map<String, String> sorts = Map.of(
                "name", "LOWER(r.name)",
                "userCount", "COALESCE(uc.user_count, 0)",
                "permissionCount", "COALESCE(pc.permission_count, 0)"
        );
        return page("""
                SELECT r.id, r.name, COALESCE(uc.user_count, 0) AS user_count,
                       COALESCE(pc.permission_count, 0) AS permission_count
                """ + from,
                "SELECT COUNT(*) FROM roles r",
                params,
                orderBy(sorts, sort, direction, "LOWER(r.name)", "ASC"),
                page,
                size,
                roleMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminBookingRow> getBookings(
            String keyword,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size,
            String sort,
            String direction
    ) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append("""
                     AND (
                        CAST(rv.id AS TEXT) LIKE :keyword OR
                        LOWER(u.full_name) LIKE :keyword OR
                        LOWER(u.email) LIKE :keyword OR
                        u.phone LIKE :keyword OR
                        LOWER(m.title) LIKE :keyword OR
                        LOWER(COALESCE(pay.transaction_id, '')) LIKE :keyword
                     )
                    """);
            params.put("keyword", like(keyword));
        }
        if (hasText(status)) {
            where.append(" AND rs.name = :status ");
            params.put("status", status);
        }
        if (fromDate != null) {
            where.append(" AND rv.start_time >= :fromDate ");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            where.append(" AND rv.start_time <= :toDate ");
            params.put("toDate", toDate);
        }
        String from = """
                FROM reservations rv
                JOIN reservation_status rs ON rs.id = rv.status_id
                JOIN users u ON u.id = rv.user_id
                JOIN showtimes st ON st.id = rv.showtime_id
                JOIN movies m ON m.id = st.movie_id
                JOIN rooms rm ON rm.id = st.room_id
                JOIN branches b ON b.id = rm.branch_id
                LEFT JOIN (
                    SELECT reservation_id, STRING_AGG(s.name, ', ' ORDER BY s.name) AS seats
                    FROM reservation_seat rsx
                    JOIN seats s ON s.id = rsx.seat_id
                    GROUP BY reservation_id
                ) seat_data ON seat_data.reservation_id = rv.id
                LEFT JOIN (
                    SELECT reservation_id, COALESCE(SUM(quantity), 0) AS product_quantity
                    FROM reservation_product
                    GROUP BY reservation_id
                ) product_data ON product_data.reservation_id = rv.id
                LEFT JOIN (
                    SELECT reservation_id, COUNT(*) AS ticket_count
                    FROM tickets
                    GROUP BY reservation_id
                ) ticket_data ON ticket_data.reservation_id = rv.id
                LEFT JOIN (
                    SELECT DISTINCT ON (pt.reservation_id)
                           pt.reservation_id, pt.transaction_id, pt.amount,
                           pm.name AS payment_method, pts.name AS payment_status
                    FROM payment_transactions pt
                    JOIN payment_methods pm ON pm.id = pt.payment_method_id
                    JOIN payment_transaction_status pts ON pts.id = pt.status_id
                    ORDER BY pt.reservation_id, pt.time DESC
                ) pay ON pay.reservation_id = rv.id
                """ + where;
        Map<String, String> sorts = Map.of(
                "bookedAt", "rv.start_time",
                "showtimeStart", "st.start_time",
                "customerName", "LOWER(u.full_name)",
                "movieTitle", "LOWER(m.title)",
                "status", "rs.name",
                "amount", "COALESCE(pay.amount, 0)"
        );
        return page("""
                SELECT rv.id, u.full_name AS customer_name, u.email AS customer_email,
                       u.phone AS customer_phone, m.title AS movie_title, b.name AS branch_name,
                       rm.name AS room_name, rv.start_time AS booked_at, st.start_time AS showtime_start,
                       COALESCE(seat_data.seats, '') AS seats,
                       COALESCE(product_data.product_quantity, 0) AS product_quantity,
                       COALESCE(ticket_data.ticket_count, 0) AS ticket_count,
                       rs.name AS reservation_status, pay.payment_status, pay.amount,
                       pay.payment_method, pay.transaction_id
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "rv.start_time", "DESC"),
                page,
                size,
                bookingMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPageResponse<AdminPaymentRow> getPayments(
            String keyword,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size,
            String sort,
            String direction
    ) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");
        if (hasText(keyword)) {
            where.append("""
                     AND (
                        LOWER(pt.transaction_id) LIKE :keyword OR
                        CAST(pt.reservation_id AS TEXT) LIKE :keyword OR
                        LOWER(u.full_name) LIKE :keyword OR
                        LOWER(u.email) LIKE :keyword OR
                        LOWER(m.title) LIKE :keyword
                     )
                    """);
            params.put("keyword", like(keyword));
        }
        if (hasText(status)) {
            where.append(" AND pts.name = :status ");
            params.put("status", status);
        }
        if (fromDate != null) {
            where.append(" AND pt.time >= :fromDate ");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            where.append(" AND pt.time <= :toDate ");
            params.put("toDate", toDate);
        }
        String from = """
                FROM payment_transactions pt
                JOIN reservations rv ON rv.id = pt.reservation_id
                JOIN users u ON u.id = rv.user_id
                JOIN showtimes st ON st.id = rv.showtime_id
                JOIN movies m ON m.id = st.movie_id
                JOIN payment_methods pm ON pm.id = pt.payment_method_id
                JOIN payment_transaction_status pts ON pts.id = pt.status_id
                """ + where;
        Map<String, String> sorts = Map.of(
                "time", "pt.time",
                "amount", "pt.amount",
                "status", "pts.name",
                "customerName", "LOWER(u.full_name)",
                "movieTitle", "LOWER(m.title)"
        );
        return page("""
                SELECT pt.id, pt.transaction_id, pt.reservation_id, u.full_name AS customer_name,
                       u.email AS customer_email, m.title AS movie_title, pm.name AS payment_method,
                       pt.time, pt.amount, pts.name AS status
                """ + from,
                "SELECT COUNT(*) " + from,
                params,
                orderBy(sorts, sort, direction, "pt.time", "DESC"),
                page,
                size,
                paymentMapper()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminLookupGroupResponse> getLookups() {
        List<AdminLookupGroupResponse> groups = new ArrayList<>();
        groups.add(lookup("branchStatuses", "Branch statuses", "SELECT id, name, NULL AS code FROM branch_status ORDER BY name"));
        groups.add(lookup("roomStatuses", "Room statuses", "SELECT id, name, NULL AS code FROM room_status ORDER BY name"));
        groups.add(lookup("seatStatuses", "Seat statuses", "SELECT id, name, NULL AS code FROM seat_status ORDER BY name"));
        groups.add(lookup("seatTypes", "Seat types", "SELECT id, name, NULL AS code FROM seat_types ORDER BY name"));
        groups.add(lookup("productStatuses", "Product statuses", "SELECT id, name, NULL AS code FROM product_status ORDER BY name"));
        groups.add(lookup("showtimeStatuses", "Showtime statuses", "SELECT id, name, NULL AS code FROM showtime_status ORDER BY name"));
        groups.add(lookup("reservationStatuses", "Reservation statuses", "SELECT id, name, NULL AS code FROM reservation_status ORDER BY name"));
        groups.add(lookup("paymentStatuses", "Payment statuses", "SELECT id, name, NULL AS code FROM payment_transaction_status ORDER BY name"));
        groups.add(lookup("paymentMethods", "Payment methods", "SELECT id, name, NULL AS code FROM payment_methods ORDER BY name"));
        groups.add(lookup("roles", "Roles", "SELECT id, name, NULL AS code FROM roles ORDER BY name"));
        groups.add(lookup("languages", "Languages", "SELECT id, name, NULL AS code FROM languages ORDER BY name"));
        groups.add(lookup("ageRatings", "Age ratings", "SELECT id, description AS name, code FROM age_ratings ORDER BY id"));
        groups.add(lookup("categories", "Categories", "SELECT id, name, NULL AS code FROM categories ORDER BY name"));
        groups.add(lookup("roomTypes", "Room types", "SELECT id, name, NULL AS code FROM room_types ORDER BY name"));
        return groups;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUserRoles(Long userId, AdminUserRolesUpdateRequest request) {
        jdbc.update("DELETE FROM user_role WHERE user_id = :userId", Map.of("userId", userId));
        List<Byte> roleIds = request.roleIds() == null ? List.of() : request.roleIds().stream().distinct().toList();
        for (Byte roleId : roleIds) {
            jdbc.update(
                    "INSERT INTO user_role(user_id, role_id) VALUES (:userId, :roleId)",
                    Map.of("userId", userId, "roleId", roleId)
            );
        }
    }

    private AdminLookupGroupResponse lookup(String key, String label, String sql) {
        List<AdminLookupOptionResponse> options = jdbc.query(sql, (rs, rowNum) -> new AdminLookupOptionResponse(
                integer(rs, "id"),
                rs.getString("name"),
                rs.getString("code")
        ));
        return new AdminLookupGroupResponse(key, label, options);
    }

    private <T> AdminPageResponse<T> page(
            String selectSql,
            String countSql,
            Map<String, Object> params,
            String orderBy,
            int page,
            int size,
            RowMapper<T> mapper
    ) {
        PageSpec spec = pageSpec(page, size);
        Map<String, Object> pageParams = new HashMap<>(params);
        pageParams.put("limit", spec.size());
        pageParams.put("offset", spec.offset());
        long total = queryLong(countSql, params);
        List<T> content = jdbc.query(selectSql + " " + orderBy + " LIMIT :limit OFFSET :offset", pageParams, mapper);
        int totalPages = spec.size() == 0 ? 0 : (int) Math.ceil((double) total / spec.size());
        boolean last = totalPages == 0 || spec.page() >= totalPages - 1;
        return new AdminPageResponse<>(content, spec.page(), spec.size(), total, totalPages, last);
    }

    private PageSpec pageSpec(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        return new PageSpec(safePage, safeSize, safePage * safeSize);
    }

    private String orderBy(Map<String, String> allowedSorts, String sort, String direction, String defaultSort, String defaultDirection) {
        String selectedSort = allowedSorts.getOrDefault(sort, defaultSort);
        String selectedDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC".equalsIgnoreCase(direction) ? "DESC" : defaultDirection;
        return "ORDER BY " + selectedSort + " " + selectedDirection;
    }

    private long queryLong(String sql, Map<String, Object> params) {
        Long value = jdbc.queryForObject(sql, params, Long.class);
        return value == null ? 0L : value;
    }

    private BigDecimal queryBigDecimal(String sql, Map<String, Object> params) {
        BigDecimal value = jdbc.queryForObject(sql, params, BigDecimal.class);
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private String like(String text) {
        return "%" + text.trim().toLowerCase() + "%";
    }

    private RowMapper<AdminMovieRow> movieMapper() {
        return (rs, rowNum) -> new AdminMovieRow(
                longValue(rs, "id"),
                rs.getString("title"),
                rs.getString("poster_url"),
                localDate(rs, "released_date"),
                integer(rs, "duration"),
                rs.getString("original_language"),
                rs.getString("age_rating"),
                longValue(rs, "showtime_count"),
                longValue(rs, "booking_count"),
                doubleValue(rs, "average_rating"),
                longValue(rs, "rating_count")
        );
    }

    private RowMapper<AdminShowtimeRow> showtimeMapper() {
        return (rs, rowNum) -> new AdminShowtimeRow(
                longValue(rs, "id"),
                longValue(rs, "movie_id"),
                rs.getString("movie_title"),
                shortValue(rs, "branch_id"),
                rs.getString("branch_name"),
                integer(rs, "room_id"),
                rs.getString("room_name"),
                localDateTime(rs, "start_time"),
                localDateTime(rs, "end_time"),
                rs.getString("status"),
                longValue(rs, "reservation_count")
        );
    }

    private RowMapper<AdminBranchRow> branchMapper() {
        return (rs, rowNum) -> new AdminBranchRow(
                shortValue(rs, "id"),
                rs.getString("name"),
                rs.getString("status"),
                rs.getString("specific_address"),
                rs.getString("ward"),
                rs.getString("district"),
                rs.getString("province"),
                longValue(rs, "room_count"),
                longValue(rs, "seat_count")
        );
    }

    private RowMapper<AdminRoomRow> roomMapper() {
        return (rs, rowNum) -> new AdminRoomRow(
                integer(rs, "id"),
                shortValue(rs, "branch_id"),
                rs.getString("branch_name"),
                rs.getString("name"),
                shortValue(rs, "row_count"),
                shortValue(rs, "column_count"),
                rs.getString("room_type"),
                rs.getString("status"),
                longValue(rs, "seat_count")
        );
    }

    private RowMapper<AdminSeatRow> seatMapper() {
        return (rs, rowNum) -> new AdminSeatRow(
                longValue(rs, "id"),
                integer(rs, "room_id"),
                rs.getString("room_name"),
                shortValue(rs, "branch_id"),
                rs.getString("branch_name"),
                rs.getString("name"),
                shortValue(rs, "x_position"),
                shortValue(rs, "y_position"),
                rs.getString("type"),
                rs.getString("status")
        );
    }

    private RowMapper<AdminProductRow> productMapper() {
        return (rs, rowNum) -> new AdminProductRow(
                byteValue(rs, "id"),
                rs.getString("name"),
                rs.getString("thumbnail_url"),
                rs.getBigDecimal("price"),
                longValue(rs, "branch_count")
        );
    }

    private RowMapper<AdminBranchProductRow> branchProductMapper() {
        return (rs, rowNum) -> new AdminBranchProductRow(
                shortValue(rs, "id"),
                shortValue(rs, "branch_id"),
                rs.getString("branch_name"),
                byteValue(rs, "product_id"),
                rs.getString("product_name"),
                rs.getString("thumbnail_url"),
                rs.getString("status")
        );
    }

    private RowMapper<AdminUserRow> userMapper() {
        return (rs, rowNum) -> new AdminUserRow(
                longValue(rs, "id"),
                rs.getString("full_name"),
                rs.getString("avatar_url"),
                rs.getString("email"),
                rs.getString("phone"),
                localDate(rs, "birthday"),
                rs.getString("gender"),
                localDateTime(rs, "creation_time"),
                rs.getString("roles")
        );
    }

    private RowMapper<AdminRoleRow> roleMapper() {
        return (rs, rowNum) -> new AdminRoleRow(
                byteValue(rs, "id"),
                rs.getString("name"),
                longValue(rs, "user_count"),
                longValue(rs, "permission_count")
        );
    }

    private RowMapper<AdminBookingRow> bookingMapper() {
        return (rs, rowNum) -> new AdminBookingRow(
                longValue(rs, "id"),
                rs.getString("customer_name"),
                rs.getString("customer_email"),
                rs.getString("customer_phone"),
                rs.getString("movie_title"),
                rs.getString("branch_name"),
                rs.getString("room_name"),
                localDateTime(rs, "booked_at"),
                localDateTime(rs, "showtime_start"),
                rs.getString("seats"),
                longValue(rs, "product_quantity"),
                longValue(rs, "ticket_count"),
                rs.getString("reservation_status"),
                rs.getString("payment_status"),
                rs.getBigDecimal("amount"),
                rs.getString("payment_method"),
                rs.getString("transaction_id")
        );
    }

    private RowMapper<AdminPaymentRow> paymentMapper() {
        return (rs, rowNum) -> new AdminPaymentRow(
                longValue(rs, "id"),
                rs.getString("transaction_id"),
                longValue(rs, "reservation_id"),
                rs.getString("customer_name"),
                rs.getString("customer_email"),
                rs.getString("movie_title"),
                rs.getString("payment_method"),
                localDateTime(rs, "time"),
                rs.getBigDecimal("amount"),
                rs.getString("status")
        );
    }

    private Long longValue(ResultSet rs, String column) throws SQLException {
        Number value = (Number) rs.getObject(column);
        return value == null ? null : value.longValue();
    }

    private Integer integer(ResultSet rs, String column) throws SQLException {
        Number value = (Number) rs.getObject(column);
        return value == null ? null : value.intValue();
    }

    private Short shortValue(ResultSet rs, String column) throws SQLException {
        Number value = (Number) rs.getObject(column);
        return value == null ? null : value.shortValue();
    }

    private Byte byteValue(ResultSet rs, String column) throws SQLException {
        Number value = (Number) rs.getObject(column);
        return value == null ? null : value.byteValue();
    }

    private Double doubleValue(ResultSet rs, String column) throws SQLException {
        Number value = (Number) rs.getObject(column);
        return value == null ? null : value.doubleValue();
    }

    private LocalDate localDate(ResultSet rs, String column) throws SQLException {
        Date value = rs.getDate(column);
        return value == null ? null : value.toLocalDate();
    }

    private LocalDateTime localDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value == null ? null : value.toLocalDateTime();
    }

    private record PageSpec(int page, int size, int offset) {
    }
}
