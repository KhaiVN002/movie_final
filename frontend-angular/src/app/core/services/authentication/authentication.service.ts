import { Injectable, OnDestroy } from "@angular/core";
import { API_BASE_URL } from "../../constants/api.constants";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { ApiResponse } from "../../models/responses/api-response.model";
import { AuthenticationResponse } from "../../models/responses/authentication/authentication-response.model";
import { BehaviorSubject, catchError, EMPTY, finalize, interval, Observable, Subscription, switchMap, tap } from "rxjs";
import { AuthenticationRequest } from "../../models/requests/authentication/authentication-request.model";
import { WindowService } from "../platform-browser/window.service";
import { LocalStorageService } from "../platform-browser/local-storage.service";

@Injectable({
    providedIn: 'root'
})
class AuthenticationService implements OnDestroy {

    private apiUrl = `${API_BASE_URL}/auth`;

    private refreshSub: Subscription | null = null;

    private loggedIn = new BehaviorSubject<boolean>(false);
    private refreshing = new BehaviorSubject<boolean>(true);

    isLogined$ = this.loggedIn.asObservable();
    isRefreshing$ = this.refreshing.asObservable();

    constructor(
        private http: HttpClient, 
        private windowService: WindowService, 
        private localStorageService: LocalStorageService
    ) {
        this.windowService.addEventListener('beforeunload', () => {
            this.stopTokenRefresh();
        });
    }

    init(): Promise<void> {
        return new Promise((resolve) => {
            const accessToken = this.getAccessToken();
            if (!accessToken) {
                this.tryRefresh().subscribe({ complete: resolve });
                return;
            }

            this.introspect(accessToken).subscribe({
                next: response => {
                    if (response.success && response.data?.valid) {
                        this.loggedIn.next(true);
                        this.startTokenRefresh();
                        this.refreshing.next(false);
                        resolve();
                        return;
                    }
                    this.tryRefresh().subscribe({ complete: resolve });
                },
                error: () => this.tryRefresh().subscribe({ complete: resolve })
            });
        });
    }

    private introspect(accessToken: string): Observable<ApiResponse<{ valid: boolean }>> {
        return this.http.post<ApiResponse<{ valid: boolean }>>(
            `${this.apiUrl}/introspect`,
            { accessToken }
        );
    }

    tryRefresh(): Observable<ApiResponse<AuthenticationResponse>> {
        return this.refresh().pipe(
            tap(response => {
                if (response.success && response.code===1000 && response.data) {
                    this.setAccessToken(response.data.accessToken);
                    this.startTokenRefresh();
                    this.loggedIn.next(true);
                }
                else {
                    console.log(`api error: ${response.message}`);
                    this.clearAuthenticationState();
                }
            }),
            catchError(() => {
                this.clearAuthenticationState();
                return EMPTY;
            }),
            finalize(() => this.refreshing.next(false))
        );
    }
    
    authenticate(
        request: AuthenticationRequest
    ): Observable<ApiResponse<AuthenticationResponse>> {
        return this.http.post<ApiResponse<AuthenticationResponse>>(
            `${this.apiUrl}/login`, 
            request,
            { withCredentials: true }
        ).pipe(
            tap(response => {
                if (response.success && response.code===1000 && response.data) {
                    this.setAccessToken(response.data.accessToken);
                    this.startTokenRefresh();
                    this.loggedIn.next(true);
                }
                else {
                    this.loggedIn.next(false);
                    this.removeAccessToken();
                }
            })
        )
    }

    refresh(): Observable<ApiResponse<AuthenticationResponse>> {
        return this.http.post<ApiResponse<AuthenticationResponse>>(
            `${this.apiUrl}/refresh`, 
            null, 
            { withCredentials: true }
        );
    }

    logout(): Observable<ApiResponse<void>> {
        const token = this.getAccessToken();

        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });
        return this.http.post<ApiResponse<void>>(
            `${this.apiUrl}/logout`, 
            null, 
            { 
                headers,
                withCredentials: true
            }
        ).pipe(
            tap((response) => {
                if (!response.success) {
                    console.log(`api error: ${response.message}`);
                }
            }),
            finalize(() => this.clearAuthenticationState())
        );
    }

    private setAccessToken(token: string): void {
        this.localStorageService.setItem('access_token', token);
    }

    private removeAccessToken(): void {
        this.localStorageService.removeItem('access_token');
    }

    getAccessToken(): string | null {
        return this.localStorageService.getItem('access_token');
    }

    private startTokenRefresh(): void {
        this.stopTokenRefresh();
        this.refreshSub = interval(12 * 60 * 1000).pipe(
            switchMap(() => this.refresh()),
            catchError(err => {
                this.clearAuthenticationState();
                return EMPTY;
            })
        ).subscribe(response => {
            if (response.success && response.code===1000 && response.data) {
                this.setAccessToken(response.data.accessToken);
                this.loggedIn.next(true);
            }
            else {
                this.clearAuthenticationState();
            }
        });
    }

    private clearAuthenticationState(): void {
        this.removeAccessToken();
        this.stopTokenRefresh();
        this.loggedIn.next(false);
    }

    private stopTokenRefresh(): void {
        this.refreshSub?.unsubscribe();
        this.refreshSub = null;
    }

    ngOnDestroy(): void {
        this.stopTokenRefresh();
    }

    get isLoggedIn(): boolean {
        return this.loggedIn.value;
    }

    get isRefreshing(): boolean {
        return this.refreshing.value;
    }


}

export { AuthenticationService };
