import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { AdminLookupGroup, AdminManagementService } from '../../services/admin-management.service';

interface AdminColumn {
  key: string;
  label: string;
  type?: 'text' | 'date' | 'money' | 'status';
}

@Component({
  standalone: true,
  selector: 'app-admin-resource-list',
  templateUrl: './admin-resource-list.component.html',
  styleUrls: ['./admin-resource-list.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class AdminResourceListComponent implements OnInit, OnDestroy {
  title = '';
  subtitle = '';
  resource = '';
  columns: AdminColumn[] = [];
  rows: any[] = [];
  lookupGroups: AdminLookupGroup[] = [];

  page = 0;
  size = 20;
  totalElements = 0;
  totalPages = 0;
  searchQuery = '';
  statusFilter = '';
  isLoading = false;
  errorMessage = '';
  private routeSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private adminService: AdminManagementService
  ) {}

  ngOnInit(): void {
    this.routeSub = this.route.data.subscribe((data) => {
      this.title = data['title'] || 'Admin List';
      this.subtitle = data['subtitle'] || '';
      this.resource = data['resource'] || '';
      this.columns = data['columns'] || [];
      this.page = 0;
      this.searchQuery = '';
      this.statusFilter = '';
      this.loadData();
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  loadData(): void {
    if (this.resource === 'lookups') {
      this.loadLookups();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getPage<any>(this.resource, {
      page: this.page,
      size: this.size,
      keyword: this.searchQuery,
      status: this.statusFilter || undefined
    }).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.rows = res.data.content || [];
          this.totalElements = res.data.totalElements || 0;
          this.totalPages = res.data.totalPages || 0;
        } else {
          this.errorMessage = res.message || 'Failed to load admin data.';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Cannot load admin data from API.';
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.page = 0;
    this.loadData();
  }

  onPageChange(newPage: number): void {
    if (newPage < 0 || newPage >= this.totalPages) {
      return;
    }
    this.page = newPage;
    this.loadData();
  }

  getCell(row: any, column: AdminColumn): string {
    const value = row[column.key];
    if (value === null || value === undefined || value === '') {
      return '-';
    }
    if (column.type === 'date') {
      return new Date(value).toLocaleString();
    }
    if (column.type === 'money') {
      return `${Number(value).toLocaleString()} d`;
    }
    return String(value);
  }

  statusClass(value: string): string {
    return String(value || '').toLowerCase().replace(/[^a-z0-9]+/g, '-');
  }

  private loadLookups(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.adminService.getLookups().subscribe({
      next: (res) => {
        if (res.success) {
          this.lookupGroups = res.data || [];
          this.totalElements = this.lookupGroups.reduce((sum, group) => sum + group.options.length, 0);
        } else {
          this.errorMessage = res.message || 'Failed to load lookup settings.';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Cannot load lookup settings from API.';
        this.isLoading = false;
      }
    });
  }
}
