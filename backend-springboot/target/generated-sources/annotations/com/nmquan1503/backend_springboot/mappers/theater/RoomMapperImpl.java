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
    date = "2026-06-20T14:12:50+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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

        roomDetailResponse.branch( branchMapper.toBranchOptionResponse( room.getBranch() ) );
        roomDetailResponse.columnCount( room.getColumnCount() );
        roomDetailResponse.id( room.getId() );
        roomDetailResponse.name( room.getName() );
        roomDetailResponse.rowCount( room.getRowCount() );
        roomDetailResponse.status( roomStatusToRoomStatusResponse( room.getStatus() ) );
        roomDetailResponse.type( roomTypeMapper.toRoomTypeSummaryResponse( room.getType() ) );

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
