import { Component, OnDestroy, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterModule, Router } from "@angular/router";
import { Subject, catchError, debounceTime, finalize, of, startWith, switchMap, takeUntil } from "rxjs";
import { HomeBannerComponent } from "./banner/banner.component";
import { MovieSlideshowComponent } from "./movie-slideshow/movie-slideshow.component";
import { PromoSliderComponent } from "./promo-slider/promo-slider.component";
import { MovieService } from "../../core/services/movie/movie.service";
import { MovieListItemResponse } from "../../core/models/responses/movie/movie-list-item-response.model";

@Component({
    standalone: true,
    selector: "app-home-page",
    templateUrl: "./home-page.component.html",
    styleUrls: ["./home-page.component.scss"],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        HomeBannerComponent,
        MovieSlideshowComponent,
        PromoSliderComponent
    ],
})
export class HomePageComponent implements OnInit, OnDestroy {
    filteredMovies: MovieListItemResponse[] = [];
    searchQuery = '';
    selectedCategoryId: number | null = null;
    isLoading = false;

    private readonly searchChanges = new Subject<void>();
    private readonly destroy$ = new Subject<void>();

    categories = [
        { id: 6, name: 'Hành động' },
        { id: 2, name: 'Hài hước' },
        { id: 7, name: 'Chính kịch' },
        { id: 10, name: 'Kinh dị' },
        { id: 14, name: 'Lãng mạn' },
        { id: 3, name: 'Viễn tưởng' },
        { id: 9, name: 'Gây cấn' }
    ];

    constructor(
        private movieService: MovieService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.searchChanges.pipe(
            startWith(undefined),
            debounceTime(250),
            switchMap(() => {
                this.isLoading = true;
                return this.movieService.searchMovieCatalog(
                    { page: 0, size: 24 },
                    this.searchQuery,
                    this.selectedCategoryId
                ).pipe(
                    catchError(error => {
                        console.error('Lỗi khi tìm kiếm phim:', error);
                        return of(null);
                    }),
                    finalize(() => this.isLoading = false)
                );
            }),
            takeUntil(this.destroy$)
        ).subscribe(response => {
            this.filteredMovies = response?.success && response.data
                ? response.data.content
                : [];
        });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    onSearchChange(): void {
        this.searchChanges.next();
    }

    selectCategory(categoryId: number | null): void {
        this.selectedCategoryId = categoryId;
        this.searchChanges.next();
    }

    getCategoriesString(movie: MovieListItemResponse): string {
        return movie.categories?.length
            ? movie.categories.map(category => category.name).join(', ')
            : 'Khác';
    }

    navigateToDetail(movieId: number): void {
        this.router.navigate(["/movies/detail", movieId]);
    }
}
