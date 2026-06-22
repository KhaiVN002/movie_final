import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './layout/admin-layout.component';
import { AdminDashboardComponent } from './pages/dashboard/admin-dashboard.component';
import { AdminMoviesComponent } from './pages/movies/admin-movies.component';
import { AdminShowtimesComponent } from './pages/showtimes/admin-showtimes.component';
import { AdminProductsComponent } from './pages/products/admin-products.component';
import { AdminConfigComponent } from './pages/config/admin-config.component';
import { AdminCinemasComponent } from './pages/cinemas/admin-cinemas.component';
import { AdminBookingsComponent } from './pages/bookings/admin-bookings.component';
import { AdminUsersComponent } from './pages/users/admin-users.component';
import { AdminLogsComponent } from './pages/logs/admin-logs.component';
import { AdminResourceListComponent } from './pages/resource-list/admin-resource-list.component';
import { AdminSupportComponent } from './pages/support/admin-support.component';

export const ADMIN_ROUTES: Routes = [
    {
        path: '',
        component: AdminLayoutComponent,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', component: AdminDashboardComponent },
            { path: 'movies', component: AdminMoviesComponent },
            { path: 'showtimes', component: AdminShowtimesComponent },
            { path: 'products', component: AdminProductsComponent },
            {
                path: 'product-branches',
                component: AdminResourceListComponent,
                data: {
                    title: 'Product Branch Pricing',
                    subtitle: 'Manage which F&B products are available at each branch.',
                    resource: 'product-branches',
                    columns: [
                        { key: 'id', label: 'ID' },
                        { key: 'branchName', label: 'Branch' },
                        { key: 'productName', label: 'Product' },
                        { key: 'status', label: 'Status', type: 'status' }
                    ]
                }
            },
            { path: 'config', component: AdminConfigComponent },
            { path: 'cinemas', component: AdminCinemasComponent },
            {
                path: 'branches',
                component: AdminResourceListComponent,
                data: {
                    title: 'Branches',
                    subtitle: 'List cinema branches with room and seat coverage.',
                    resource: 'branches',
                    columns: [
                        { key: 'id', label: 'ID' },
                        { key: 'name', label: 'Branch' },
                        { key: 'province', label: 'Province' },
                        { key: 'district', label: 'District' },
                        { key: 'roomCount', label: 'Rooms' },
                        { key: 'seatCount', label: 'Seats' },
                        { key: 'status', label: 'Status', type: 'status' }
                    ]
                }
            },
            {
                path: 'rooms',
                component: AdminResourceListComponent,
                data: {
                    title: 'Rooms',
                    subtitle: 'Scan screening rooms by branch, room type, status, and capacity.',
                    resource: 'rooms',
                    columns: [
                        { key: 'id', label: 'ID' },
                        { key: 'branchName', label: 'Branch' },
                        { key: 'name', label: 'Room' },
                        { key: 'roomType', label: 'Type' },
                        { key: 'rowCount', label: 'Rows' },
                        { key: 'columnCount', label: 'Columns' },
                        { key: 'seatCount', label: 'Seats' },
                        { key: 'status', label: 'Status', type: 'status' }
                    ]
                }
            },
            {
                path: 'seats',
                component: AdminResourceListComponent,
                data: {
                    title: 'Seats',
                    subtitle: 'Inspect seat positions, seat types, and operational status.',
                    resource: 'seats',
                    columns: [
                        { key: 'id', label: 'ID' },
                        { key: 'branchName', label: 'Branch' },
                        { key: 'roomName', label: 'Room' },
                        { key: 'name', label: 'Seat' },
                        { key: 'positionX', label: 'X' },
                        { key: 'positionY', label: 'Y' },
                        { key: 'type', label: 'Type' },
                        { key: 'status', label: 'Status', type: 'status' }
                    ]
                }
            },
            { path: 'bookings', component: AdminBookingsComponent },
            {
                path: 'payments',
                component: AdminResourceListComponent,
                data: {
                    title: 'Payments',
                    subtitle: 'Monitor payment transactions, methods, statuses, and amounts.',
                    resource: 'payments',
                    columns: [
                        { key: 'transactionId', label: 'Transaction' },
                        { key: 'reservationId', label: 'Booking' },
                        { key: 'customerEmail', label: 'Customer' },
                        { key: 'movieTitle', label: 'Movie' },
                        { key: 'paymentMethod', label: 'Method' },
                        { key: 'amount', label: 'Amount', type: 'money' },
                        { key: 'time', label: 'Time', type: 'date' },
                        { key: 'status', label: 'Status', type: 'status' }
                    ]
                }
            },
            { path: 'users', component: AdminUsersComponent },
            {
                path: 'roles',
                component: AdminResourceListComponent,
                data: {
                    title: 'Roles',
                    subtitle: 'Review role usage and permission counts.',
                    resource: 'roles',
                    columns: [
                        { key: 'id', label: 'ID' },
                        { key: 'name', label: 'Role' },
                        { key: 'userCount', label: 'Users' },
                        { key: 'permissionCount', label: 'Permissions' }
                    ]
                }
            },
            {
                path: 'lookups',
                component: AdminResourceListComponent,
                data: {
                    title: 'Lookup Settings',
                    subtitle: 'Reference statuses, ratings, languages, room types, and other lookup tables.',
                    resource: 'lookups',
                    columns: []
                }
            },
            { path: 'logs', component: AdminLogsComponent },
            { path: 'support', component: AdminSupportComponent }
        ]
    }
];
