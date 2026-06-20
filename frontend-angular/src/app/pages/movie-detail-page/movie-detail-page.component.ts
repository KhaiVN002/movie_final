import { Component, OnInit, ViewChild } from "@angular/core";
import { MovieDetailResponse } from "../../core/models/responses/movie/movie-detail-response.model";
import { ActivatedRoute } from "@angular/router";
import { MovieService } from "../../core/services/movie/movie.service";
import { MetadataComponent } from "./metadata/metadata.component";
import { CommonModule } from "@angular/common";
import { BookingComponent } from "./booking/booking.component";
import { MoviePhotosComponent } from "./photos/movie-photos.component";
import { MovieDetailInfoComponent } from "./detail/detail-info.component";
import { ShowtimeModalComponent } from "../../shared/components/showtime-modal/showtime-modal.component";

@Component({
    standalone: true,
    selector: 'app-movie-detail-page',
    templateUrl: './movie-detail-page.component.html',
    styleUrls: [
        './movie-detail-page.component.scss'
    ],
    imports: [
        MetadataComponent,
        CommonModule,
        BookingComponent,
        MoviePhotosComponent,
        MovieDetailInfoComponent,
        ShowtimeModalComponent
    ]
})
export class MovieDetailPageComponent implements OnInit {

    movieId!: number;
    movie!: MovieDetailResponse;

    @ViewChild("showtimeModal") showtimeModal!: ShowtimeModalComponent;

    constructor(
        private route: ActivatedRoute,
        private movieService: MovieService
    ) { }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (!isNaN(id)) {
            this.movieId = id;
            this.loadMovies();
        }
    }

    loadMovies(): void {
        this.movieService.getMovieDetail(this.movieId).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.movie = response.data;
                }
                else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`error: ${err}`);
            }
        });
    }

    openShowtimeModal(): void {
        this.showtimeModal.open(this.movieId);
    }

}