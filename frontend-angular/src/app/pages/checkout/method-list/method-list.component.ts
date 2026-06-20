import { CommonModule } from "@angular/common";
import { Component, Input, OnInit} from "@angular/core";
import { PaymentMethodDetailResponse } from "../../../core/models/responses/payment/payment-method-detail-response.model";
import { ReservationDetailResponse } from "../../../core/models/responses/reservation/reservation-detail-response.model";
import { PaymentTransactionCreationRequest } from "../../../core/models/requests/payment/payment-transaction-creation-request.model";
import { PaymentTransactionService } from "../../../core/services/payment/payment-transaction.service";
import { WindowService } from "../../../core/services/platform-browser/window.service";

@Component({
    standalone: true,
    selector: 'method-list',
    templateUrl: './method-list.component.html',
    styleUrls: [
        './method-list.component.scss'
    ],
    imports: [
        CommonModule
    ]
})
export class MethodListComponent implements OnInit {
    @Input() methods!: PaymentMethodDetailResponse[];
    @Input() reservation!: ReservationDetailResponse;
    selectedMethod!: number;

    constructor(
        private paymentTransactionService: PaymentTransactionService,
        private windowService: WindowService
    ) { }

    ngOnInit(): void {
        if (this.methods) {
            for (const method of this.methods) {
                if (method.status.name === 'ACTIVE') {
                    this.selectedMethod = method.id;
                    return;
                }
            }
        }
    }

    changeSelectedMethod(method: PaymentMethodDetailResponse): void {
        this.selectedMethod = method.id;
    }

    createPayment(): void {
        const request: PaymentTransactionCreationRequest = {
            paymentMethodId: this.selectedMethod,
            reservationId: this.reservation.id
        };
        this.paymentTransactionService.createPayment(request).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    const url = response.data.redirectURL;
                    this.windowService.redirect(url);
                }
                else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(err.error.message);
            }
        });
    }

}
