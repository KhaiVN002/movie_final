package com.nmquan1503.backend_springboot.mappers.reservation;

import com.nmquan1503.backend_springboot.dtos.responses.reservation.ReservationDetailResponse;
import com.nmquan1503.backend_springboot.entities.reservation.Reservation;
import com.nmquan1503.backend_springboot.mappers.showtime.ShowtimeMapper;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:09+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ReservationMapperImpl implements ReservationMapper {

    @Autowired
    private ShowtimeMapper showtimeMapper;
    @Autowired
    private ReservationStatusMapper reservationStatusMapper;

    @Override
    public ReservationDetailResponse toReservationDetailResponse(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }

        ReservationDetailResponse.ReservationDetailResponseBuilder reservationDetailResponse = ReservationDetailResponse.builder();

        reservationDetailResponse.endTime( reservation.getEndTime() );
        reservationDetailResponse.id( reservation.getId() );
        reservationDetailResponse.showtime( showtimeMapper.toShowtimeDetailResponse( reservation.getShowtime() ) );
        reservationDetailResponse.startTime( reservation.getStartTime() );
        reservationDetailResponse.status( reservationStatusMapper.toReservationStatusSummaryResponse( reservation.getStatus() ) );

        return reservationDetailResponse.build();
    }
}
