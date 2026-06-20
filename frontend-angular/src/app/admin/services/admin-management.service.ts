import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminApiService } from './admin-api.service';
import { ApiResponse } from '../../core/models/responses/api-response.model';

export interface AdminPageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface AdminPageQuery {
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'ASC' | 'DESC';
  keyword?: string;
  status?: string;
  branchId?: number;
  roomId?: number;
  roleId?: number;
  fromDate?: string;
  toDate?: string;
}

export interface AdminLookupOption {
  id: number;
  name: string;
  code?: string;
}

export interface AdminLookupGroup {
  key: string;
  label: string;
  options: AdminLookupOption[];
}

export interface AdminDashboardResponse {
  movieCount: number;
  upcomingShowtimeCount: number;
  userCount: number;
  reservationCount: number;
  paidReservationCount: number;
  revenueToday: number;
  revenueLast7Days: number;
  recentPayments: any[];
}

@Injectable({
  providedIn: 'root'
})
export class AdminManagementService {
  constructor(
    private http: HttpClient,
    private apiService: AdminApiService
  ) {}

  getDashboard(): Observable<ApiResponse<AdminDashboardResponse>> {
    return this.http.get<ApiResponse<AdminDashboardResponse>>(
      this.apiService.getUrl('admin/dashboard')
    );
  }

  getPage<T>(resource: string, query: AdminPageQuery = {}): Observable<ApiResponse<AdminPageResponse<T>>> {
    return this.http.get<ApiResponse<AdminPageResponse<T>>>(
      this.apiService.getUrl(`admin/${resource}`),
      { params: this.toParams(query) }
    );
  }

  getLookups(): Observable<ApiResponse<AdminLookupGroup[]>> {
    return this.http.get<ApiResponse<AdminLookupGroup[]>>(
      this.apiService.getUrl('admin/lookups')
    );
  }

  updateUserRoles(userId: number, roleIds: number[]): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      this.apiService.getUrl(`admin/users/${userId}/roles`),
      { roleIds }
    );
  }

  private toParams(query: AdminPageQuery): HttpParams {
    let params = new HttpParams();
    Object.entries(query).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, String(value));
      }
    });
    return params;
  }
}
