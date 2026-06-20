import { Injectable } from "@angular/core";
import { API_BASE_URL } from "../../constants/api.constants";
import { HttpClient, HttpParams } from "@angular/common/http";
import { UserCreationRequest } from "../../models/requests/user/user-creation-request.model";
import { Observable } from "rxjs";
import { ApiResponse } from "../../models/responses/api-response.model";
import { UserUpdateRequest } from "../../models/requests/user/user-update-request.model";
import { UserDetailResponse } from "../../models/responses/user/user-detail-response.model";
import { Pageable } from "../../models/common/pageable.model";
import { Page } from "../../models/common/page.model";
import { UserPreviewResponse } from "../../models/responses/user/user-preview-response.model";
import { PageRequest } from "../../models/requests/page-request.model";
import { buildPageParams } from "../../utils/pagnination.util";

@Injectable({
    providedIn: 'root'
})
class UserService {

    private apiUrl = `${API_BASE_URL}/users`;

    constructor(private http: HttpClient) { }

    createUser(
        request: UserCreationRequest
    ): Observable<ApiResponse<void>> {
        return this.http.post<ApiResponse<void>>(
            this.apiUrl, 
            request
        );
    }

    updateUser(
        userId: number, 
        request: UserUpdateRequest
    ): Observable<ApiResponse<UserDetailResponse>> {
        return this.http.put<ApiResponse<UserDetailResponse>>(
            `${this.apiUrl}/${userId}`, 
            request
        );
    }

    getMyInfo(): Observable<ApiResponse<UserDetailResponse>> {
        return this.http.get<ApiResponse<UserDetailResponse>>(
            `${this.apiUrl}/myInfo`
        );
    }

    getUserPreviews(
        pageable: PageRequest
    ): Observable<ApiResponse<Page<UserPreviewResponse>>> {
        const params = buildPageParams(pageable);
        return this.http.get<ApiResponse<Page<UserPreviewResponse>>>(
            `${this.apiUrl}/previews`, 
            { params }
        );
    }

    getMyInfoPreview(): Observable<ApiResponse<UserPreviewResponse>> {
        return this.http.get<ApiResponse<UserPreviewResponse>>(
            `${this.apiUrl}/myInfo/preview`
        );
    }

    uploadAvatar(userId: number, file: File): Observable<ApiResponse<string>> {
        const formData = new FormData();
        formData.append("file", file);
        return this.http.post<ApiResponse<string>>(`${this.apiUrl}/${userId}/avatar`, formData);
    }
}

export { UserService }