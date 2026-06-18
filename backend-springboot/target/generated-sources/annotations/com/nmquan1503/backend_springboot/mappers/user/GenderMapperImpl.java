package com.nmquan1503.backend_springboot.mappers.user;

import com.nmquan1503.backend_springboot.dtos.responses.user.GenderOptionResponse;
import com.nmquan1503.backend_springboot.entities.user.Gender;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class GenderMapperImpl implements GenderMapper {

    @Override
    public GenderOptionResponse toGenderOptionResponse(Gender gender) {
        if ( gender == null ) {
            return null;
        }

        GenderOptionResponse.GenderOptionResponseBuilder genderOptionResponse = GenderOptionResponse.builder();

        genderOptionResponse.id( gender.getId() );
        genderOptionResponse.name( gender.getName() );

        return genderOptionResponse.build();
    }
}
