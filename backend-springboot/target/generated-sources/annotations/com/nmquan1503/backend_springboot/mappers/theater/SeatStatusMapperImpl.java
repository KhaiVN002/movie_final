package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.SeatStatusResponse;
import com.nmquan1503.backend_springboot.entities.theater.SeatStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
