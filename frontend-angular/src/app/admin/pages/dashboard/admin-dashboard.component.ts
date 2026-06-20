import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { AdminProductService } from '../../services/admin-product.service';
import { AdminManagementService } from '../../services/admin-management.service';

interface DailyRevenue {
  day: string;
  amount: number;
}

interface MovieTicketStat {
  title: string;
  ticketsSold: number;
  ticketsLeft: number;
  revenue: number;
  percentage: number;
  color: string;
}

interface RecentBuyer {
  id: string;
  email: string;
  movieTitle: string;
  seats: string;
  amount: number;
  time: string;
  status: 'SUCCESS' | 'PAID' | 'PENDING' | 'EXPIRED' | 'FAILED';
}

@Component({
  standalone: true,
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
  imports: [CommonModule]
})
export class AdminDashboardComponent implements OnInit {
  isLoading = true;
  movieCount = 0;
  productCount = 0;
  showtimeCount = 0;
  revenueToday = 0;
  activeUsers = 0;
  seatOccupancyRate = 0;
  circumference = 2 * Math.PI * 40;

  recentBuyers: RecentBuyer[] = [];
  dailyRevenue: DailyRevenue[] = [];
  movieStats: MovieTicketStat[] = [];

  revenueChartPath = '';
  revenueChartPoints: { x: number; y: number; label: string; value: number }[] = [];

  constructor(
    private productService: AdminProductService,
    private adminService: AdminManagementService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  get strokeDashoffset(): number {
    return this.circumference - (this.seatOccupancyRate / 100) * this.circumference;
  }

  loadDashboardData(): void {
    this.isLoading = true;

    forkJoin({
      dashboardRes: this.adminService.getDashboard(),
      productsRes: this.productService.getProducts()
    }).subscribe({
      next: ({ dashboardRes, productsRes }) => {
        if (dashboardRes?.success && dashboardRes.data) {
          const data = dashboardRes.data;
          this.movieCount = data.movieCount || 0;
          this.showtimeCount = data.upcomingShowtimeCount || 0;
          this.activeUsers = data.userCount || 0;
          this.revenueToday = Number(data.revenueToday || 0);
          this.seatOccupancyRate = this.calculateOccupancy(data.paidReservationCount, data.reservationCount);
          this.recentBuyers = (data.recentPayments || []).map((payment: any) => ({
            id: payment.transactionId,
            email: payment.customerEmail,
            movieTitle: payment.movieTitle,
            seats: '-',
            amount: Number(payment.amount || 0),
            time: payment.time,
            status: payment.status
          }));
          this.prepareRevenueFallback(Number(data.revenueLast7Days || 0));
        }

        if (productsRes?.success) {
          this.productCount = productsRes.data?.totalElements || productsRes.data?.content?.length || 0;
        }

        this.prepareFallbackMovieStats();
        this.prepareFallbackRecentBuyers();
        this.isLoading = false;
      },
      error: () => {
        this.prepareRevenueFallback(0);
        this.prepareFallbackMovieStats();
        this.prepareFallbackRecentBuyers();
        this.isLoading = false;
      }
    });
  }

  private calculateOccupancy(paidReservations: number, reservations: number): number {
    if (!reservations) {
      return 0;
    }
    return Math.min(100, Math.round((paidReservations / reservations) * 100));
  }

  private prepareRevenueFallback(totalLast7Days: number): void {
    const average = totalLast7Days > 0 ? totalLast7Days / 7 : this.revenueToday;
    this.dailyRevenue = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((day, index) => ({
      day,
      amount: Math.round(average * (0.75 + index * 0.08))
    }));
    this.calculateRevenueChart();
  }

  private prepareFallbackMovieStats(): void {
    if (this.movieStats.length > 0) {
      return;
    }
    this.movieStats = [
      { title: 'Top movie', ticketsSold: 0, ticketsLeft: 0, revenue: this.revenueToday, percentage: this.seatOccupancyRate, color: '#ef4444' }
    ];
  }

  private prepareFallbackRecentBuyers(): void {
    if (this.recentBuyers.length > 0) {
      return;
    }
    this.recentBuyers = [
      {
        id: '-',
        email: 'No recent payments',
        movieTitle: '-',
        seats: '-',
        amount: 0,
        time: '-',
        status: 'PENDING'
      }
    ];
  }

  private calculateRevenueChart(): void {
    const width = 500;
    const height = 150;
    const padding = 20;
    const values = this.dailyRevenue.map(d => d.amount);
    const maxVal = Math.max(...values, 1) * 1.1;
    const minVal = Math.min(...values, 0) * 0.9;
    const pointsCount = this.dailyRevenue.length;
    const xStep = (width - padding * 2) / Math.max(pointsCount - 1, 1);

    this.revenueChartPoints = this.dailyRevenue.map((d, i) => {
      const x = padding + i * xStep;
      const y = height - padding - ((d.amount - minVal) / Math.max(maxVal - minVal, 1)) * (height - padding * 2);
      return { x, y, label: d.day, value: d.amount };
    });

    if (this.revenueChartPoints.length > 0) {
      let path = `M ${this.revenueChartPoints[0].x} ${this.revenueChartPoints[0].y}`;
      for (let i = 1; i < this.revenueChartPoints.length; i++) {
        const prev = this.revenueChartPoints[i - 1];
        const curr = this.revenueChartPoints[i];
        const cpX1 = prev.x + xStep / 2;
        const cpY1 = prev.y;
        const cpX2 = curr.x - xStep / 2;
        const cpY2 = curr.y;
        path += ` C ${cpX1} ${cpY1}, ${cpX2} ${cpY2}, ${curr.x} ${curr.y}`;
      }
      this.revenueChartPath = path;
    }
  }
}
