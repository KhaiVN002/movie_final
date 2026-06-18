package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.movie.AgeRatingDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.LanguageResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieBannerResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieListItemResponse;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MoviePreviewResponse;
import com.nmquan1503.backend_springboot.entities.movie.AgeRating;
import com.nmquan1503.backend_springboot.entities.movie.Language;
import com.nmquan1503.backend_springboot.entities.movie.Movie;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MovieMapperImpl implements MovieMapper {

    @Autowired
    private AgeRatingMapper ageRatingMapper;

    @Override
    public MoviePreviewResponse toMoviePreviewResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MoviePreviewResponse.MoviePreviewResponseBuilder moviePreviewResponse = MoviePreviewResponse.builder();

        moviePreviewResponse.id( movie.getId() );
        moviePreviewResponse.title( movie.getTitle() );
        moviePreviewResponse.posterURL( movie.getPosterURL() );
        moviePreviewResponse.ageRating( ageRatingMapper.toAgeRatingSummaryResponse( movie.getAgeRating() ) );

        return moviePreviewResponse.build();
    }

    @Override
    public MovieListItemResponse toMovieListItemResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieListItemResponse.MovieListItemResponseBuilder movieListItemResponse = MovieListItemResponse.builder();

        movieListItemResponse.id( movie.getId() );
        movieListItemResponse.title( movie.getTitle() );
        movieListItemResponse.posterURL( movie.getPosterURL() );
        movieListItemResponse.releasedDate( movie.getReleasedDate() );
        movieListItemResponse.duration( movie.getDuration() );
        movieListItemResponse.ageRating( ageRatingMapper.toAgeRatingSummaryResponse( movie.getAgeRating() ) );

        return movieListItemResponse.build();
    }

    @Override
    public MovieDetailResponse toMovieDetailResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieDetailResponse.MovieDetailResponseBuilder movieDetailResponse = MovieDetailResponse.builder();

        movieDetailResponse.id( movie.getId() );
        movieDetailResponse.title( movie.getTitle() );
        movieDetailResponse.posterURL( movie.getPosterURL() );
        movieDetailResponse.backdropURL( movie.getBackdropURL() );
        movieDetailResponse.trailerURL( movie.getTrailerURL() );
        movieDetailResponse.tagline( movie.getTagline() );
        movieDetailResponse.overview( movie.getOverview() );
        movieDetailResponse.releasedDate( movie.getReleasedDate() );
        movieDetailResponse.duration( movie.getDuration() );
        movieDetailResponse.originalLanguage( languageToLanguageResponse( movie.getOriginalLanguage() ) );
        movieDetailResponse.ageRating( ageRatingToAgeRatingDetailResponse( movie.getAgeRating() ) );

        return movieDetailResponse.build();
    }

    @Override
    public Movie toMovie(MovieCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Movie.MovieBuilder movie = Movie.builder();

        movie.title( request.getTitle() );
        movie.posterURL( request.getPosterURL() );
        movie.trailerURL( request.getTrailerURL() );
        movie.tagline( request.getTagline() );
        movie.overview( request.getOverview() );
        movie.releasedDate( request.getReleasedDate() );
        movie.duration( request.getDuration() );

        return movie.build();
    }

    @Override
    public void updateMovie(Movie movie, MovieUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        movie.setTitle( request.getTitle() );
        movie.setPosterURL( request.getPosterURL() );
        movie.setBackdropURL( request.getBackdropURL() );
        movie.setTrailerURL( request.getTrailerURL() );
        movie.setTagline( request.getTagline() );
        movie.setOverview( request.getOverview() );
        movie.setReleasedDate( request.getReleasedDate() );
        movie.setDuration( request.getDuration() );
    }

    @Override
    public MovieBannerResponse toMovieBannerResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieBannerResponse.MovieBannerResponseBuilder movieBannerResponse = MovieBannerResponse.builder();

        movieBannerResponse.id( movie.getId() );
        movieBannerResponse.title( movie.getTitle() );
        movieBannerResponse.backdropURL( movie.getBackdropURL() );

        return movieBannerResponse.build();
    }

    protected LanguageResponse languageToLanguageResponse(Language language) {
        if ( language == null ) {
            return null;
        }

        LanguageResponse.LanguageResponseBuilder languageResponse = LanguageResponse.builder();

        languageResponse.id( language.getId() );
        languageResponse.name( language.getName() );

        return languageResponse.build();
    }

    protected AgeRatingDetailResponse ageRatingToAgeRatingDetailResponse(AgeRating ageRating) {
        if ( ageRating == null ) {
            return null;
        }

        AgeRatingDetailResponse.AgeRatingDetailResponseBuilder ageRatingDetailResponse = AgeRatingDetailResponse.builder();

        ageRatingDetailResponse.id( ageRating.getId() );
        ageRatingDetailResponse.code( ageRating.getCode() );
        ageRatingDetailResponse.description( ageRating.getDescription() );

        return ageRatingDetailResponse.build();
    }
}
