package com.nmquan1503.backend_springboot.mappers.theater;

import com.nmquan1503.backend_springboot.dtos.responses.theater.RoomTypeFeatureResponse;
import com.nmquan1503.backend_springboot.entities.theater.RoomTypeFeature;
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
public class RoomTypeFeatureMapperImpl implements RoomTypeFeatureMapper {

    @Override
    public RoomTypeFeatureResponse toRoomTypeFeatureResponse(RoomTypeFeature roomTypeFeature) {
        if ( roomTypeFeature == null ) {
            return null;
        }

        RoomTypeFeatureResponse.RoomTypeFeatureResponseBuilder roomTypeFeatureResponse = RoomTypeFeatureResponse.builder();

        roomTypeFeatureResponse.id( roomTypeFeature.getId() );
        roomTypeFeatureResponse.imageURL( roomTypeFeature.getImageURL() );
        roomTypeFeatureResponse.title( roomTypeFeature.getTitle() );
        roomTypeFeatureResponse.description( roomTypeFeature.getDescription() );

        return roomTypeFeatureResponse.build();
    }

    @Override
    public List<RoomTypeFeatureResponse> toListRoomTypeFeatureResponse(List<RoomTypeFeature> roomTypeFeatures) {
        if ( roomTypeFeatures == null ) {
            return null;
        }

        List<RoomTypeFeatureResponse> list = new ArrayList<RoomTypeFeatureResponse>( roomTypeFeatures.size() );
        for ( RoomTypeFeature roomTypeFeature : roomTypeFeatures ) {
            list.add( toRoomTypeFeatureResponse( roomTypeFeature ) );
        }

        return list;
    }
}
