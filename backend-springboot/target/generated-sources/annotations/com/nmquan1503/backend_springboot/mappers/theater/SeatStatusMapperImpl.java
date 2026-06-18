package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.SeatStatusResponse;
import com.nmquan1503.backend_springboot.entities.theater.SeatStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SeatStatusMapperImpl implements SeatStatusMapper {

    @Override
    public SeatStatusResponse toSeatStatusSummaryResponse(SeatStatus seatStatus) {
        if ( seatStatus == null ) {
            return null;
        }

        SeatStatusResponse.SeatStatusResponseBuilder seatStatusResponse = SeatStatusResponse.builder();

        seatStatusResponse.id( seatStatus.getId() );
        seatStatusResponse.name( seatStatus.getName() );

        return seatStatusResponse.build();
    }
}
