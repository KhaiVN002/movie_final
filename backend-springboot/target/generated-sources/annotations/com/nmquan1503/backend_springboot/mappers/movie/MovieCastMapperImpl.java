package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCastCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCastUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieCastResponse;
import com.nmquan1503.backend_springboot.entities.movie.MovieCast;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:09+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MovieCastMapperImpl implements MovieCastMapper {

    @Autowired
    private PersonMapper personMapper;

    @Override
    public MovieCastResponse toMovieCastResponse(MovieCast movieCast) {
        if ( movieCast == null ) {
            return null;
        }

        MovieCastResponse.MovieCastResponseBuilder movieCastResponse = MovieCastResponse.builder();

        movieCastResponse.character( movieCast.getCharacter() );
        movieCastResponse.id( movieCast.getId() );
        movieCastResponse.order( movieCast.getOrder() );
        movieCastResponse.person( personMapper.toPersonPreviewResponse( movieCast.getPerson() ) );

        return movieCastResponse.build();
    }

    @Override
    public List<MovieCastResponse> toListMovieCastResponse(List<MovieCast> cast) {
        if ( cast == null ) {
            return null;
        }

        List<MovieCastResponse> list = new ArrayList<MovieCastResponse>( cast.size() );
        for ( MovieCast movieCast : cast ) {
            list.add( toMovieCastResponse( movieCast ) );
        }

        return list;
    }

    @Override
    public MovieCast toMovieCast(MovieCastCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        MovieCast.MovieCastBuilder movieCast = MovieCast.builder();

        movieCast.character( request.getCharacter() );
        movieCast.order( request.getOrder() );

        return movieCast.build();
    }

    @Override
    public List<MovieCast> toListMovieCast(List<MovieCastCreationRequest> requests) {
        if ( requests == null ) {
            return null;
        }

        List<MovieCast> list = new ArrayList<MovieCast>( requests.size() );
        for ( MovieCastCreationRequest movieCastCreationRequest : requests ) {
            list.add( toMovieCast( movieCastCreationRequest ) );
        }

        return list;
    }

    @Override
    public void updateMovieCast(MovieCast movieCast, MovieCastUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        movieCast.setCharacter( request.getCharacter() );
        movieCast.setOrder( request.getOrder() );
    }
}
