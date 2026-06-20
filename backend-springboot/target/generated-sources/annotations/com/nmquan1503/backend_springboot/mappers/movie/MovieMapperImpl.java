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
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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

        moviePreviewResponse.ageRating( ageRatingMapper.toAgeRatingSummaryResponse( movie.getAgeRating() ) );
        moviePreviewResponse.id( movie.getId() );
        moviePreviewResponse.posterURL( movie.getPosterURL() );
        moviePreviewResponse.title( movie.getTitle() );

        return moviePreviewResponse.build();
    }

    @Override
    public MovieListItemResponse toMovieListItemResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieListItemResponse.MovieListItemResponseBuilder movieListItemResponse = MovieListItemResponse.builder();

        movieListItemResponse.ageRating( ageRatingMapper.toAgeRatingSummaryResponse( movie.getAgeRating() ) );
        movieListItemResponse.duration( movie.getDuration() );
        movieListItemResponse.id( movie.getId() );
        movieListItemResponse.posterURL( movie.getPosterURL() );
        movieListItemResponse.releasedDate( movie.getReleasedDate() );
        movieListItemResponse.title( movie.getTitle() );

        return movieListItemResponse.build();
    }

    @Override
    public MovieDetailResponse toMovieDetailResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieDetailResponse.MovieDetailResponseBuilder movieDetailResponse = MovieDetailResponse.builder();

        movieDetailResponse.ageRating( ageRatingToAgeRatingDetailResponse( movie.getAgeRating() ) );
        movieDetailResponse.backdropURL( movie.getBackdropURL() );
        movieDetailResponse.duration( movie.getDuration() );
        movieDetailResponse.id( movie.getId() );
        movieDetailResponse.originalLanguage( languageToLanguageResponse( movie.getOriginalLanguage() ) );
        movieDetailResponse.overview( movie.getOverview() );
        movieDetailResponse.posterURL( movie.getPosterURL() );
        movieDetailResponse.releasedDate( movie.getReleasedDate() );
        movieDetailResponse.tagline( movie.getTagline() );
        movieDetailResponse.title( movie.getTitle() );
        movieDetailResponse.trailerURL( movie.getTrailerURL() );

        return movieDetailResponse.build();
    }

    @Override
    public Movie toMovie(MovieCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Movie.MovieBuilder movie = Movie.builder();

        movie.duration( request.getDuration() );
        movie.overview( request.getOverview() );
        movie.posterURL( request.getPosterURL() );
        movie.releasedDate( request.getReleasedDate() );
        movie.tagline( request.getTagline() );
        movie.title( request.getTitle() );
        movie.trailerURL( request.getTrailerURL() );

        return movie.build();
    }

    @Override
    public void updateMovie(Movie movie, MovieUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        movie.setBackdropURL( request.getBackdropURL() );
        movie.setDuration( request.getDuration() );
        movie.setOverview( request.getOverview() );
        movie.setPosterURL( request.getPosterURL() );
        movie.setReleasedDate( request.getReleasedDate() );
        movie.setTagline( request.getTagline() );
        movie.setTitle( request.getTitle() );
        movie.setTrailerURL( request.getTrailerURL() );
    }

    @Override
    public MovieBannerResponse toMovieBannerResponse(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieBannerResponse.MovieBannerResponseBuilder movieBannerResponse = MovieBannerResponse.builder();

        movieBannerResponse.backdropURL( movie.getBackdropURL() );
        movieBannerResponse.id( movie.getId() );
        movieBannerResponse.title( movie.getTitle() );

        return movieBannerResponse.build();
    }

    protected AgeRatingDetailResponse ageRatingToAgeRatingDetailResponse(AgeRating ageRating) {
        if ( ageRating == null ) {
            return null;
        }

        AgeRatingDetailResponse.AgeRatingDetailResponseBuilder ageRatingDetailResponse = AgeRatingDetailResponse.builder();

        ageRatingDetailResponse.code( ageRating.getCode() );
        ageRatingDetailResponse.description( ageRating.getDescription() );
        ageRatingDetailResponse.id( ageRating.getId() );

        return ageRatingDetailResponse.build();
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
}
