import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import dayjs from 'dayjs';

import { ReservationDetailResponse } from '../../core/models/responses/reservation/reservation-detail-response.model';
import { ReservationService } from '../../core/services/reservation/reservation.service';

@Component({
  standalone: true,
  selector: 'app-my-tickets-page',
  templateUrl: './my-tickets-page.component.html',
  styleUrls: ['./my-tickets-page.component.scss'],
  imports: [CommonModule, RouterModule]
})
export class MyTicketsPageComponent implements OnInit {
  tickets: ReservationDetailResponse[] = [];
  isLoading = true;
  hasError = false;
  readonly dayjs = dayjs;

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.loadTickets();
  }

  loadTickets(): void {
    this.isLoading = true;
    this.hasError = false;
    this.reservationService.getMyReservations().subscribe({
      next: response => {
        this.tickets = (response.data || [])
          .filter(reservation => reservation.status.name === 'PAID')
          .sort((a, b) => dayjs(b.showtime.startTime).valueOf() - dayjs(a.showtime.startTime).valueOf());
        this.isLoading = false;
      },
      error: () => {
        this.hasError = true;
        this.isLoading = false;
      }
    });
  }

  getSeats(seats: { name: string }[]): string {
    return seats?.map(seat => seat.name).join(', ') || 'Đang cập nhật';
  }

  getProducts(ticket: ReservationDetailResponse): string {
    if (!ticket.items?.length) return 'Không có';
    return ticket.items.map(item => `${item.product.name} ×${item.quantity}`).join(', ');
  }

  getTotal(ticket: ReservationDetailResponse): number {
    const productTotal = (ticket.items || []).reduce(
      (total, item) => total + item.product.price * item.quantity,
      0
    );
    return ticket.ticketPrice + productTotal;
  }

  isUpcoming(ticket: ReservationDetailResponse): boolean {
    return dayjs(ticket.showtime.startTime).isAfter(dayjs());
  }
}
