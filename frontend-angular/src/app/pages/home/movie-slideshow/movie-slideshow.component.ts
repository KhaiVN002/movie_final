import { ChangeDetectorRef, Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import { MovieService } from "../../../core/services/movie/movie.service";
import { CommonModule } from "@angular/common";
import { SlideShowComponent } from "../../../shared/components/slideshow/slideshow.component";
import { SlideItemComponent } from "../../../shared/components/slideshow/slide-item/slide-item.component";
import { MoviePreviewResponse } from "../../../core/models/responses/movie/movie-preview-response.model";
import { BaseComponent } from "../../../shared/components/base/base.component";
import { LoaderService } from "../../../core/services/ui/loader.service";
import { ShowtimeModalComponent } from "../../../shared/components/showtime-modal/showtime-modal.component";
import { Router, RouterModule } from "@angular/router";

@Component({
    standalone: true,
    selector: "movie-slideshow",
    templateUrl: "./movie-slideshow.component.html",
    styleUrls: [
        "./movie-slideshow.component.scss"
    ],
    imports: [
        CommonModule,
        RouterModule,
        SlideShowComponent,
        SlideItemComponent,
        ShowtimeModalComponent
    ],
    encapsulation: ViewEncapsulation.None
})
class MovieSlideshowComponent implements OnInit {

    movies: MoviePreviewResponse[] = [];

    @ViewChild("showtimeModal") showtimeModal!: ShowtimeModalComponent;

    constructor(
        private movieService: MovieService,
        private cdr: ChangeDetectorRef,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadMovies();
    }

    private loadMovies(): void {
        this.movieService.getTrendingMoviePreviews().subscribe({
            next: (response) => {
                if (response.success === true) {
                    this.movies = response.data ?? [];
                }
                else {
                    console.log(`code: ${response.code}\nmessage: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`error: ${err}`);
            }

        });
    }

    getLinkBackgroundAgeRating(movie: MoviePreviewResponse): string {
        return `/assets/images/ratings/${movie.ageRating.code}.png`;
    }

    openShowtimeModal(movieId: number): void {
        this.showtimeModal.open(movieId);
    }

    navigateToDetail(movieId: number): void {
        this.router.navigate(["/movies/detail", movieId]);
    }

}

export { MovieSlideshowComponent };
