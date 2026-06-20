import { Inject, Injectable, PLATFORM_ID } from "@angular/core";
import { isPlatformBrowser } from "@angular/common";
import { CanActivate, Router } from "@angular/router";
import { AuthenticationService } from "../../core/services/authentication/authentication.service";

@Injectable({
    providedIn: 'root',
})
export class AdminGuard implements CanActivate {
    constructor(
        private authService: AuthenticationService,
        private router: Router,
        @Inject(PLATFORM_ID) private platformId: Object
    ) {}

    canActivate(): boolean {
        if (!isPlatformBrowser(this.platformId)) {
            return false;
        }

        const token = this.authService.getAccessToken();
        if (!token) {
            this.router.navigate(['/login']);
            return false;
        }

        const payload = this.decodeJwtPayload(token);
        if (payload && payload.scope && payload.scope.split(' ').includes('ROLE_ADMIN')) {
            return true;
        }

        this.router.navigate(['/home']);
        return false;
    }

    private decodeJwtPayload(token: string): any {
        try {
            const payload = token.split('.')[1];
            if (!payload) return null;
            const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
            const paddedBase64 = base64.padEnd(base64.length + (4 - base64.length % 4) % 4, '=');
            return JSON.parse(atob(paddedBase64));
        } catch {
            return null;
        }
    }
}
