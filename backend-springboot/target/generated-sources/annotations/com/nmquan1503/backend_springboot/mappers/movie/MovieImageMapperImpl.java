package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieImageResponse;
import com.nmquan1503.backend_springboot.entities.movie.MovieImage;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:08+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MovieImageMapperImpl implements MovieImageMapper {

    @Override
    public MovieImageResponse toMovieImageResponse(MovieImage movieImage) {
        if ( movieImage == null ) {
            return null;
        }

        MovieImageResponse.MovieImageResponseBuilder movieImageResponse = MovieImageResponse.builder();

        movieImageResponse.id( movieImage.getId() );
        movieImageResponse.imageURL( movieImage.getImageURL() );

        return movieImageResponse.build();
    }
}
