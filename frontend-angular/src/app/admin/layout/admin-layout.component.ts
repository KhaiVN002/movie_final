import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { AuthenticationService } from '../../core/services/authentication/authentication.service';

interface Breadcrumb {
  label: string;
  url: string;
}

@Component({
  standalone: true,
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss'],
  imports: [CommonModule, RouterModule]
})
export class AdminLayoutComponent implements OnInit, OnDestroy {
  adminEmail = 'admin@cgv.vn';
  isSidebarCollapsed = false;
  showProfileDropdown = false;
  showNotifications = false;
  breadcrumbs: Breadcrumb[] = [];
  private routerSub?: Subscription;

  menuGroups = {
    analytics: true,
    cinema: true,
    concessions: true,
    customers: true,
    system: true
  };

  notifications = [
    { id: 1, text: 'New admin data is available.', time: '10 minutes ago', read: false },
    { id: 2, text: 'Showtime list defaults to upcoming screenings.', time: '1 hour ago', read: true },
    { id: 3, text: 'Database indexes have been refreshed.', time: '1 day ago', read: true }
  ];

  private labels: Record<string, string> = {
    dashboard: 'Tổng quan',
    movies: 'Phim',
    showtimes: 'Lịch chiếu',
    cinemas: 'Sơ đồ rạp',
    branches: 'Chi nhánh',
    rooms: 'Phòng chiếu',
    seats: 'Ghế ngồi',
    products: 'Sản phẩm F&B',
    'product-branches': 'Giá F&B theo chi nhánh',
    bookings: 'Đặt vé',
    payments: 'Thanh toán',
    users: 'Người dùng',
    roles: 'Vai trò',
    config: 'Giá vé',
    lookups: 'Cài đặt tra cứu',
    logs: 'Nhật ký hoạt động'
  };

  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {
    const token = this.authService.getAccessToken();
    if (token) {
      const payload = this.decodeJwtPayload(token);
      this.adminEmail = payload?.sub || payload?.email || this.adminEmail;
    }
  }

  ngOnInit(): void {
    this.generateBreadcrumbs(this.router.url);
    this.autoExpandGroup(this.router.url);
    this.routerSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.urlAfterRedirects || event.url;
      this.generateBreadcrumbs(url);
      this.autoExpandGroup(url);
    });
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
  }

  toggleSidebar(): void {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  toggleGroup(group: keyof typeof this.menuGroups): void {
    this.menuGroups[group] = !this.menuGroups[group];
  }

  toggleProfileDropdown(): void {
    this.showProfileDropdown = !this.showProfileDropdown;
    if (this.showProfileDropdown) {
      this.showNotifications = false;
    }
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.showProfileDropdown = false;
    }
  }

  markAllNotificationsAsRead(): void {
    this.notifications.forEach(item => item.read = true);
  }

  get unreadNotificationsCount(): number {
    return this.notifications.filter(item => !item.read).length;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }

  private autoExpandGroup(url: string): void {
    if (url.includes('/admin/dashboard')) this.menuGroups.analytics = true;
    if (url.includes('/admin/movies') || url.includes('/admin/showtimes') || url.includes('/admin/cinemas') || url.includes('/admin/branches') || url.includes('/admin/rooms') || url.includes('/admin/seats')) this.menuGroups.cinema = true;
    if (url.includes('/admin/products') || url.includes('/admin/product-branches')) this.menuGroups.concessions = true;
    if (url.includes('/admin/users') || url.includes('/admin/roles') || url.includes('/admin/bookings') || url.includes('/admin/payments')) this.menuGroups.customers = true;
    if (url.includes('/admin/config') || url.includes('/admin/lookups') || url.includes('/admin/logs')) this.menuGroups.system = true;
  }

  private generateBreadcrumbs(url: string): void {
    const parts = url.split('/').filter(part => part && part !== 'admin');
    let currentUrl = '/admin';
    this.breadcrumbs = [{ label: 'Admin', url: '/admin/dashboard' }];

    parts.forEach(part => {
      currentUrl += `/${part}`;
      this.breadcrumbs.push({
        label: this.labels[part] || this.toTitle(part),
        url: currentUrl
      });
    });
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

  private toTitle(value: string): string {
    return value
      .split('-')
      .map(part => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}
