package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.SeatTypeResponse;
import com.nmquan1503.backend_springboot.entities.theater.SeatType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SeatTypeMapperImpl implements SeatTypeMapper {

    @Override
    public SeatTypeResponse toSeatTypeSummaryResponse(SeatType seatType) {
        if ( seatType == null ) {
            return null;
        }

        SeatTypeResponse.SeatTypeResponseBuilder seatTypeResponse = SeatTypeResponse.builder();

        seatTypeResponse.id( seatType.getId() );
        seatTypeResponse.name( seatType.getName() );
        seatTypeResponse.extraFee( seatType.getExtraFee() );

        return seatTypeResponse.build();
    }
}
