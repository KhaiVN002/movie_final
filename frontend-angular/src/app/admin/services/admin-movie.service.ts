import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminApiService } from './admin-api.service';
import { ApiResponse } from '../../core/models/responses/api-response.model';
import { MovieCreationRequest } from '../../core/models/requests/movie/movie-creation-request.model';
import { MovieUpdateRequest } from '../../core/models/requests/movie/movie-update-request.model';

@Injectable({
  providedIn: 'root'
})
export class AdminMovieService {
  constructor(
    private http: HttpClient,
    private apiService: AdminApiService
  ) {}

  getMovies(page: number = 0, size: number = 10, keyword: string = ''): Observable<ApiResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'releasedDate')
      .set('direction', 'DESC');

    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    return this.http.get<ApiResponse<any>>(
      this.apiService.getUrl('admin/movies'),
      { params }
    );
  }

  getMovieDetail(id: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(
      this.apiService.getUrl(`admin/movies/${id}`)
    );
  }

  createMovie(request: MovieCreationRequest, poster?: File, backdrops?: File[]): Observable<void> {
    const formData = new FormData();
    
    // Append JSON request data as a blob
    const dataBlob = new Blob([JSON.stringify(request)], { type: 'application/json' });
    formData.append('data', dataBlob);

    if (poster) {
      formData.append('poster', poster);
    }
    if (backdrops && backdrops.length > 0) {
      backdrops.forEach(file => {
        formData.append('backdrop', file);
      });
    }

    return this.http.post<void>(
      this.apiService.getUrl('admin/movies'),
      formData
    );
  }

  createMovieQuick(request: MovieCreationRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(
      this.apiService.getUrl('admin/movies/quick'),
      request
    );
  }

  updateMovie(id: number, request: MovieUpdateRequest, poster?: File, backdrops?: File[]): Observable<void> {
    const formData = new FormData();
    const dataBlob = new Blob([JSON.stringify(request)], { type: 'application/json' });
    formData.append('data', dataBlob);

    if (poster) {
      formData.append('poster', poster);
    }
    if (backdrops && backdrops.length > 0) {
      backdrops.forEach(file => {
        formData.append('backdrop', file);
      });
    }

    return this.http.put<void>(
      this.apiService.getUrl(`admin/movies/${id}`),
      formData
    );
  }

  deleteMovie(id: number): Observable<void> {
    return this.http.delete<void>(
      this.apiService.getUrl(`admin/movies/${id}`)
    );
  }
}
