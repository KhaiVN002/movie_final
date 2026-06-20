import { CommonModule } from "@angular/common";
import { Component, Input, OnInit } from "@angular/core";
import { SeatDetailResponse } from "../../../core/models/responses/theater/seat-detail-response.model";
import { ProductDetailResponse } from "../../../core/models/responses/product/product-detail-response.model";
import { TicketPriceResponse } from "../../../core/models/responses/ticket/ticket-price-response.model";
import { TicketPriceService } from "../../../core/services/ticket/ticket-price.service";
import dayjs from "dayjs";
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';
import { ShowtimeDetailResponse } from "../../../core/models/responses/showtime/showtime-detail-response.model";
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { ProductOrderRequest } from "../../../core/models/requests/product/product-order-request.model";
import { ReservationCreationRequest } from "../../../core/models/requests/reservation/reservation-creation-request.model";
import { ReservationService } from "../../../core/services/reservation/reservation.service";
import { Router } from "@angular/router";
dayjs.extend(isSameOrAfter);
dayjs.extend(customParseFormat);


@Component({
    standalone: true,
    selector: 'price-info',
    templateUrl: './price-info.component.html',
    styleUrls: [
        './price-info.component.scss'
    ],
    imports: [
        CommonModule
    ]
})
export class PriceInfoComponent implements OnInit {
    @Input() choosedSeats!: SeatDetailResponse[];
    @Input() productQuantity!: Map<number, number>;
    @Input() productMap!: Map<number, ProductDetailResponse>;
    @Input() showtime!: ShowtimeDetailResponse;

    basePriceMap: Map<number, TicketPriceResponse[]> = new Map();

    constructor(
        private ticketPriceService: TicketPriceService,
        private reservationService: ReservationService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadPrice();
    }

    loadPrice(): void {
        this.ticketPriceService.getAllBaseTicketPrice().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    for (let price of response.data) {
                        if (!this.basePriceMap.has(price.dayOfWeek)) {
                            this.basePriceMap.set(price.dayOfWeek, []);
                        }
                        this.basePriceMap.get(price.dayOfWeek)?.push(price);
                    }
                }
            }
        });
    }

    get choosedSeatString(): string {
        return this.choosedSeats.map(seat => seat.name).join(', ');
    }

    get seatsPrice(): number {
        const now = dayjs();
        const dow = now.day();
        const candidates = this.basePriceMap.get(dow);
        const currentTime = dayjs(now.format('HH:mm:ss'), 'HH:mm:ss');
        let basePrice = 0;
        if (!candidates) {
            return 0;
        }
        for (const entry of candidates) {
            const start = dayjs(typeof entry.timeRangeStart === 'string' ? entry.timeRangeStart : entry.timeRangeStart.format('HH:mm:ss'), 'HH:mm:ss');
            const end = dayjs(typeof entry.timeRangeEnd === 'string' ? entry.timeRangeEnd : entry.timeRangeEnd.format('HH:mm:ss'), 'HH:mm:ss');
            if (currentTime.isSameOrAfter(start) && currentTime.isBefore(end)) {
                basePrice = entry.price;
            }
        }
        let total = 0;
        for (const seat of this.choosedSeats) {
            total += basePrice + seat.type.extraFee + this.showtime.room.type.extraFee;
        }
        return total;
    }

    get listProduct(): ProductDetailResponse[] {
        let list = [];
        for (const [key, value] of this.productQuantity) {
            if (value) {
                const product = this.productMap.get(key);
                if (product) {
                    list.push(product)
                }
            }
        }
        return list;
    }

    getProductString(product: ProductDetailResponse): string {
        return `x${this.productQuantity.get(product.id)} ${product.name}`;
    }

    getProductPrice(product: ProductDetailResponse): number {
        let quantity = this.productQuantity.get(product.id);
        quantity = quantity ? quantity : 0;
        return quantity * product.price;
    }

    get totalPrice(): number {
        let total = 0;
        for (let product of this.listProduct) {
            let quantity = this.productQuantity.get(product.id);
            if (!quantity) quantity = 0;
            total += product.price * quantity;
        }
        return total + this.seatsPrice;
    }

    process(): void {
        if (this.choosedSeats.length === 0) {
            alert('Bạn chưa chọn ghế!');
            return;
        }
        const seatIds = this.choosedSeats.map(seat => seat.id);
        const productOrders: ProductOrderRequest[] = [];
        for (let [key, value] of this.productQuantity) {
            if (value) {
                productOrders.push({
                    productId: key,
                    quantity: value
                });
            }
        }
        const request: ReservationCreationRequest = {
            showtimeId: this.showtime.id,
            seatIds: seatIds,
            productOrders: productOrders
        };
        this.reservationService.createReservation(request).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    const reservationId = response.data;
                    this.router.navigate(['/checkout', reservationId])
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

}   
