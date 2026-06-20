package com.nmquan1503.backend_springboot.dtos.responses.movie;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieListItemResponse {

    Long id;
    String title;
    String posterURL;
    LocalDate releasedDate;
    Short duration;
    Double averageRating;
    Long ratingCount;
    List<CategoryResponse> categories;
    AgeRatingLabelResponse ageRating;
    Boolean hasBookings;

}
