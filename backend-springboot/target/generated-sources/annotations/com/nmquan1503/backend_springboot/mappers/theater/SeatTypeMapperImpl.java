package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.SeatTypeResponse;
import com.nmquan1503.backend_springboot.entities.theater.SeatType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:48+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SeatTypeMapperImpl implements SeatTypeMapper {

    @Override
    public SeatTypeResponse toSeatTypeSummaryResponse(SeatType seatType) {
        if ( seatType == null ) {
            return null;
        }

        SeatTypeResponse.SeatTypeResponseBuilder seatTypeResponse = SeatTypeResponse.builder();

        seatTypeResponse.extraFee( seatType.getExtraFee() );
        seatTypeResponse.id( seatType.getId() );
        seatTypeResponse.name( seatType.getName() );

        return seatTypeResponse.build();
    }
}
