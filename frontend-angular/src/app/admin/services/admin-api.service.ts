import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../../core/constants/api.constants';

@Injectable({
    providedIn: 'root'
})
export class AdminApiService {
    /**
     * Get the full backend API URL for a given path.
     * @param path The relative path (e.g. 'products' or 'admin/movies')
     */
    getUrl(path: string): string {
        // Clean leading slash if present
        const cleanPath = path.startsWith('/') ? path.substring(1) : path;
        return `${API_BASE_URL}/${cleanPath}`;
    }
}
