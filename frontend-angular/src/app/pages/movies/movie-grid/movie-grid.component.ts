import { Component, OnInit } from "@angular/core";
import { MovieListItemResponse } from "../../../core/models/responses/movie/movie-list-item-response.model";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { MovieService } from "../../../core/services/movie/movie.service";
import { PageRequest } from "../../../core/models/requests/page-request.model";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import dayjs from "dayjs";
import { finalize } from "rxjs";

@Component({
    standalone: true,
    selector: 'movie-grid',
    templateUrl: './movie-grid.component.html',
    styleUrls: [
        './movie-grid.component.scss'
    ],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ]
})
export class MovieGridComponent implements OnInit {
    movieStatus: 'all' | 'now_showing' | 'coming_soon' = 'all';

    movies: MovieListItemResponse[] = []
    searchQuery: string = '';
    
    dayjs = dayjs;

    pageable: PageRequest = {
        page: 0,
        size: 15
    }

    isLastPage: boolean = false;
    isLoading: boolean = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private movieService: MovieService
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            const status = params['status'];
            this.movieStatus = ['now_showing', 'coming_soon'].includes(status)
                ? status
                : 'all';
            this.resetPaging();
            this.loadMovies();
        })
    }

    changeMovieStatus(status: 'all' | 'now_showing' | 'coming_soon'): void {
        if (this.movieStatus === status) return;

        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                status: status === 'all' ? null : status
            },
            queryParamsHandling: 'merge'
        });
    }

    resetPaging(): void {
        this.pageable.page = 0;
        this.movies = [];
        this.isLastPage = false;
    }

    get filteredMovies(): MovieListItemResponse[] {
        const query = this.normalizeSearchText(this.searchQuery);
        if (!query) return this.movies;

        return this.movies.filter(movie => {
            const title = this.normalizeSearchText(movie.title);
            const categories = this.normalizeSearchText(
                movie.categories?.map(category => category.name).join(' ') || ''
            );

            return title.includes(query) || categories.includes(query);
        });
    }

    clearSearch(): void {
        this.searchQuery = '';
    }

    private normalizeSearchText(value: string): string {
        return value
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .replace(/đ/g, 'd')
            .replace(/Đ/g, 'D')
            .toLowerCase()
            .trim();
    }

loadMovies(): void {
    if (this.isLoading || this.isLastPage) {
        return;
    }
    this.isLoading = true;

    let observable;

    if (this.movieStatus === 'all') {
        observable = this.movieService.getMovieListItems(this.pageable);
    } 
    else if (this.movieStatus === 'now_showing') {
        observable = this.movieService.getNowShowingListItems(this.pageable);
    } 
    else {
        observable = this.movieService.getUpcomingListItems(this.pageable);
    }

    observable.pipe(
        finalize(() => {
            this.isLoading = false;
        })
    ).subscribe({
        next: (response) => {
            if (response.success && response.data) {
                let page = response.data;
                this.movies.push(...page.content);
                this.isLastPage = page.last;
                this.pageable.page += 1;
            } else {
                console.log(`error: ${response.message}`);
            }
        },
        error: (err) => {
            console.error('error:', err);
            this.isLastPage = true;
        }
    });
}

    getCategoriesString(movie: MovieListItemResponse): string {
        return movie.categories?.map(cat => cat.name).join(', ') || 'Đang cập nhật';
    }

}
