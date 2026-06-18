package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomStatusResponse;
import com.nmquan1503.backend_springboot.entities.theater.Room;
import com.nmquan1503.backend_springboot.entities.theater.RoomStatus;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RoomMapperImpl implements RoomMapper {

    @Autowired
    private RoomTypeMapper roomTypeMapper;
    @Autowired
    private BranchMapper branchMapper;

    @Override
    public RoomDetailResponse toRoomDetailResponse(Room room) {
        if ( room == null ) {
            return null;
        }

        RoomDetailResponse.RoomDetailResponseBuilder roomDetailResponse = RoomDetailResponse.builder();

        roomDetailResponse.id( room.getId() );
        roomDetailResponse.name( room.getName() );
        roomDetailResponse.rowCount( room.getRowCount() );
        roomDetailResponse.columnCount( room.getColumnCount() );
        roomDetailResponse.branch( branchMapper.toBranchOptionResponse( room.getBranch() ) );
        roomDetailResponse.type( roomTypeMapper.toRoomTypeSummaryResponse( room.getType() ) );
        roomDetailResponse.status( roomStatusToRoomStatusResponse( room.getStatus() ) );

        return roomDetailResponse.build();
    }

    protected RoomStatusResponse roomStatusToRoomStatusResponse(RoomStatus roomStatus) {
        if ( roomStatus == null ) {
            return null;
        }

        RoomStatusResponse.RoomStatusResponseBuilder roomStatusResponse = RoomStatusResponse.builder();

        roomStatusResponse.id( roomStatus.getId() );
        roomStatusResponse.name( roomStatus.getName() );

        return roomStatusResponse.build();
    }
}
