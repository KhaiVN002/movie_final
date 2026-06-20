import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ShowtimeDetailResponse } from "../../core/models/responses/showtime/showtime-detail-response.model";
import { ShowtimeService } from "../../core/services/showtime/showtime.service";
import { SeatService } from "../../core/services/theater/seat.service";
import { SeatDetailResponse } from "../../core/models/responses/theater/seat-detail-response.model";
import { ShowtimeListBannerComponent } from "../showtime-list/banner/showtime-list-banner.component";
import { InfoShowtimeComponent } from "./info/info-showtime.component";
import { SeatPlanComponent } from "./seat-plan/seat-plan.component";
import { ProductPlanComponent } from "./product-plan/product-plan.component";
import { ProductDetailResponse } from "../../core/models/responses/product/product-detail-response.model";
import { ProductService } from "../../core/services/product/product.service";
import { PriceInfoComponent } from "./price-info/price-info.component";

@Component({
    standalone: true,
    selector: 'booking-page',
    templateUrl: './booking-page.component.html',
    styleUrls: [
        './booking-page.component.scss'
    ],
    imports: [
        CommonModule,
        ShowtimeListBannerComponent,
        InfoShowtimeComponent,
        SeatPlanComponent,
        ProductPlanComponent,
        PriceInfoComponent,
    ]
})
export class BookingPageComponent implements OnInit {
    showtimeId!: number;
    showtime!: ShowtimeDetailResponse;
    seats!: SeatDetailResponse[];
    products!: ProductDetailResponse[];
    pageNumber: number = 1;
    choosedSeats: SeatDetailResponse[] = [];
    choosedProducts: ProductDetailResponse[] = [];
    productQuantity: Map<number, number> = new Map();
    productMap: Map<number, ProductDetailResponse> = new Map();

    constructor(
        private route: ActivatedRoute,
        private showtimeService: ShowtimeService,
        private router: Router,
        private seatService: SeatService,
        private productService: ProductService
    ) { }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (!isNaN(id)) {
            this.showtimeId = id;
            this.loadShowtime();
            this.loadSeats();
        }
    }

    changePage(pageNum: number) {
        this.pageNumber = pageNum;
    }

    loadProducts(): void {
        this.productService.getProductDetailsByBranchId(this.showtime.room.branch.id).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.products = response.data;
                    for (let product of this.products) {
                        this.productQuantity.set(product.id, 0);
                        this.productMap.set(product.id, product);
                    }
                }
                else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`error: ${err.error.message}`);
            }
        });
    }

    loadSeats(): void {
        this.seatService.getSeatDetailsByShowtimeId(this.showtimeId).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.seats = response.data;
                }
                else {
                    console.log(`api error: ${response.message}`);
                    this.router.navigate(['/home']);
                }
            },
            error: (err) => {
                console.log(`error: ${err.error.message}`);
                this.router.navigate(['/home']);
            }
        })
    }

    loadShowtime(): void {
        this.showtimeService.getShowtimeDetail(this.showtimeId).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.showtime = response.data;
                    this.loadProducts();
                }
                else {
                    console.log(`api error: ${response.message}`);
                    this.router.navigate(['/home']);
                }
            },
            error: (err) => {
                console.log(`api error: ${err.error.message}`);
                this.router.navigate(['/home']);
            }
        })
    }
}
