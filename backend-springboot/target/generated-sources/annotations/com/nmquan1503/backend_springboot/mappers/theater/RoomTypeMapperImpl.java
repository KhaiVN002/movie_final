package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomTypeDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomTypeIconResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomTypePreviewResponse;
import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomTypeSimpleResponse;
import com.nmquan1503.backend_springboot.entities.theater.RoomType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RoomTypeMapperImpl implements RoomTypeMapper {

    @Override
    public RoomTypeSimpleResponse toRoomTypeSummaryResponse(RoomType roomType) {
        if ( roomType == null ) {
            return null;
        }

        RoomTypeSimpleResponse.RoomTypeSimpleResponseBuilder roomTypeSimpleResponse = RoomTypeSimpleResponse.builder();

        roomTypeSimpleResponse.id( roomType.getId() );
        roomTypeSimpleResponse.name( roomType.getName() );
        roomTypeSimpleResponse.extraFee( roomType.getExtraFee() );

        return roomTypeSimpleResponse.build();
    }

    @Override
    public RoomTypePreviewResponse toRoomTypePreviewResponse(RoomType roomType) {
        if ( roomType == null ) {
            return null;
        }

        RoomTypePreviewResponse.RoomTypePreviewResponseBuilder roomTypePreviewResponse = RoomTypePreviewResponse.builder();

        roomTypePreviewResponse.id( roomType.getId() );
        roomTypePreviewResponse.name( roomType.getName() );
        roomTypePreviewResponse.thumbnailURL( roomType.getThumbnailURL() );
        roomTypePreviewResponse.slogan( roomType.getSlogan() );

        return roomTypePreviewResponse.build();
    }

    @Override
    public List<RoomTypePreviewResponse> toListRoomTypePreviewResponse(List<RoomType> roomTypes) {
        if ( roomTypes == null ) {
            return null;
        }

        List<RoomTypePreviewResponse> list = new ArrayList<RoomTypePreviewResponse>( roomTypes.size() );
        for ( RoomType roomType : roomTypes ) {
            list.add( toRoomTypePreviewResponse( roomType ) );
        }

        return list;
    }

    @Override
    public RoomTypeIconResponse toRoomTypeIconResponse(RoomType roomType) {
        if ( roomType == null ) {
            return null;
        }

        RoomTypeIconResponse.RoomTypeIconResponseBuilder roomTypeIconResponse = RoomTypeIconResponse.builder();

        roomTypeIconResponse.id( roomType.getId() );
        roomTypeIconResponse.name( roomType.getName() );
        roomTypeIconResponse.iconURL( roomType.getIconURL() );

        return roomTypeIconResponse.build();
    }

    @Override
    public List<RoomTypeIconResponse> toListRoomTypeIconResponse(List<RoomType> roomTypes) {
        if ( roomTypes == null ) {
            return null;
        }

        List<RoomTypeIconResponse> list = new ArrayList<RoomTypeIconResponse>( roomTypes.size() );
        for ( RoomType roomType : roomTypes ) {
            list.add( toRoomTypeIconResponse( roomType ) );
        }

        return list;
    }

    @Override
    public RoomTypeDetailResponse toRoomTypeDetailResponse(RoomType roomType) {
        if ( roomType == null ) {
            return null;
        }

        RoomTypeDetailResponse.RoomTypeDetailResponseBuilder roomTypeDetailResponse = RoomTypeDetailResponse.builder();

        roomTypeDetailResponse.id( roomType.getId() );
        roomTypeDetailResponse.name( roomType.getName() );
        roomTypeDetailResponse.iconURL( roomType.getIconURL() );
        roomTypeDetailResponse.overview( roomType.getOverview() );

        return roomTypeDetailResponse.build();
    }
}
