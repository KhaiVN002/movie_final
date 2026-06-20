import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminShowtimeService } from '../../services/admin-showtime.service';
import { BranchOptionResponse } from '../../../core/models/responses/theater/branch-option-response.model';

@Component({
  standalone: true,
  selector: 'app-admin-showtimes',
  templateUrl: './admin-showtimes.component.html',
  styleUrls: ['./admin-showtimes.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AdminShowtimesComponent implements OnInit {
  showtimes: any[] = [];
  movies: any[] = [];
  branches: any[] = [];
  rooms: any[] = [];

  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Pagination states
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  searchQuery = '';
  selectedShowtimeIds = new Set<number>();
  isAllMatchingSelected = false;

  // Movie search in modal
  movieSearchQuery = '';
  filteredSearchMovies: any[] = [];

  // Form states
  showForm = false;
  showtimeForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private showtimeService: AdminShowtimeService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadData();
  }

  initForm(): void {
    this.showtimeForm = this.fb.group({
      movieId: ['', [Validators.required]],
      branchId: ['', [Validators.required]],
      roomId: ['', [Validators.required]],
      startTime: ['', [Validators.required]]
    });

    // Listen to branch selection changes to load branch-specific rooms
    this.showtimeForm.get('branchId')?.valueChanges.subscribe((branchId) => {
      if (branchId) {
        this.loadRooms(branchId);
      } else {
        this.rooms = [];
        this.showtimeForm.get('roomId')?.setValue('');
      }
    });
  }

  loadData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.selectedShowtimeIds.clear();
    this.isAllMatchingSelected = false;

    this.showtimeService.getShowtimes(this.page, this.size, this.searchQuery).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.showtimes = (res.data.content || []).map((s: any) => ({
            ...s,
            movie: {
              id: s.movieId,
              title: s.movieTitle,
              duration: this.getDurationMinutes(s.startTime, s.endTime)
            },
            room: {
              id: s.roomId,
              name: s.roomName,
              branch: {
                id: s.branchId,
                name: s.branchName
              },
              type: {
                name: 'Standard'
              }
            },
            status: {
              name: s.status
            }
          }));
          this.totalElements = res.data.totalElements || 0;
          this.totalPages = res.data.totalPages || 0;
        } else {
          this.errorMessage = res.message || 'Failed to load showtimes';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'An error occurred while loading showtimes.';
        this.isLoading = false;
      }
    });

    this.showtimeService.getBranches().subscribe({
      next: (res) => {
        if (res.success) {
          this.branches = res.data || [];
        }
      }
    });

    this.showtimeService.getMoviesList('').subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.movies = res.data.content || [];
          this.filteredSearchMovies = [...this.movies];
        }
      }
    });
  }

  onSearch(): void {
    this.page = 0;
    this.isAllMatchingSelected = false;
    this.loadData();
  }

  private getDurationMinutes(startTime: string, endTime: string): number {
    if (!startTime || !endTime) {
      return 0;
    }
    const start = new Date(startTime).getTime();
    const end = new Date(endTime).getTime();
    if (Number.isNaN(start) || Number.isNaN(end) || end <= start) {
      return 0;
    }
    return Math.round((end - start) / 60000);
  }

  onPageChange(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadData();
    }
  }

  loadRooms(branchId: number): void {
    this.showtimeForm.get('roomId')?.setValue('');
    this.showtimeService.getRoomsByBranchId(branchId).subscribe({
      next: (res) => {
        if (res.success) {
          this.rooms = res.data || [];
        }
      }
    });
  }

  openAddShowtime(): void {
    this.movieSearchQuery = '';
    this.filteredSearchMovies = [...this.movies];
    this.showtimeForm.reset({
      movieId: '',
      branchId: '',
      roomId: '',
      startTime: ''
    });
    this.rooms = [];
    this.showForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onMovieSearchChange(): void {
    this.showtimeService.getMoviesList(this.movieSearchQuery).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.filteredSearchMovies = res.data.content || [];
        }
      }
    });
  }

  closeForm(): void {
    this.showForm = false;
  }

  saveShowtime(): void {
    if (this.showtimeForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const val = this.showtimeForm.value;

    // Convert date string from input to ISO-8601 (LocalDateTime format)
    // datetime-local returns "YYYY-MM-DDTHH:MM", we append ":00" to match LocalDateTime format on Spring Boot
    let formattedTime = val.startTime;
    if (formattedTime && formattedTime.length === 16) {
      formattedTime = `${formattedTime}:00`;
    }

    const payload = {
      movieId: Number(val.movieId),
      roomId: Number(val.roomId),
      startTime: formattedTime
    };

    this.showtimeService.createShowtime(payload).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = 'Showtime scheduled successfully!';
          this.loadData();
          this.showForm = false;
        } else {
          this.errorMessage = res.message || 'Failed to schedule showtime';
        }
        this.isLoading = false;
      },
      error: (err) => {
        // Backend handles overlaps and throws a 400 Bad Request with a message
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Conflict detected: this room is already scheduled for another movie at this time.';
        }
        this.isLoading = false;
      }
    });
  }

  toggleShowtimeStatus(showtime: any): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const newStatusId = showtime.status.name === 'AVAILABLE' ? 2 : 1; // 2: UNAVAILABLE, 1: AVAILABLE
    this.showtimeService.updateShowtime(showtime.id, newStatusId).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = `Showtime status updated successfully!`;
          this.loadData();
        } else {
          this.errorMessage = res.message || 'Failed to update showtime status';
          this.isLoading = false;
        }
      },
      error: () => {
        this.errorMessage = 'An error occurred during status update.';
        this.isLoading = false;
      }
    });
  }

  deleteShowtime(showtimeId: number): void {
    if (!confirm('Bạn có chắc chắn muốn xóa TOÀN BỘ lịch chiếu của bộ phim này không?')) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.showtimeService.deleteShowtime(showtimeId).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = 'Xóa lịch chiếu thành công!';
          this.loadData();
        } else {
          this.errorMessage = res.message || 'Xóa lịch chiếu thất bại.';
          this.isLoading = false;
        }
      },
      error: (err) => {
        if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Xóa lịch chiếu thất bại. Lịch chiếu này có thể đã có khách mua vé.';
        }
        this.isLoading = false;
      }
    });
  }

  // Selection Helpers
  isSelected(id: number): boolean {
    return this.selectedShowtimeIds.has(id);
  }

  toggleSelection(id: number): void {
    this.isAllMatchingSelected = false;
    if (this.selectedShowtimeIds.has(id)) {
      this.selectedShowtimeIds.delete(id);
    } else {
      this.selectedShowtimeIds.add(id);
    }
  }

  isAllSelected(): boolean {
    if (!this.showtimes.length) return false;
    return this.showtimes.every(s => this.selectedShowtimeIds.has(s.id));
  }

  toggleAllSelection(event: any): void {
    this.isAllMatchingSelected = false;
    if (event.target.checked) {
      this.showtimes.forEach(s => this.selectedShowtimeIds.add(s.id));
    } else {
      this.showtimes.forEach(s => this.selectedShowtimeIds.delete(s.id));
    }
  }

  selectAllMatching(): void {
    this.isAllMatchingSelected = true;
    this.showtimes.forEach(s => this.selectedShowtimeIds.add(s.id));
  }

  deleteSelectedShowtimes(): void {
    if (this.isAllMatchingSelected) {
      const count = this.totalElements;
      if (!confirm(`Bạn có chắc chắn muốn xóa TOÀN BỘ ${count} lịch chiếu khớp với tìm kiếm "${this.searchQuery}" không?`)) {
        return;
      }

      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.showtimeService.deleteShowtimesByMovieTitle(this.searchQuery).subscribe({
        next: (res) => {
          if (res.success) {
            this.successMessage = `Đã xóa thành công toàn bộ ${count} lịch chiếu!`;
            this.selectedShowtimeIds.clear();
            this.isAllMatchingSelected = false;
            this.loadData();
          } else {
            this.errorMessage = res.message || 'Xóa lịch chiếu thất bại.';
            this.isLoading = false;
          }
        },
        error: (err) => {
          if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
          } else {
            this.errorMessage = 'Xóa thất bại. Một số lịch chiếu có thể đã có khách mua vé.';
          }
          this.isLoading = false;
        }
      });
    } else {
      const count = this.selectedShowtimeIds.size;
      if (count === 0) return;
      if (!confirm(`Bạn có chắc chắn muốn xóa ${count} lịch chiếu đã chọn không?`)) {
        return;
      }

      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.showtimeService.deleteShowtimesBulk(Array.from(this.selectedShowtimeIds)).subscribe({
        next: (res) => {
          if (res.success) {
            this.successMessage = `Đã xóa thành công ${count} lịch chiếu!`;
            this.selectedShowtimeIds.clear();
            this.loadData();
          } else {
            this.errorMessage = res.message || 'Xóa lịch chiếu thất bại.';
            this.isLoading = false;
          }
        },
        error: (err) => {
          if (err.error && err.error.message) {
            this.errorMessage = err.error.message;
          } else {
            this.errorMessage = 'Xóa hàng loạt thất bại. Một số lịch chiếu có thể đã có khách mua vé.';
          }
          this.isLoading = false;
        }
      });
    }
  }
}
