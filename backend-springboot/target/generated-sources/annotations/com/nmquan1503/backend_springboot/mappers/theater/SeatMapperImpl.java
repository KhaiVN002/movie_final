package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.SeatDetailResponse;
import com.nmquan1503.backend_springboot.entities.theater.Seat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:08+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class SeatMapperImpl implements SeatMapper {

    @Autowired
    private SeatStatusMapper seatStatusMapper;
    @Autowired
    private SeatTypeMapper seatTypeMapper;

    @Override
    public SeatDetailResponse toSeatDetailResponse(Seat seat) {
        if ( seat == null ) {
            return null;
        }

        SeatDetailResponse.SeatDetailResponseBuilder seatDetailResponse = SeatDetailResponse.builder();

        seatDetailResponse.id( seat.getId() );
        seatDetailResponse.name( seat.getName() );
        seatDetailResponse.positionX( seat.getPositionX() );
        seatDetailResponse.positionY( seat.getPositionY() );
        seatDetailResponse.status( seatStatusMapper.toSeatStatusSummaryResponse( seat.getStatus() ) );
        seatDetailResponse.type( seatTypeMapper.toSeatTypeSummaryResponse( seat.getType() ) );

        return seatDetailResponse.build();
    }

    @Override
    public List<SeatDetailResponse> toListSeatDetailResponse(List<Seat> seats) {
        if ( seats == null ) {
            return null;
        }

        List<SeatDetailResponse> list = new ArrayList<SeatDetailResponse>( seats.size() );
        for ( Seat seat : seats ) {
            list.add( toSeatDetailResponse( seat ) );
        }

        return list;
    }
}
