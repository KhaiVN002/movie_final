package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.PositionResponse;
import com.nmquan1503.backend_springboot.entities.movie.Position;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PositionMapperImpl implements PositionMapper {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public PositionResponse toPositionResponse(Position position) {
        if ( position == null ) {
            return null;
        }

        PositionResponse.PositionResponseBuilder positionResponse = PositionResponse.builder();

        positionResponse.id( position.getId() );
        positionResponse.department( departmentMapper.toDepartmentResponse( position.getDepartment() ) );
        positionResponse.name( position.getName() );

        return positionResponse.build();
    }
}
