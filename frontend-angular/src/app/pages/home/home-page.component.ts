import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterModule, Router } from "@angular/router";
import { HomeBannerComponent } from "./banner/banner.component";
import { SearchBar } from "../../shared/components/search-bar/search-bar.component";
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
        SearchBar,
        MovieSlideshowComponent,
        PromoSliderComponent
    ],
})
export class HomePageComponent implements OnInit {
    allMovies: MovieListItemResponse[] = [];
    filteredMovies: MovieListItemResponse[] = [];
    searchQuery: string = '';
    selectedCategoryId: number | null = null;
    isLoading: boolean = false;

    categories = [
        { id: 1, name: 'Hành động' },
        { id: 2, name: 'Hài hước' },
        { id: 3, name: 'Tâm lý' },
        { id: 4, name: 'Kinh dị' },
        { id: 5, name: 'Tình cảm' },
        { id: 6, name: 'Viễn tưởng' },
        { id: 7, name: 'Giật gân' }
    ];

    constructor(
        private movieService: MovieService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.loadMovies();
    }

    loadMovies(): void {
        this.isLoading = true;
        this.movieService.getMovieListItems({ page: 0, size: 100 }).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.allMovies = res.data.content || [];
                    this.filterMovies();
                }
                this.isLoading = false;
            },
            error: (err) => {
                console.error("Lỗi khi tải danh sách phim ở trang chủ:", err);
                this.isLoading = false;
            }
        });
    }

    onSearchChange(): void {
        this.filterMovies();
    }

    selectCategory(categoryId: number | null): void {
        this.selectedCategoryId = categoryId;
        this.filterMovies();
    }

    filterMovies(): void {
        const query = this.searchQuery.trim().toLowerCase();
        this.filteredMovies = this.allMovies.filter(movie => {
            const matchTitle = !query || movie.title.toLowerCase().includes(query);
            const matchCategory = !this.selectedCategoryId || 
                (movie.categories && movie.categories.some(c => c.id === this.selectedCategoryId));
            return matchTitle && matchCategory;
        });
    }

    getCategoriesString(movie: MovieListItemResponse): string {
        if (!movie.categories || movie.categories.length === 0) return 'Khác';
        return movie.categories.map(cat => cat.name).join(', ');
    }

    navigateToDetail(movieId: number): void {
        this.router.navigate(["/movies/detail", movieId]);
    }
}
