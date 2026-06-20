import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminApiService } from './admin-api.service';
import { ApiResponse } from '../../core/models/responses/api-response.model';
import { ProductCreationRequest } from '../../core/models/requests/product/product-creation-request.model';
import { ProductUpdateRequest } from '../../core/models/requests/product/product-update-request.model';
import { BranchProductCreationRequest } from '../../core/models/requests/product/branch-product-creation-request.model';
import { BranchOptionResponse } from '../../core/models/responses/theater/branch-option-response.model';

@Injectable({
  providedIn: 'root'
})
export class AdminProductService {
  constructor(
    private http: HttpClient,
    private apiService: AdminApiService
  ) {}

  getProducts(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(
      this.apiService.getUrl('admin/products?page=0&size=100&sort=name&direction=ASC')
    );
  }

  createProduct(request: ProductCreationRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      this.apiService.getUrl('admin/products'),
      request
    );
  }

  updateProduct(id: number, request: ProductUpdateRequest): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(
      this.apiService.getUrl(`admin/products/${id}`),
      request
    );
  }

  getBranches(): Observable<ApiResponse<BranchOptionResponse[]>> {
    return this.http.get<ApiResponse<BranchOptionResponse[]>>(
      this.apiService.getUrl('branches/options')
    );
  }

  assignProductToBranch(request: BranchProductCreationRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(
      this.apiService.getUrl('admin/product-branches'),
      request
    );
  }
}
