import { Routes } from '@angular/router';
import { HomePageComponent } from './pages/home/home-page.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { MoviesPageComponent } from './pages/movies/movies-page.component';
import { MovieDetailPageComponent } from './pages/movie-detail-page/movie-detail-page.component';
import { ShowtimeListPageComponent } from './pages/showtime-list/showtime-list-page.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { LoginPageComponent } from './pages/login/login-page.component';
import { RegisterPageComponent } from './pages/register/register-page.component';
import { NoAuthGuard } from './core/guards/no-auth.guard';
import { ProfilePageComponent } from './pages/profile/profile-page.component';
import { AuthGuard } from './core/guards/auth.guard';
import { BookingPageComponent } from './pages/booking/booking-page.component';
import { CheckoutPageComponent } from './pages/checkout/checkout-page.component';
import { PaymentResultPageComponent } from './pages/payment-result/payment-result-page.component';
import { MyTicketsPageComponent } from './pages/my-tickets/my-tickets-page.component';

import { AdminGuard } from './admin/guards/admin.guard';

export const routes: Routes = [
    {
        path: 'admin',
        canActivate: [AdminGuard],
        loadChildren: () => import('./admin/admin.routes').then(m => m.ADMIN_ROUTES)
    },
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            { path: "home", component: HomePageComponent },
            { path: "movies", component: MoviesPageComponent },
            { path: "movies/detail/:id", component: MovieDetailPageComponent},
            { path: "movies/:id/showtimes", component: ShowtimeListPageComponent},
            { path: "profile", component: ProfilePageComponent, canActivate: [AuthGuard] },
            { path: "my-tickets", component: MyTicketsPageComponent, canActivate: [AuthGuard] },
            { path: "booking/:id", component: BookingPageComponent, canActivate: [AuthGuard]},
            { path: "checkout/:reservationId", component: CheckoutPageComponent, canActivate: [AuthGuard]},
            { path: "payment-result", component: PaymentResultPageComponent }
        ]
    },
    {
        path: '',
        component: AuthLayoutComponent,
        children: [
            { path: 'login', component: LoginPageComponent, canActivate: [NoAuthGuard] },
            { path: 'register', component: RegisterPageComponent, canActivate: [NoAuthGuard] },
        ]
    },
    { path: "**", redirectTo: "home"}
];
