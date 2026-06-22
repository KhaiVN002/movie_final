import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../constants/api.constants';
import { ApiResponse } from '../../models/responses/api-response.model';

export interface SupportRequestResponse {
  id: number;
  userId?: number | null;
  userName: string;
  userEmail?: string | null;
  message: string;
  read: boolean;
  createdAt: string;
  readAt?: string | null;
}

export interface SupportRequestPage {
  content: SupportRequestResponse[];
  totalElements: number;
  totalPages: number;
  last: boolean;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class SupportRequestService {
  private readonly apiUrl = `${API_BASE_URL}/support-requests`;
  private readonly adminApiUrl = `${API_BASE_URL}/admin/support-requests`;

  constructor(private http: HttpClient) {}

  create(message: string): Observable<ApiResponse<number>> {
    return this.http.post<ApiResponse<number>>(this.apiUrl, { message });
  }

  getAdminRequests(page = 0, size = 20, unreadOnly = false): Observable<ApiResponse<SupportRequestPage>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);
    if (unreadOnly) params = params.set('unreadOnly', true);

    return this.http.get<ApiResponse<SupportRequestPage>>(this.adminApiUrl, { params });
  }

  getUnreadCount(): Observable<ApiResponse<number>> {
    return this.http.get<ApiResponse<number>>(`${this.adminApiUrl}/unread-count`);
  }

  markAsRead(id: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.adminApiUrl}/${id}/read`, null);
  }

  markAllAsRead(): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.adminApiUrl}/read-all`, null);
  }
}
