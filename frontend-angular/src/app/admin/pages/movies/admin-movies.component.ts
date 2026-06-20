import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminMovieService } from '../../services/admin-movie.service';
import { resolveImageUrl } from '../../../core/utils/image.utils';

@Component({
  standalone: true,
  selector: 'app-admin-movies',
  templateUrl: './admin-movies.component.html',
  styleUrls: ['./admin-movies.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AdminMoviesComponent implements OnInit {
  movies: any[] = [];
  page = 0;
  size = 10;
  totalElements = 0;
  totalPages = 0;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  showForm = false;
  isEditing = false;
  selectedMovieId: number | null = null;
  originalCategoryIds: number[] = [];
  movieForm!: FormGroup;

  selectedPosterFile: File | undefined;
  posterPreviewUrl: string | null = null;
  backdropItems: Array<{ file?: File, url: string }> = [];

  movieFilterType: 'ALL' | 'BOOKED' | 'UNBOOKED' = 'ALL';
  searchQuery = '';

  readonly languages = [
    { id: 1, name: 'Tiếng Anh' },
    { id: 2, name: 'Tiếng Hindi' },
    { id: 3, name: 'Tiếng Tây Ban Nha' },
    { id: 5, name: 'Tiếng Đức' },
    { id: 6, name: 'Tiếng Hàn' },
    { id: 7, name: 'Tiếng Pháp' },
    { id: 10, name: 'Tiếng Nhật' },
    { id: 17, name: 'Tiếng Trung' },
    { id: 27, name: 'Tiếng Thái' }
  ];

  readonly ageRatings = [
    { id: 4, code: 'P', description: 'Mọi lứa tuổi' },
    { id: 2, code: 'K', description: 'Dưới 13 tuổi cần người bảo hộ' },
    { id: 3, code: 'T13', description: 'Từ đủ 13 tuổi trở lên' },
    { id: 5, code: 'T16', description: 'Từ đủ 16 tuổi trở lên' },
    { id: 1, code: 'T18', description: 'Từ đủ 18 tuổi trở lên' }
  ];

  readonly categories = [
    { id: 1, name: 'Phim Gia Đình' },
    { id: 2, name: 'Phim Hài' },
    { id: 3, name: 'Phim Khoa Học Viễn Tưởng' },
    { id: 4, name: 'Phim Phiêu Lưu' },
    { id: 5, name: 'Phim Giả Tượng' },
    { id: 6, name: 'Phim Hành Động' },
    { id: 7, name: 'Phim Chính Kịch' },
    { id: 8, name: 'Phim Hình Sự' },
    { id: 9, name: 'Phim Gây Cấn' },
    { id: 10, name: 'Phim Kinh Dị' },
    { id: 11, name: 'Phim Bí Ẩn' },
    { id: 12, name: 'Phim Chiến Tranh' },
    { id: 13, name: 'Phim Hoạt Hình' },
    { id: 14, name: 'Phim Lãng Mạn' },
    { id: 15, name: 'Phim Tài Liệu' },
    { id: 16, name: 'Phim Miền Tây' },
    { id: 17, name: 'Chương Trình Truyền Hình' },
    { id: 18, name: 'Phim Lịch Sử' },
    { id: 19, name: 'Phim Nhạc' }
  ];

  private readonly defaultDuration = 90;

  constructor(
    private fb: FormBuilder,
    private movieService: AdminMovieService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadMovies();
  }

  get filteredMoviesList(): any[] {
    if (this.movieFilterType === 'BOOKED') {
      return this.movies.filter(m => m.hasBookings);
    }
    if (this.movieFilterType === 'UNBOOKED') {
      return this.movies.filter(m => !m.hasBookings);
    }
    return this.movies;
  }

  initForm(): void {
    this.movieForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2)]],
      duration: [null, [Validators.min(1)]],
      originalLanguageId: [null],
      ageRatingId: [null],
      releasedDate: [''],
      trailerURL: [''],
      tagline: [''],
      overview: [''],
      categoryIds: [[]]
    });
  }

  loadMovies(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.movieService.getMovies(this.page, this.size, this.searchQuery).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.movies = (res.data.content || []).map((m: any) => ({
            ...m,
            posterURL: resolveImageUrl(m.posterURL),
            backdropURL: resolveImageUrl(m.backdropURL),
            hasBookings: m.hasBookings === true || Number(m.bookingCount || 0) > 0,
            ageRating: m.ageRating?.code ? m.ageRating : { code: m.ageRating || 'P' },
            originalLanguage: m.originalLanguage?.name ? m.originalLanguage : { name: m.originalLanguage || 'Unknown' }
          }));
          this.totalElements = res.data.totalElements || 0;
          this.totalPages = res.data.totalPages || 0;
        } else {
          this.errorMessage = res.message || 'Lỗi tải danh sách phim';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Đã xảy ra lỗi khi tải danh sách phim từ hệ thống.';
        this.isLoading = false;
      }
    });
  }

  onPageChange(newPage: number): void {
    if (newPage >= 0 && newPage < this.totalPages) {
      this.page = newPage;
      this.loadMovies();
    }
  }

  onSearchChange(): void {
    this.page = 0;
    this.loadMovies();
  }

  openAddMovie(): void {
    this.isEditing = false;
    this.selectedMovieId = null;
    this.originalCategoryIds = [];
    this.clearSelectedFiles();
    this.movieForm.reset({
      title: '',
      duration: null,
      originalLanguageId: null,
      ageRatingId: null,
      releasedDate: '',
      trailerURL: '',
      tagline: '',
      overview: '',
      categoryIds: []
    });
    this.showForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  openEditMovie(movie: any): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.movieService.getMovieDetail(movie.id).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          const detail = res.data;
          this.isEditing = true;
          this.selectedMovieId = detail.id;
          this.selectedPosterFile = undefined;
          this.posterPreviewUrl = detail.posterURL ? resolveImageUrl(detail.posterURL) : null;
          
          this.backdropItems = [];
          if (detail.backdropURL) {
            this.backdropItems.push({ url: resolveImageUrl(detail.backdropURL) });
          }
          if (detail.images?.content) {
            detail.images.content.forEach((img: any) => {
              const url = resolveImageUrl(img.imageURL);
              if (url !== resolveImageUrl(detail.backdropURL)) {
                this.backdropItems.push({ url });
              }
            });
          }

          const categoryIds = detail.categories ? detail.categories.map((c: any) => c.id) : [];
          this.originalCategoryIds = [...categoryIds];

          let releasedDate = detail.releasedDate;
          if (Array.isArray(releasedDate)) {
            const y = releasedDate[0];
            const m = String(releasedDate[1]).padStart(2, '0');
            const d = String(releasedDate[2]).padStart(2, '0');
            releasedDate = `${y}-${m}-${d}`;
          }

          this.movieForm.patchValue({
            title: detail.title,
            duration: detail.duration,
            originalLanguageId: detail.originalLanguage ? detail.originalLanguage.id : null,
            ageRatingId: detail.ageRating ? detail.ageRating.id : null,
            releasedDate,
            trailerURL: detail.trailerURL || '',
            tagline: detail.tagline || '',
            overview: detail.overview || '',
            categoryIds
          });

          this.showForm = true;
        } else {
          this.errorMessage = res.message || 'Lỗi tải chi tiết thông tin phim';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Không thể kết nối máy chủ để tải thông tin phim.';
        this.isLoading = false;
      }
    });
  }

  closeForm(): void {
    this.showForm = false;
  }

  onPosterSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.selectedPosterFile = file;
      this.previewFile(file, (url) => this.posterPreviewUrl = url);
    }
  }

  onBackdropSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    if (files && files.length > 0) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        this.previewFile(file, (url) => {
          this.backdropItems.push({ file, url });
        });
      }
    }
  }

  clearPosterPreview(): void {
    this.selectedPosterFile = undefined;
    this.posterPreviewUrl = null;
  }

  clearBackdropPreview(index: number): void {
    this.backdropItems.splice(index, 1);
  }

  toggleCategory(categoryId: number): void {
    const current = this.normalizeNumberList(this.movieForm.get('categoryIds')?.value);
    if (current.includes(categoryId)) {
      this.movieForm.patchValue({ categoryIds: current.filter((id: number) => id !== categoryId) });
    } else {
      this.movieForm.patchValue({ categoryIds: Array.from(new Set([...current, categoryId])) });
    }
  }

  isCategoryChecked(categoryId: number): boolean {
    return this.normalizeNumberList(this.movieForm.get('categoryIds')?.value).includes(categoryId);
  }

  canSaveMovie(): boolean {
    if (this.isLoading) {
      return false;
    }
    return this.movieForm.valid;
  }

  saveMovie(): void {
    if (this.isEditing) {
      this.updateMovie();
      return;
    }
    this.createMovie();
  }

  deleteMovie(id: number): void {
    if (!confirm('Bạn có chắc chắn muốn xóa phim này không? Dữ liệu sẽ bị xóa vĩnh viễn.')) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.movieService.deleteMovie(id).subscribe({
      next: () => {
        this.successMessage = 'Xóa phim thành công!';
        this.loadMovies();
      },
      error: () => {
        this.errorMessage = 'Không thể xóa phim này do đã có lịch chiếu hoặc vé được đặt.';
        this.isLoading = false;
      }
    });
  }

  private createMovie(): void {
    const payload = this.buildCreatePayload();
    if (!payload) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const backdropFiles = this.backdropItems
      .map(item => item.file)
      .filter((file): file is File => file !== undefined);

    this.movieService.createMovie(payload, this.selectedPosterFile, backdropFiles).subscribe({
      next: () => {
        this.successMessage = 'Tạo phim mới thành công!';
        this.loadMovies();
        this.showForm = false;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Thêm phim mới thất bại. Vui lòng kiểm tra lại thông tin.';
        this.isLoading = false;
      }
    });
  }

  private updateMovie(): void {
    if (this.movieForm.invalid || this.selectedMovieId === null) {
      this.movieForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formVal = this.movieForm.value;
    const currentCategoryIds = this.normalizeNumberList(formVal.categoryIds);
    const addCategoryIds = currentCategoryIds.filter((id: number) => !this.originalCategoryIds.includes(id));
    const deleteCategoryIds = this.originalCategoryIds.filter((id: number) => !currentCategoryIds.includes(id));

    const backdropFiles = this.backdropItems
      .map(item => item.file)
      .filter((file): file is File => file !== undefined);

    const existingBackdropUrls = this.backdropItems
      .filter(item => !item.file && item.url && !item.url.startsWith('data:'))
      .map(item => this.cleanUrl(item.url));

    const firstBackdropUrl = this.backdropItems[0] && !this.backdropItems[0].file && this.backdropItems[0].url && !this.backdropItems[0].url.startsWith('data:')
      ? this.cleanUrl(this.backdropItems[0].url)
      : '';

    const payload: any = {
      title: this.firstText(formVal.title),
      posterURL: this.posterPreviewUrl && !this.posterPreviewUrl.startsWith('data:') ? this.cleanUrl(this.posterPreviewUrl) : '',
      backdropURL: firstBackdropUrl,
      backdropURLs: existingBackdropUrls,
      trailerURL: this.firstText(formVal.trailerURL) || '',
      tagline: this.firstText(formVal.tagline) || '',
      overview: this.firstText(formVal.overview) || '',
      releasedDate: this.normalizeDate(formVal.releasedDate),
      duration: this.toPositiveNumber(formVal.duration) || this.defaultDuration,
      originalLanguageId: this.toPositiveNumber(formVal.originalLanguageId),
      ageRatingId: this.toPositiveNumber(formVal.ageRatingId),
      addCategoryIds,
      deleteCategoryIds,
      createCast: [],
      updateCast: [],
      deleteCast: [],
      createCrew: [],
      updateCrew: [],
      deleteCrew: []
    };

    this.movieService.updateMovie(this.selectedMovieId, payload, this.selectedPosterFile, backdropFiles).subscribe({
      next: () => {
        this.successMessage = 'Cập nhật phim thành công!';
        this.loadMovies();
        this.showForm = false;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Cập nhật phim thất bại. Vui lòng thử lại.';
        this.isLoading = false;
      }
    });
  }

  private buildCreatePayload(): any | null {
    if (this.movieForm.invalid) {
      this.movieForm.markAllAsTouched();
      return null;
    }

    const formVal = this.movieForm.value;
    const title = this.firstText(formVal.title);

    if (!title || title.length < 2) {
      this.errorMessage = 'Tên phim phải có ít nhất 2 ký tự.';
      return null;
    }

    const firstBackdropUrl = this.backdropItems[0] && !this.backdropItems[0].file && this.backdropItems[0].url && !this.backdropItems[0].url.startsWith('data:')
      ? this.cleanUrl(this.backdropItems[0].url)
      : '';

    const payload: any = {
      title,
      duration: this.toPositiveNumber(formVal.duration) || this.defaultDuration,
      categoryIds: this.normalizeNumberList(formVal.categoryIds),
      cast: [],
      crew: []
    };

    if (this.posterPreviewUrl && !this.posterPreviewUrl.startsWith('data:')) {
      payload.posterURL = this.cleanUrl(this.posterPreviewUrl);
    }
    if (firstBackdropUrl) {
      payload.backdropURL = firstBackdropUrl;
    }
    this.setOptionalText(payload, 'trailerURL', this.firstText(formVal.trailerURL));
    this.setOptionalText(payload, 'tagline', this.firstText(formVal.tagline));
    this.setOptionalText(payload, 'overview', this.firstText(formVal.overview));

    const releasedDate = this.normalizeDate(formVal.releasedDate);
    if (releasedDate) {
      payload.releasedDate = releasedDate;
    }

    const originalLanguageId = this.toPositiveNumber(formVal.originalLanguageId);
    if (originalLanguageId) {
      payload.originalLanguageId = originalLanguageId;
    }

    const ageRatingId = this.toPositiveNumber(formVal.ageRatingId);
    if (ageRatingId) {
      payload.ageRatingId = ageRatingId;
    }

    return payload;
  }

  private normalizeNumberList(value: any): number[] {
    if (!value) {
      return [];
    }
    if (Array.isArray(value)) {
      return Array.from(new Set(value
        .map(item => this.toPositiveNumber(item))
        .filter((item): item is number => typeof item === 'number')));
    }
    return [];
  }

  private cleanUrl(url: string | null | undefined): string {
    if (!url) return '';
    if (url.includes('/uploads/')) {
      return '/uploads/' + url.substring(url.indexOf('/uploads/') + '/uploads/'.length);
    }
    return url;
  }

  private normalizeDate(value: any): string | undefined {
    const raw = this.firstText(value);
    if (!raw) {
      return undefined;
    }

    const normalized = this.normalizeKey(raw);
    if (['today', 'now', 'homnay'].includes(normalized)) {
      return this.toIsoDate(new Date());
    }

    const ymd = raw.match(/^(\d{4})[-/](\d{1,2})[-/](\d{1,2})$/);
    if (ymd) {
      return `${ymd[1]}-${ymd[2].padStart(2, '0')}-${ymd[3].padStart(2, '0')}`;
    }

    const dmy = raw.match(/^(\d{1,2})[-/](\d{1,2})[-/](\d{4})$/);
    if (dmy) {
      return `${dmy[3]}-${dmy[2].padStart(2, '0')}-${dmy[1].padStart(2, '0')}`;
    }

    const parsed = new Date(raw);
    return Number.isNaN(parsed.getTime()) ? undefined : this.toIsoDate(parsed);
  }

  private toPositiveNumber(value: any): number | undefined {
    if (value === null || value === undefined || value === '') {
      return undefined;
    }
    const numeric = Number(value);
    return Number.isFinite(numeric) && numeric > 0 ? numeric : undefined;
  }

  private firstText(...values: any[]): string | undefined {
    for (const value of values) {
      if (typeof value === 'string' && value.trim()) {
        return value.trim();
      }
      if (typeof value === 'number' && Number.isFinite(value)) {
        return String(value);
      }
    }
    return undefined;
  }

  private setOptionalText(payload: any, key: string, value: any): void {
    const text = this.firstText(value);
    if (text) {
      payload[key] = text;
    }
  }

  private normalizeKey(value: string): string {
    return value
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/[^a-z0-9]/g, '');
  }

  private toIsoDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private previewFile(file: File, callback: (url: string) => void): void {
    const reader = new FileReader();
    reader.onload = () => callback(reader.result as string);
    reader.readAsDataURL(file);
  }

  private clearSelectedFiles(): void {
    this.selectedPosterFile = undefined;
    this.posterPreviewUrl = null;
    this.backdropItems = [];
  }
}
