import { Injectable } from "@angular/core";
import { API_BASE_URL } from "../../constants/api.constants";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { ApiResponse } from "../../models/responses/api-response.model";
import { MoviePreviewResponse } from "../../models/responses/movie/movie-preview-response.model";
import { Page } from "../../models/common/page.model";
import { MovieListItemResponse } from "../../models/responses/movie/movie-list-item-response.model";
import { MovieCreationRequest } from "../../models/requests/movie/movie-creation-request.model";
import { MovieUpdateRequest } from "../../models/requests/movie/movie-update-request.model";
import { Pageable } from "../../models/common/pageable.model";
import { PageRequest } from "../../models/requests/page-request.model";
import { buildPageParams } from "../../utils/pagnination.util";
import { MovieDetailResponse } from "../../models/responses/movie/movie-detail-response.model";
import { MovieBannerResponse } from "../../models/responses/movie/movie-banner-response.model";

@Injectable({
    providedIn: 'root'
})
class MovieService {

    private apiUrl = `${API_BASE_URL}/movies`;

    constructor(private http: HttpClient) { }

    getMovieDetail(movieId: number): Observable<ApiResponse<MovieDetailResponse>> {
        return this.http.get<ApiResponse<MovieDetailResponse>> (
            `${this.apiUrl}/details/${movieId}`
        );
    }

    getTrendingMoviePreviews(): Observable<ApiResponse<MoviePreviewResponse[]>> {
        return this.http.get<ApiResponse<MoviePreviewResponse[]>>(
            `${this.apiUrl}/previews/trending`
        );
    }

    getMovieListItems(
        pageable: PageRequest
    ): Observable<ApiResponse<Page<MovieListItemResponse>>> {
        const params = buildPageParams(pageable);
        return this.http.get<ApiResponse<Page<MovieListItemResponse>>>(
            `${this.apiUrl}/list/all`, 
            { params }
        );
    }

    searchMovieCatalog(
        pageable: PageRequest,
        query: string,
        categoryId: number | null
    ): Observable<ApiResponse<Page<MovieListItemResponse>>> {
        let params = buildPageParams(pageable).set('query', query.trim());
        if (categoryId !== null) {
            params = params.set('categoryId', categoryId.toString());
        }
        return this.http.get<ApiResponse<Page<MovieListItemResponse>>>(
            `${this.apiUrl}/catalog/search`,
            { params }
        );
    }

    getNowShowingListItems(
        pageable: PageRequest
    ): Observable<ApiResponse<Page<MovieListItemResponse>>> {
        const params = buildPageParams(pageable);
        return this.http.get<ApiResponse<Page<MovieListItemResponse>>>(
            `${this.apiUrl}/list/now-showing`,
            { params }
        );
    }

    getUpcomingListItems(
        pageable: PageRequest
    ): Observable<ApiResponse<Page<MovieListItemResponse>>> {
        const params = buildPageParams(pageable);
        return this.http.get<ApiResponse<Page<MovieListItemResponse>>>(
            `${this.apiUrl}/list/upcoming`,
            { params }
        );
    }

    createMovie(
        request: MovieCreationRequest
    ): Observable<ApiResponse<void>> {
        return this.http.post<ApiResponse<void>>(
            this.apiUrl, 
            request
        );
    }

    updateMovie(
        movieId: number, 
        request: MovieUpdateRequest
    ): Observable<ApiResponse<void>> {
        return this.http.post<ApiResponse<void>>(
            `${this.apiUrl}/${movieId}`, 
            request
        );
    }

    getMovieBanner(
        movieId: number
    ): Observable<ApiResponse<MovieBannerResponse>> {
        return this.http.get<ApiResponse<MovieBannerResponse>>(
            `${this.apiUrl}/banner/${movieId}`
        );
    }
}

export { MovieService };
