package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCrewCreationRequest;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieCrewResponse;
import com.nmquan1503.backend_springboot.entities.movie.MovieCrew;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MovieCrewMapperImpl implements MovieCrewMapper {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PositionMapper positionMapper;

    @Override
    public MovieCrewResponse toMovieCrewResponse(MovieCrew movieCrew) {
        if ( movieCrew == null ) {
            return null;
        }

        MovieCrewResponse.MovieCrewResponseBuilder movieCrewResponse = MovieCrewResponse.builder();

        movieCrewResponse.id( movieCrew.getId() );
        movieCrewResponse.person( personMapper.toPersonPreviewResponse( movieCrew.getPerson() ) );
        movieCrewResponse.position( positionMapper.toPositionResponse( movieCrew.getPosition() ) );

        return movieCrewResponse.build();
    }

    @Override
    public List<MovieCrewResponse> toListMovieCrewResponse(List<MovieCrew> crew) {
        if ( crew == null ) {
            return null;
        }

        List<MovieCrewResponse> list = new ArrayList<MovieCrewResponse>( crew.size() );
        for ( MovieCrew movieCrew : crew ) {
            list.add( toMovieCrewResponse( movieCrew ) );
        }

        return list;
    }

    @Override
    public MovieCrew toMovieCrew(MovieCrewCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        MovieCrew.MovieCrewBuilder movieCrew = MovieCrew.builder();

        return movieCrew.build();
    }

    @Override
    public List<MovieCrewCreationRequest> toListMovieCrew(List<MovieCrewCreationRequest> requests) {
        if ( requests == null ) {
            return null;
        }

        List<MovieCrewCreationRequest> list = new ArrayList<MovieCrewCreationRequest>( requests.size() );
        for ( MovieCrewCreationRequest movieCrewCreationRequest : requests ) {
            list.add( movieCrewCreationRequest );
        }

        return list;
    }
}
