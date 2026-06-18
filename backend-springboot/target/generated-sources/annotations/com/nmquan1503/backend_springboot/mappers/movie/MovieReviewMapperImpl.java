package com.nmquan1503.backend_springboot.mappers.movie;

import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieReviewCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieReviewUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.movie.MovieReviewResponse;
import com.nmquan1503.backend_springboot.dtos.responses.user.UserPreviewResponse;
import com.nmquan1503.backend_springboot.entities.movie.MovieReview;
import com.nmquan1503.backend_springboot.entities.user.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:35:59+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MovieReviewMapperImpl implements MovieReviewMapper {

    @Override
    public MovieReviewResponse toMovieReviewSummaryResponse(MovieReview movieReview) {
        if ( movieReview == null ) {
            return null;
        }

        MovieReviewResponse.MovieReviewResponseBuilder movieReviewResponse = MovieReviewResponse.builder();

        movieReviewResponse.id( movieReview.getId() );
        movieReviewResponse.user( userToUserPreviewResponse( movieReview.getUser() ) );
        movieReviewResponse.rating( movieReview.getRating() );
        movieReviewResponse.comment( movieReview.getComment() );
        movieReviewResponse.creationTime( movieReview.getCreationTime() );

        return movieReviewResponse.build();
    }

    @Override
    public MovieReview toMovieReview(MovieReviewCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        MovieReview.MovieReviewBuilder movieReview = MovieReview.builder();

        movieReview.rating( request.getRating() );
        movieReview.comment( request.getComment() );

        return movieReview.build();
    }

    @Override
    public void updateReview(MovieReview review, MovieReviewUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        review.setRating( request.getRating() );
        review.setComment( request.getComment() );
    }

    @Override
    public MovieReview clone(MovieReview review) {
        if ( review == null ) {
            return null;
        }

        MovieReview.MovieReviewBuilder movieReview = MovieReview.builder();

        movieReview.id( review.getId() );
        movieReview.user( review.getUser() );
        movieReview.movie( review.getMovie() );
        movieReview.rating( review.getRating() );
        movieReview.comment( review.getComment() );
        movieReview.creationTime( review.getCreationTime() );

        return movieReview.build();
    }

    protected UserPreviewResponse userToUserPreviewResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserPreviewResponse.UserPreviewResponseBuilder userPreviewResponse = UserPreviewResponse.builder();

        userPreviewResponse.id( user.getId() );
        userPreviewResponse.fullName( user.getFullName() );
        userPreviewResponse.avatarURL( user.getAvatarURL() );

        return userPreviewResponse.build();
    }
}
