package com.nmquan1503.backend_springboot.mappers.reservation;

import com.nmquan1503.backend_springboot.dtos.responses.reservation.ReservationStatusResponse;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ReservationStatusMapperImpl implements ReservationStatusMapper {

    @Override
    public ReservationStatusResponse toReservationStatusSummaryResponse(ReservationStatus status) {
        if ( status == null ) {
            return null;
        }

        ReservationStatusResponse.ReservationStatusResponseBuilder reservationStatusResponse = ReservationStatusResponse.builder();

        reservationStatusResponse.id( status.getId() );
        reservationStatusResponse.name( status.getName() );

        return reservationStatusResponse.build();
    }
}
