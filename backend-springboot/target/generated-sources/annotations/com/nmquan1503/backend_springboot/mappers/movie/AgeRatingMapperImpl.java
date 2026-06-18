package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.AgeRatingLabelResponse;
import com.nmquan1503.backend_springboot.entities.movie.AgeRating;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class AgeRatingMapperImpl implements AgeRatingMapper {

    @Override
    public AgeRatingLabelResponse toAgeRatingSummaryResponse(AgeRating ageRating) {
        if ( ageRating == null ) {
            return null;
        }

        AgeRatingLabelResponse.AgeRatingLabelResponseBuilder ageRatingLabelResponse = AgeRatingLabelResponse.builder();

        ageRatingLabelResponse.id( ageRating.getId() );
        ageRatingLabelResponse.code( ageRating.getCode() );

        return ageRatingLabelResponse.build();
    }
}
