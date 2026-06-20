import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminShowtimeService } from '../../services/admin-showtime.service';

@Component({
  standalone: true,
  selector: 'app-admin-cinemas',
  templateUrl: './admin-cinemas.component.html',
  styleUrls: ['./admin-cinemas.component.scss'],
  imports: [CommonModule]
})
export class AdminCinemasComponent implements OnInit {
  branches: any[] = [];
  rooms: any[] = [];
  selectedBranchId: number | null = null;
  selectedRoom: any = null;
  
  isLoadingBranches = false;
  isLoadingRooms = false;
  errorMessage = '';

  rowLetters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'];

  constructor(private showtimeService: AdminShowtimeService) {}

  ngOnInit(): void {
    this.loadBranches();
  }

  loadBranches(): void {
    this.isLoadingBranches = true;
    this.showtimeService.getBranches().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          // Add some details to make the page professional (mock Address, hotline, etc. since DTO is simple)
          this.branches = res.data.map((b: any) => ({
            ...b,
            address: b.province ? `${b.name}, ${b.province.name}` : `${b.name}, Vietnam`,
            hotline: '1900 6017',
            hours: '08:00 - 24:00'
          }));
          if (this.branches.length > 0) {
            this.selectBranch(this.branches[0].id);
          }
        }
        this.isLoadingBranches = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load cinema branches.';
        this.isLoadingBranches = false;
      }
    });
  }

  selectBranch(branchId: number): void {
    this.selectedBranchId = branchId;
    this.selectedRoom = null;
    this.rooms = [];
    this.isLoadingRooms = true;

    this.showtimeService.getRoomsByBranchId(branchId).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.rooms = res.data;
          if (this.rooms.length > 0) {
            this.selectRoom(this.rooms[0]);
          }
        }
        this.isLoadingRooms = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load rooms for the selected branch.';
        this.isLoadingRooms = false;
      }
    });
  }

  selectRoom(room: any): void {
    this.selectedRoom = room;
  }

  getRowsArray(rowCount: number): number[] {
    return Array.from({ length: rowCount }, (_, i) => i);
  }

  getColsArray(colCount: number): number[] {
    return Array.from({ length: colCount }, (_, i) => i + 1);
  }

  getSeatType(rowIndex: number, totalRows: number): 'standard' | 'vip' | 'couple' {
    // Top 3 rows standard, middle VIP, last row couple
    if (rowIndex >= totalRows - 1) {
      return 'couple';
    } else if (rowIndex >= 3 && rowIndex < totalRows - 2) {
      return 'vip';
    }
    return 'standard';
  }
}
