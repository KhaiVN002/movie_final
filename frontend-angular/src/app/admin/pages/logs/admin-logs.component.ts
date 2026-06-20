import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface AuditLog {
  timestamp: string;
  adminEmail: string;
  action: string;
  module: string;
  status: 'SUCCESS' | 'FAILED' | 'WARNING';
  ipAddress: string;
}

@Component({
  standalone: true,
  selector: 'app-admin-logs',
  templateUrl: './admin-logs.component.html',
  styleUrls: ['./admin-logs.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class AdminLogsComponent implements OnInit {
  logs: AuditLog[] = [];
  filteredLogs: AuditLog[] = [];
  
  searchQuery = '';
  statusFilter = 'ALL';
  moduleFilter = 'ALL';

  ngOnInit(): void {
    this.generateLogs();
    this.applyFilters();
  }

  generateLogs(): void {
    this.logs = [
      { timestamp: '2026-06-18 20:41:15', adminEmail: 'test1@gmail.com', action: 'Create Booking TX-98402', module: 'BOOKING', status: 'SUCCESS', ipAddress: '192.168.1.15' },
      { timestamp: '2026-06-18 20:38:02', adminEmail: 'test1@gmail.com', action: 'Update Movie "Spider-Man"', module: 'MOVIE', status: 'SUCCESS', ipAddress: '192.168.1.15' },
      { timestamp: '2026-06-18 20:25:40', adminEmail: 'giangtl@gmail.com', action: 'Update Concession "Combo Solo"', module: 'CONCESSION', status: 'SUCCESS', ipAddress: '192.168.1.20' },
      { timestamp: '2026-06-18 19:40:12', adminEmail: 'test1@gmail.com', action: 'Attempted to delete Movie "The Batman" with active showtimes', module: 'MOVIE', status: 'FAILED', ipAddress: '192.168.1.15' },
      { timestamp: '2026-06-18 18:15:30', adminEmail: 'giangtl@gmail.com', action: 'Create Showtime Room 3 (18:30)', module: 'SHOWTIME', status: 'SUCCESS', ipAddress: '192.168.1.20' },
      { timestamp: '2026-06-18 17:05:44', adminEmail: 'test1@gmail.com', action: 'Login successful', module: 'AUTH', status: 'SUCCESS', ipAddress: '192.168.1.15' },
      { timestamp: '2026-06-18 16:59:10', adminEmail: 'admin@cgv.vn', action: 'Login failed - Invalid Password', module: 'AUTH', status: 'FAILED', ipAddress: '203.113.188.25' },
      { timestamp: '2026-06-18 15:30:00', adminEmail: 'test1@gmail.com', action: 'Update ticket base price to 80,000 đ', module: 'SETTINGS', status: 'WARNING', ipAddress: '192.168.1.15' },
      { timestamp: '2026-06-18 14:10:12', adminEmail: 'giangtl@gmail.com', action: 'Lock user account #4 (lanpham@gmail.com)', module: 'USER', status: 'SUCCESS', ipAddress: '192.168.1.20' }
    ];
  }

  applyFilters(): void {
    this.filteredLogs = this.logs.filter(l => {
      const matchesStatus = this.statusFilter === 'ALL' || l.status === this.statusFilter;
      const matchesModule = this.moduleFilter === 'ALL' || l.module === this.moduleFilter;
      
      const q = this.searchQuery.toLowerCase().trim();
      const matchesQuery = !q || 
        l.adminEmail.toLowerCase().includes(q) || 
        l.action.toLowerCase().includes(q) || 
        l.module.toLowerCase().includes(q) ||
        l.ipAddress.includes(q);

      return matchesStatus && matchesModule && matchesQuery;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  exportToCSV(): void {
    // 1. Prepare CSV Headers
    const headers = ['Timestamp', 'Operator', 'Action', 'Module', 'Status', 'IP Address'];
    
    // 2. Prepare Rows
    const rows = this.filteredLogs.map(l => [
      l.timestamp,
      l.adminEmail,
      `"${l.action.replace(/"/g, '""')}"`, // escape quotes
      l.module,
      l.status,
      l.ipAddress
    ]);

    // 3. Construct CSV Content
    const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n');
    
    // 4. Download file in browser
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `cgv_audit_logs_${new Date().toISOString().slice(0,10)}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
