package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.PersonPreviewResponse;
import com.nmquan1503.backend_springboot.entities.movie.Person;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public PersonPreviewResponse toPersonPreviewResponse(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonPreviewResponse.PersonPreviewResponseBuilder personPreviewResponse = PersonPreviewResponse.builder();

        personPreviewResponse.id( person.getId() );
        personPreviewResponse.name( person.getName() );
        personPreviewResponse.avatarURL( person.getAvatarURL() );

        return personPreviewResponse.build();
    }
}
