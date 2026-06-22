import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  SupportRequestResponse,
  SupportRequestService
} from '../../../core/services/support/support-request.service';

@Component({
  standalone: true,
  selector: 'app-admin-support',
  templateUrl: './admin-support.component.html',
  styleUrls: ['./admin-support.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class AdminSupportComponent implements OnInit {
  requests: SupportRequestResponse[] = [];
  searchQuery = '';
  unreadOnly = false;
  isLoading = false;

  constructor(private supportRequestService: SupportRequestService) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  get filteredRequests(): SupportRequestResponse[] {
    const query = this.searchQuery.trim().toLowerCase();
    if (!query) return this.requests;
    return this.requests.filter(item =>
      item.message.toLowerCase().includes(query)
      || item.userName.toLowerCase().includes(query)
      || (item.userEmail || '').toLowerCase().includes(query)
    );
  }

  loadRequests(): void {
    this.isLoading = true;
    this.supportRequestService.getAdminRequests(0, 100, this.unreadOnly).subscribe({
      next: response => {
        this.requests = response.data?.content || [];
        this.isLoading = false;
      },
      error: () => {
        this.requests = [];
        this.isLoading = false;
      }
    });
  }

  markAsRead(item: SupportRequestResponse): void {
    if (item.read) return;
    this.supportRequestService.markAsRead(item.id).subscribe({
      next: () => {
        item.read = true;
        if (this.unreadOnly) {
          this.requests = this.requests.filter(request => request.id !== item.id);
        }
      }
    });
  }

  markAllAsRead(): void {
    this.supportRequestService.markAllAsRead().subscribe({
      next: () => {
        if (this.unreadOnly) {
          this.requests = [];
        } else {
          this.requests.forEach(item => item.read = true);
        }
      }
    });
  }
}
