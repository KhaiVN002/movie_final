import { Component, OnInit } from "@angular/core";
import { MovieListItemResponse } from "../../../core/models/responses/movie/movie-list-item-response.model";
import { ActivatedRoute, RouterModule } from "@angular/router";
import { MovieService } from "../../../core/services/movie/movie.service";
import { PageRequest } from "../../../core/models/requests/page-request.model";
import { CommonModule } from "@angular/common";
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
        RouterModule
    ]
})
export class MovieGridComponent implements OnInit {
    movieStatus: 'all' | 'now_showing' | 'coming_soon' = 'all';

    movies: MovieListItemResponse[] = []
    
    dayjs = dayjs;

    pageable: PageRequest = {
        page: 0,
        size: 15
    }

    isLastPage: boolean = false;
    isLoading: boolean = false;

    constructor(
        private route: ActivatedRoute,
        private movieService: MovieService
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            const status = params['status'];
            if (status) {
                this.movieStatus = status;
            }
            this.resetPaging();
            this.loadMovies();
        })
    }

    changeMovieStatus(status: 'all' | 'now_showing' | 'coming_soon'): void {
        if (this.movieStatus !== status) {
            this.movieStatus = status;
            this.resetPaging();
            this.loadMovies();
        }
    }

    resetPaging(): void {
        this.pageable.page = 0;
        this.movies = [];
        this.isLastPage = false;
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
        }
    });
}

    getCategoriesString(movie: MovieListItemResponse): string {
        return movie.categories.map(cat => cat.name).join(', ');
    }

}
