package com.nmquan1503.backend_springboot.services.movie;

import com.nmquan1503.backend_springboot.dtos.internal.MovieReviewStats;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.movie.MovieUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.movie.*;
import com.nmquan1503.backend_springboot.entities.movie.Category;
import com.nmquan1503.backend_springboot.entities.movie.Movie;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.movie.CategoryMapper;
import com.nmquan1503.backend_springboot.mappers.movie.MovieMapper;
import com.nmquan1503.backend_springboot.repositories.movie.AgeRatingRepository;
import com.nmquan1503.backend_springboot.repositories.movie.LanguageRepository;
import com.nmquan1503.backend_springboot.repositories.movie.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {

    // 2.1 Dependencies
    MovieRepository movieRepository;
    MovieMapper movieMapper;
    LanguageRepository languageRepository;
    AgeRatingRepository ageRatingRepository;

    // Giữ lại các dependency cũ vẫn cần dùng
    CategoryService categoryService;
    MovieCategoryService movieCategoryService;
    MovieCastService movieCastService;
    MovieCrewService movieCrewService;
    MovieReviewService movieReviewService;
    MovieImageService movieImageService;
    CategoryMapper categoryMapper;

    // ── READ ────────────────────────────────────────────────────────────────

    public List<MoviePreviewResponse> getTrendingMoviePreviews() {
        List<Movie> trendingMovies = movieRepository.findTrendingMovies(5);
        return trendingMovies.stream().map(movieMapper::toMoviePreviewResponse).toList();
    }

    public Page<MovieListItemResponse> getMovieListItems(Pageable pageable) {
        return convertToPageItem(movieRepository.findAllSortByFinalScore(pageable));
    }

    public Page<MovieListItemResponse> getNowShowingMovieListItems(Pageable pageable) {
        return convertToPageItem(movieRepository.findNowShowingSortByFinalScore(pageable));
    }

    public Page<MovieListItemResponse> getUpComingMovieListItems(Pageable pageable) {
        return convertToPageItem(movieRepository.findUpComingSortByFinalScore(pageable));
    }

    public MovieDetailResponse getMovieDetailByMovieId(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new GeneralException(ResponseCode.MOVIE_NOT_FOUND));
        MovieDetailResponse response = movieMapper.toMovieDetailResponse(movie);
        addToMovieDetailResponse(response, true, true, true, true, true);
        return response;
    }

    public MovieBannerResponse getMovieBanner(Long movieId) {
        return movieMapper.toMovieBannerResponse(
                movieRepository.findById(movieId)
                        .orElseThrow(() -> new GeneralException(ResponseCode.MOVIE_NOT_FOUND))
        );
    }

    // ── CREATE (2.2) ─────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public MovieDetailResponse createMovie(MovieCreationRequest request) {
        return createMovie(request, null, null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public MovieDetailResponse createMovie(MovieCreationRequest request,
                                           MultipartFile poster,
                                           MultipartFile backdrop) {

        Movie movie = movieMapper.toMovie(request);

        movie.setOriginalLanguage(
                languageRepository.findById(request.getOriginalLanguageId())
                        .orElseThrow(() -> new GeneralException(ResponseCode.LANGUAGE_NOT_FOUND))
        );

        if (request.getAgeRatingId() != null) {
            movie.setAgeRating(
                    ageRatingRepository.findById(request.getAgeRatingId())
                            .orElseThrow(() -> new GeneralException(ResponseCode.AGE_RATING_NOT_FOUND))
            );
        }

        if (poster != null) {
            movie.setPosterURL(saveFile(poster));
        }

        if (backdrop != null) {
            movie.setBackdropURL(saveFile(backdrop));
        }

        movie = movieRepository.save(movie);
        return movieMapper.toMovieDetailResponse(movie);
    }

    // ── UPDATE (2.3) ─────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public MovieDetailResponse updateMovie(Long id, MovieUpdateRequest request) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.MOVIE_NOT_FOUND));

        movieMapper.updateMovie(movie, request);

        if (request.getOriginalLanguageId() != null) {
            movie.setOriginalLanguage(
                    languageRepository.findById(request.getOriginalLanguageId())
                            .orElseThrow(() -> new GeneralException(ResponseCode.LANGUAGE_NOT_FOUND))
            );
        }

        if (request.getAgeRatingId() != null) {
            movie.setAgeRating(
                    ageRatingRepository.findById(request.getAgeRatingId())
                            .orElseThrow(() -> new GeneralException(ResponseCode.AGE_RATING_NOT_FOUND))
            );
        }

        movie = movieRepository.save(movie);
        return movieMapper.toMovieDetailResponse(movie);
    }

    // ── DELETE (2.4) ─────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    // STEP 3: Upload file
    private String saveFile(MultipartFile file) {
        try {
            String folder = "uploads/";
            File dir = new File(folder);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(folder + fileName);

            file.transferTo(dest);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Upload file failed");
        }
    }

    private Page<MovieListItemResponse> convertToPageItem(Page<Movie> movies) {
        List<Long> movieIds = movies.getContent().stream().map(Movie::getId).toList();
        Map<Long, MovieReviewStats> statsMap = movieReviewService.getMapStatsByMovieIds(movieIds);
        Map<Long, List<Category>> categoriesMap = movieCategoryService.fetchCategoryByMovieIds(movieIds);
        return movies.map(movie -> {
            MovieListItemResponse response = movieMapper.toMovieListItemResponse(movie);
            MovieReviewStats stats = statsMap.get(response.getId());
            if (stats == null) {
                response.setAverageRating(0.0);
                response.setRatingCount(0L);
            } else {
                response.setAverageRating(stats.getAverageRating());
                response.setRatingCount(stats.getCountRating());
            }
            List<Category> categories = categoriesMap.get(response.getId());
            if (categories != null) {
                response.setCategories(categoryMapper.toListCategoryResponse(categories));
            }
            return response;
        });
    }

    private void addToMovieDetailResponse(
            MovieDetailResponse response,
            boolean addStats,
            boolean addCast,
            boolean addCrew,
            boolean addImages,
            boolean addCategories
    ) {
        Long movieId = response.getId();
        if (addStats) {
            MovieReviewStats stats = movieReviewService.getStatsByMovieId(movieId);
            response.setAverageRating(stats.getAverageRating());
            response.setRatingCount(stats.getCountRating());
        }
        if (addCast) {
            response.setCast(movieCastService.getCastByMovieId(movieId, PageRequest.of(0, 10)));
        }
        if (addCrew) {
            response.setCrew(movieCrewService.getCrewByMovieId(movieId, PageRequest.of(0, 10)));
        }
        if (addImages) {
            response.setImages(movieImageService.getMovieImageByMovieId(movieId, PageRequest.of(0, 5)));
        }
        if (addCategories) {
            response.setCategories(categoryService.getCategoriesByMovieId(movieId));
        }
    }

    public boolean existsByMovieId(Long movieId) {
        return movieRepository.existsById(movieId);
    }

    public Movie fetchByMovieId(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new GeneralException(ResponseCode.MOVIE_NOT_FOUND));
    }

    public List<Long> fetchMovieIdsByListIds(List<Long> ids) {
        return movieRepository.findIdsByListIds(ids);
    }
}
