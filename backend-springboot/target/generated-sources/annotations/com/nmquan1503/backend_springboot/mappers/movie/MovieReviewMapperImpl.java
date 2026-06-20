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
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MovieReviewMapperImpl implements MovieReviewMapper {

    @Override
    public MovieReviewResponse toMovieReviewSummaryResponse(MovieReview movieReview) {
        if ( movieReview == null ) {
            return null;
        }

        MovieReviewResponse.MovieReviewResponseBuilder movieReviewResponse = MovieReviewResponse.builder();

        movieReviewResponse.comment( movieReview.getComment() );
        movieReviewResponse.creationTime( movieReview.getCreationTime() );
        movieReviewResponse.id( movieReview.getId() );
        movieReviewResponse.rating( movieReview.getRating() );
        movieReviewResponse.user( userToUserPreviewResponse( movieReview.getUser() ) );

        return movieReviewResponse.build();
    }

    @Override
    public MovieReview toMovieReview(MovieReviewCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        MovieReview.MovieReviewBuilder movieReview = MovieReview.builder();

        movieReview.comment( request.getComment() );
        movieReview.rating( request.getRating() );

        return movieReview.build();
    }

    @Override
    public void updateReview(MovieReview review, MovieReviewUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        review.setComment( request.getComment() );
        review.setRating( request.getRating() );
    }

    @Override
    public MovieReview clone(MovieReview review) {
        if ( review == null ) {
            return null;
        }

        MovieReview.MovieReviewBuilder movieReview = MovieReview.builder();

        movieReview.comment( review.getComment() );
        movieReview.creationTime( review.getCreationTime() );
        movieReview.id( review.getId() );
        movieReview.movie( review.getMovie() );
        movieReview.rating( review.getRating() );
        movieReview.user( review.getUser() );

        return movieReview.build();
    }

    protected UserPreviewResponse userToUserPreviewResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserPreviewResponse.UserPreviewResponseBuilder userPreviewResponse = UserPreviewResponse.builder();

        userPreviewResponse.avatarURL( user.getAvatarURL() );
        userPreviewResponse.fullName( user.getFullName() );
        userPreviewResponse.id( user.getId() );

        return userPreviewResponse.build();
    }
}
