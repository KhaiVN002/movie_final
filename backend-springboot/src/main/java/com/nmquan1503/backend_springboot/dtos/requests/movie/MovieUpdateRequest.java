package com.nmquan1503.backend_springboot.dtos.requests.movie;

import com.nmquan1503.backend_springboot.validators.UniqueElements;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieUpdateRequest {

    String title;
    String posterURL;
    String backdropURL;
    List<String> backdropURLs;
    String trailerURL;
    String tagline;
    String overview;
    LocalDate releasedDate;
    Short duration;
    Byte originalLanguageId;
    Byte ageRatingId;

    @UniqueElements(message = "DUPLICATE_CAST_MEMBER")
    List<MovieCastCreationRequest> createCast;

    @UniqueElements(message = "DUPLICATE_CAST_MEMBER")
    List<MovieCastUpdateRequest> updateCast;

    @UniqueElements(message = "DUPLICATE_CAST_MEMBER")
    List<Long> deleteCast;

    @UniqueElements(message = "DUPLICATE_CREW_MEMBER")
    List<MovieCrewCreationRequest> createCrew;

    @UniqueElements(message = "DUPLICATE_CREW_MEMBER")
    List<MovieCrewUpdateRequest> updateCrew;

    @UniqueElements(message = "DUPLICATE_CREW_MEMBER")
    List<Long> deleteCrew;

    @UniqueElements(message = "DUPLICATE_CATEGORY")
    List<Byte> addCategoryIds;

    @UniqueElements(message = "DUPLICATE_CATEGORY")
    List<Byte> deleteCategoryIds;

}
