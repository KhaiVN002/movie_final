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
import com.nmquan1503.backend_springboot.entities.movie.MovieImage;
import com.nmquan1503.backend_springboot.repositories.movie.MovieImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {

    private static final short DEFAULT_MOVIE_DURATION_MINUTES = 90;

    // 2.1 Dependencies
    MovieRepository movieRepository;
    MovieImageRepository movieImageRepository;
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
    com.nmquan1503.backend_springboot.repositories.reservation.ReservationRepository reservationRepository;
    com.nmquan1503.backend_springboot.repositories.showtime.ShowtimeRepository showtimeRepository;

    // ── READ ────────────────────────────────────────────────────────────────

    public List<MoviePreviewResponse> getTrendingMoviePreviews() {
        List<Movie> trendingMovies = movieRepository.findTrendingMovies(5);
        return trendingMovies.stream()
                .map(m -> {
                    MoviePreviewResponse res = movieMapper.toMoviePreviewResponse(m);
                    res.setPosterURL(resolveImageUrl(res.getPosterURL()));
                    return res;
                }).toList();
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
        response.setPosterURL(resolveImageUrl(response.getPosterURL()));
        response.setBackdropURL(resolveImageUrl(response.getBackdropURL()));
        return response;
    }

    public MovieBannerResponse getMovieBanner(Long movieId) {
        MovieBannerResponse response = movieMapper.toMovieBannerResponse(
                movieRepository.findById(movieId)
                        .orElseThrow(() -> new GeneralException(ResponseCode.MOVIE_NOT_FOUND))
        );
        response.setBackdropURL(resolveImageUrl(response.getBackdropURL()));
        return response;
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
                                           MultipartFile[] backdrops) {

        Movie movie = movieMapper.toMovie(request);
        movie.setTitle(request.getTitle().trim());
        if (movie.getPosterURL() != null && movie.getPosterURL().contains("/uploads/")) {
            movie.setPosterURL("/uploads/" + movie.getPosterURL().substring(movie.getPosterURL().indexOf("/uploads/") + "/uploads/".length()));
        }
        if (movie.getBackdropURL() != null && movie.getBackdropURL().contains("/uploads/")) {
            movie.setBackdropURL("/uploads/" + movie.getBackdropURL().substring(movie.getBackdropURL().indexOf("/uploads/") + "/uploads/".length()));
        }
        movie.setReleasedDate(defaultReleasedDate(request.getReleasedDate()));
        movie.setDuration(defaultDuration(request.getDuration()));

        movie.setOriginalLanguage(
                languageRepository.findById(defaultOriginalLanguageId(request.getOriginalLanguageId()))
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

        if (backdrops != null && backdrops.length > 0) {
            MultipartFile firstBackdrop = backdrops[0];
            if (firstBackdrop != null && !firstBackdrop.isEmpty()) {
                movie.setBackdropURL(saveFile(firstBackdrop));
            }
        }

        movie = movieRepository.save(movie);

        // Save new backdrops to MovieImage
        if (backdrops != null && backdrops.length > 0) {
            for (MultipartFile file : backdrops) {
                if (file != null && !file.isEmpty()) {
                    String url = saveFile(file);
                    movieImageRepository.save(new MovieImage(null, movie, url));
                }
            }
        }

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            movieCategoryService.save(movie.getId(), request.getCategoryIds());
        }
        MovieDetailResponse response = movieMapper.toMovieDetailResponse(movie);
        addToMovieDetailResponse(response, true, true, true, true, true);
        response.setPosterURL(resolveImageUrl(response.getPosterURL()));
        response.setBackdropURL(resolveImageUrl(response.getBackdropURL()));
        return response;
    }

    // ── UPDATE (2.3) ─────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public MovieDetailResponse updateMovie(Long id, MovieUpdateRequest request) {
        return updateMovie(id, request, null, null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public MovieDetailResponse updateMovie(Long id,
                                           MovieUpdateRequest request,
                                           MultipartFile poster,
                                           MultipartFile[] backdrops) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.MOVIE_NOT_FOUND));

        movieMapper.updateMovie(movie, request);

        if (movie.getPosterURL() != null && movie.getPosterURL().contains("/uploads/")) {
            movie.setPosterURL("/uploads/" + movie.getPosterURL().substring(movie.getPosterURL().indexOf("/uploads/") + "/uploads/".length()));
        }
        if (movie.getBackdropURL() != null && movie.getBackdropURL().contains("/uploads/")) {
            movie.setBackdropURL("/uploads/" + movie.getBackdropURL().substring(movie.getBackdropURL().indexOf("/uploads/") + "/uploads/".length()));
        }

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

        if (poster != null) {
            movie.setPosterURL(saveFile(poster));
        }

        if (backdrops != null && backdrops.length > 0) {
            MultipartFile firstBackdrop = backdrops[0];
            if (firstBackdrop != null && !firstBackdrop.isEmpty()) {
                movie.setBackdropURL(saveFile(firstBackdrop));
            }
        }

        // Handle kept and deleted existing backdrops
        List<MovieImage> existingImages = movieImageRepository.findByMovieId(id);
        List<String> keptUrls = request.getBackdropURLs();
        for (MovieImage img : existingImages) {
            boolean keep = false;
            if (keptUrls != null) {
                for (String keptUrl : keptUrls) {
                    String cleanKeptUrl = keptUrl;
                    if (cleanKeptUrl.contains("/uploads/")) {
                        cleanKeptUrl = "/uploads/" + cleanKeptUrl.substring(cleanKeptUrl.indexOf("/uploads/") + "/uploads/".length());
                    }
                    String cleanImgUrl = img.getImageURL();
                    if (cleanImgUrl.contains("/uploads/")) {
                        cleanImgUrl = "/uploads/" + cleanImgUrl.substring(cleanImgUrl.indexOf("/uploads/") + "/uploads/".length());
                    }
                    if (cleanKeptUrl.equals(cleanImgUrl)) {
                        keep = true;
                        break;
                    }
                }
            }
            if (!keep) {
                movieImageRepository.delete(img);
            }
        }

        // Save new backdrops
        if (backdrops != null && backdrops.length > 0) {
            for (MultipartFile file : backdrops) {
                if (file != null && !file.isEmpty()) {
                    String url = saveFile(file);
                    movieImageRepository.save(new MovieImage(null, movie, url));
                }
            }
        }

        if (request.getAddCategoryIds() != null && !request.getAddCategoryIds().isEmpty()) {
            movieCategoryService.save(movie.getId(), request.getAddCategoryIds());
        }
        if (request.getDeleteCategoryIds() != null && !request.getDeleteCategoryIds().isEmpty()) {
            movieCategoryService.delete(movie.getId(), request.getDeleteCategoryIds());
        }
        movie = movieRepository.save(movie);
        MovieDetailResponse response = movieMapper.toMovieDetailResponse(movie);
        addToMovieDetailResponse(response, true, true, true, true, true);
        response.setPosterURL(resolveImageUrl(response.getPosterURL()));
        response.setBackdropURL(resolveImageUrl(response.getBackdropURL()));
        return response;
    }

    // ── DELETE (2.4) ─────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found");
        }
        List<com.nmquan1503.backend_springboot.entities.showtime.Showtime> showtimes = showtimeRepository.findByMovieId(id);
        for (com.nmquan1503.backend_springboot.entities.showtime.Showtime s : showtimes) {
            if (reservationRepository.existsByShowtimeId(s.getId())) {
                throw new GeneralException(ResponseCode.SHOWTIME_HAS_BOOKINGS);
            }
        }
        showtimeRepository.deleteAll(showtimes);
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
            File dest = new File(dir.getAbsoluteFile(), fileName);

            file.transferTo(dest);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Upload file failed: " + e.getMessage(), e);
        }
    }

    private LocalDate defaultReleasedDate(LocalDate releasedDate) {
        return releasedDate == null ? LocalDate.now() : releasedDate;
    }

    private Short defaultDuration(Short duration) {
        return duration == null || duration <= 0 ? DEFAULT_MOVIE_DURATION_MINUTES : duration;
    }

    private Byte defaultOriginalLanguageId(Byte originalLanguageId) {
        if (originalLanguageId != null) {
            return originalLanguageId;
        }
        return languageRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .findFirst()
                .map(language -> language.getId())
                .orElseThrow(() -> new GeneralException(ResponseCode.LANGUAGE_NOT_FOUND));
    }

    private Page<MovieListItemResponse> convertToPageItem(Page<Movie> movies) {
        List<Long> movieIds = movies.getContent().stream().map(Movie::getId).toList();
        Map<Long, MovieReviewStats> statsMap = movieReviewService.getMapStatsByMovieIds(movieIds);
        Map<Long, List<Category>> categoriesMap = movieCategoryService.fetchCategoryByMovieIds(movieIds);
        List<Long> movieIdsWithReservations = reservationRepository.findMovieIdsWithReservations();
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
            response.setHasBookings(movieIdsWithReservations.contains(movie.getId()));
            response.setPosterURL(resolveImageUrl(response.getPosterURL()));
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

    private String resolveImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String path = url;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try {
            org.springframework.web.servlet.support.ServletUriComponentsBuilder builder = 
                org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath();
            return builder.path(path).toUriString();
        } catch (Exception e) {
            return "http://localhost:8080/api" + path;
        }
    }
}
