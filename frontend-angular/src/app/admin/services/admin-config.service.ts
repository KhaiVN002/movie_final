import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminApiService } from './admin-api.service';
import { ApiResponse } from '../../core/models/responses/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class AdminConfigService {
  constructor(
    private http: HttpClient,
    private apiService: AdminApiService
  ) {}

  getTicketPrices(): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(
      this.apiService.getUrl('ticket-price')
    );
  }

  updateTicketPrice(id: number, price: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      this.apiService.getUrl(`ticket-price/${id}`),
      price // Send raw double value matching the RequestBody in controller
    );
  }
}
