package com.nmquan1503.backend_springboot.dtos.requests.reservation;

import com.nmquan1503.backend_springboot.dtos.requests.product.ProductOrderRequest;
import com.nmquan1503.backend_springboot.validators.UniqueElements;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationCreationRequest {

    @NotNull(message = "SHOWTIME_EMPTY")
    Long showtimeId;

    @NotEmpty(message = "SEAT_EMPTY")
    @UniqueElements(message = "DUPLICATE_SEAT")
    List<Long> seatIds;

    List<ProductOrderRequest> productOrders;

}
