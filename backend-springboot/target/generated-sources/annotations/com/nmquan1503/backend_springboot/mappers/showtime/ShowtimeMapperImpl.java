package com.nmquan1503.backend_springboot.mappers.showtime;

import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeOptionResponse;
import com.nmquan1503.backend_springboot.dtos.responses.showtime.ShowtimeStatusResponse;
import com.nmquan1503.backend_springboot.entities.showtime.Showtime;
import com.nmquan1503.backend_springboot.entities.showtime.ShowtimeStatus;
import com.nmquan1503.backend_springboot.mappers.movie.MovieMapper;
import com.nmquan1503.backend_springboot.mappers.theater.RoomMapper;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:09+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ShowtimeMapperImpl implements ShowtimeMapper {

    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private MovieMapper movieMapper;

    @Override
    public ShowtimeOptionResponse toShowtimeSummaryResponse(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }

        ShowtimeOptionResponse.ShowtimeOptionResponseBuilder showtimeOptionResponse = ShowtimeOptionResponse.builder();

        showtimeOptionResponse.id( showtime.getId() );
        showtimeOptionResponse.room( roomMapper.toRoomDetailResponse( showtime.getRoom() ) );
        showtimeOptionResponse.startTime( showtime.getStartTime() );
        showtimeOptionResponse.status( showtimeStatusToShowtimeStatusResponse( showtime.getStatus() ) );

        return showtimeOptionResponse.build();
    }

    @Override
    public ShowtimeDetailResponse toShowtimeDetailResponse(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }

        ShowtimeDetailResponse.ShowtimeDetailResponseBuilder showtimeDetailResponse = ShowtimeDetailResponse.builder();

        showtimeDetailResponse.id( showtime.getId() );
        showtimeDetailResponse.movie( movieMapper.toMovieBannerResponse( showtime.getMovie() ) );
        showtimeDetailResponse.room( roomMapper.toRoomDetailResponse( showtime.getRoom() ) );
        showtimeDetailResponse.startTime( showtime.getStartTime() );

        return showtimeDetailResponse.build();
    }

    protected ShowtimeStatusResponse showtimeStatusToShowtimeStatusResponse(ShowtimeStatus showtimeStatus) {
        if ( showtimeStatus == null ) {
            return null;
        }

        ShowtimeStatusResponse.ShowtimeStatusResponseBuilder showtimeStatusResponse = ShowtimeStatusResponse.builder();

        showtimeStatusResponse.id( showtimeStatus.getId() );
        showtimeStatusResponse.name( showtimeStatus.getName() );

        return showtimeStatusResponse.build();
    }
}
