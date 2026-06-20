import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminManagementService } from '../../services/admin-management.service';

interface ConcessionItem {
  name: string;
  quantity: number;
  price: number;
}

interface Booking {
  id: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  movieTitle: string;
  showtime: string;
  seats: string;
  concessions: ConcessionItem[];
  amount: number;
  status: 'PAID' | 'PENDING' | 'EXPIRED';
  paymentMethod: string;
  bookingDate: string;
}

@Component({
  standalone: true,
  selector: 'app-admin-bookings',
  templateUrl: './admin-bookings.component.html',
  styleUrls: ['./admin-bookings.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class AdminBookingsComponent implements OnInit {
  bookings: Booking[] = [];
  filteredBookings: Booking[] = [];
  searchQuery = '';
  statusFilter = 'ALL';
  page = 0;
  size = 20;
  totalElements = 0;
  totalPages = 0;
  isLoading = false;
  errorMessage = '';

  selectedBooking: Booking | null = null;
  showDetailModal = false;

  constructor(private adminService: AdminManagementService) {}

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getPage<any>('bookings', {
      page: this.page,
      size: this.size,
      keyword: this.searchQuery,
      status: this.statusFilter === 'ALL' ? undefined : this.statusFilter,
      sort: 'bookedAt',
      direction: 'DESC'
    }).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.bookings = (res.data.content || []).map((b: any) => ({
            id: String(b.id),
            customerName: b.customerName,
            customerEmail: b.customerEmail,
            customerPhone: b.customerPhone,
            movieTitle: b.movieTitle,
            showtime: `${this.formatDateTime(b.showtimeStart)} (${b.branchName} / ${b.roomName})`,
            seats: b.seats || '-',
            concessions: b.productQuantity
              ? [{ name: 'F&B products', quantity: Number(b.productQuantity), price: 0 }]
              : [],
            amount: Number(b.amount || 0),
            status: this.normalizeStatus(b.reservationStatus),
            paymentMethod: b.paymentMethod || '-',
            bookingDate: this.formatDateTime(b.bookedAt)
          }));
          this.totalElements = res.data.totalElements || 0;
          this.totalPages = res.data.totalPages || 0;
          this.applyFilters();
        } else {
          this.errorMessage = res.message || 'Failed to load bookings.';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Cannot load bookings from admin API.';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredBookings = this.bookings;
  }

  onSearchChange(): void {
    this.page = 0;
    this.loadBookings();
  }

  onStatusChange(status: string): void {
    this.statusFilter = status;
    this.page = 0;
    this.loadBookings();
  }

  openDetails(booking: Booking): void {
    this.selectedBooking = booking;
    this.showDetailModal = true;
  }

  closeModal(): void {
    this.showDetailModal = false;
    this.selectedBooking = null;
  }

  private normalizeStatus(status: string | null | undefined): 'PAID' | 'PENDING' | 'EXPIRED' {
    if (status === 'PAID' || status === 'EXPIRED') {
      return status;
    }
    return 'PENDING';
  }

  private formatDateTime(value: string | null | undefined): string {
    if (!value) {
      return '-';
    }
    return new Date(value).toLocaleString();
  }
}
