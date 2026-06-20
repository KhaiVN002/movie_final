package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.PositionResponse;
import com.nmquan1503.backend_springboot.entities.movie.Position;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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

        positionResponse.department( departmentMapper.toDepartmentResponse( position.getDepartment() ) );
        positionResponse.id( position.getId() );
        positionResponse.name( position.getName() );

        return positionResponse.build();
    }
}
