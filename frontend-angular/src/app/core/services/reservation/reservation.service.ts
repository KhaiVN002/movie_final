import { Injectable } from "@angular/core";
import { API_BASE_URL } from "../../constants/api.constants";
import { HttpClient } from "@angular/common/http";
import { ReservationCreationRequest } from "../../models/requests/reservation/reservation-creation-request.model";
import { Observable } from "rxjs";
import { ApiResponse } from "../../models/responses/api-response.model";
import { ReservationDetailResponse } from "../../models/responses/reservation/reservation-detail-response.model";

@Injectable({
    providedIn: 'root'
})
class ReservationService {

    private apiUrl = `${API_BASE_URL}/reservations`;

    constructor(private http: HttpClient) { }

    createReservation(
        request: ReservationCreationRequest
    ): Observable<ApiResponse<number>> {
        return this.http.post<ApiResponse<number>>(
            this.apiUrl, 
            request
        );
    }

    getReservationDetail(
        reservationId: number
    ): Observable<ApiResponse<ReservationDetailResponse>> {
        return this.http.get<ApiResponse<ReservationDetailResponse>>(
            `${this.apiUrl}/detail/${reservationId}`
        );
    }

    getMyReservations(): Observable<ApiResponse<ReservationDetailResponse[]>> {
        return this.http.get<ApiResponse<ReservationDetailResponse[]>>(
            `${this.apiUrl}/my`
        );
    }
}

export { ReservationService };