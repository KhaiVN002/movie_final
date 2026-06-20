import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminApiService } from './admin-api.service';
import { ApiResponse } from '../../core/models/responses/api-response.model';
import { BranchOptionResponse } from '../../core/models/responses/theater/branch-option-response.model';

@Injectable({
  providedIn: 'root'
})
export class AdminShowtimeService {
  constructor(
    private http: HttpClient,
    private apiService: AdminApiService
  ) {}

  getShowtimes(page: number = 0, size: number = 10, search: string = ''): Observable<ApiResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'startTime')
      .set('direction', 'ASC');

    if (search && search.trim() !== '') {
      params = params.set('keyword', search.trim());
    }

    return this.http.get<ApiResponse<any>>(
      this.apiService.getUrl('admin/showtimes'),
      { params }
    );
  }

  createShowtime(request: { movieId: number; roomId: number; startTime: string }): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      this.apiService.getUrl('admin/showtimes'),
      request
    );
  }

  updateShowtime(showtimeId: number, statusId: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      this.apiService.getUrl(`admin/showtimes/${showtimeId}`),
      { showtimeStatusId: statusId }
    );
  }

  deleteShowtime(showtimeId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      this.apiService.getUrl(`admin/showtimes/${showtimeId}`)
    );
  }

  deleteShowtimesBulk(showtimeIds: number[]): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      this.apiService.getUrl('admin/showtimes/bulk'),
      { body: showtimeIds }
    );
  }

  deleteShowtimesByMovieTitle(title: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      this.apiService.getUrl('admin/showtimes/by-movie-title'),
      { params: new HttpParams().set('title', title) }
    );
  }

  getBranches(): Observable<ApiResponse<BranchOptionResponse[]>> {
    return this.http.get<ApiResponse<BranchOptionResponse[]>>(
      this.apiService.getUrl('branches/options')
    );
  }

  getRoomsByBranchId(branchId: number): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(
      this.apiService.getUrl(`branches/${branchId}/rooms`)
    );
  }

  getMoviesList(keyword: string = ''): Observable<ApiResponse<any>> {
    let params = new HttpParams()
      .set('page', '0')
      .set('size', '100')
      .set('sort', 'title')
      .set('direction', 'ASC');

    if (keyword.trim() !== '') {
      params = params.set('keyword', keyword.trim());
    }

    return this.http.get<ApiResponse<any>>(
      this.apiService.getUrl('admin/movies'),
      { params }
    );
  }
}
