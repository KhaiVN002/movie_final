import { Component } from "@angular/core";
import { MovieBannerComponent } from "./banner/movie-banner.component";
import { MovieGridComponent } from "./movie-grid/movie-grid.component";

@Component({
    selector: "app-movies-page",
    templateUrl: "./movies-page.component.html",
    styleUrls: [
        "./movies-page.component.scss"
    ],
    imports: [
        MovieBannerComponent,
        MovieGridComponent
    ]
})
export class MoviesPageComponent {

}
