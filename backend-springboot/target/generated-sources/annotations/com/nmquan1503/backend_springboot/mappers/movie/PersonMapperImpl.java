package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.PersonPreviewResponse;
import com.nmquan1503.backend_springboot.entities.movie.Person;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PersonMapperImpl implements PersonMapper {

    @Override
    public PersonPreviewResponse toPersonPreviewResponse(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonPreviewResponse.PersonPreviewResponseBuilder personPreviewResponse = PersonPreviewResponse.builder();

        personPreviewResponse.avatarURL( person.getAvatarURL() );
        personPreviewResponse.id( person.getId() );
        personPreviewResponse.name( person.getName() );

        return personPreviewResponse.build();
    }
}
