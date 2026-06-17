import { CommonModule, isPlatformBrowser } from "@angular/common";
import {
    Component,
    Inject,
    OnInit,
    PLATFORM_ID
} from "@angular/core";
import { ActivatedRoute, RouterModule } from "@angular/router";
import { switchMap, take } from "rxjs/operators";

import { PaymentTransactionService } from "../../core/services/payment/payment-transaction.service";
import { LoadingOverlayComponent } from "../../shared/components/loading-overlay/loading-overlay.component";

@Component({
    standalone: true,
    selector: 'payment-result-page',
    templateUrl: './payment-result-page.component.html',
    styleUrls: [
        './payment-result-page.component.scss'
    ],
    imports: [
        CommonModule,
        LoadingOverlayComponent,
        RouterModule
    ]
})
export class PaymentResultPageComponent implements OnInit {

    status: 'success' | 'fail' | 'loading' = 'loading';

    constructor(
        @Inject(PLATFORM_ID)
        private platformId: Object,

        private route: ActivatedRoute,
        private paymentTransactionService: PaymentTransactionService
    ) { }

    ngOnInit(): void {

        if (!isPlatformBrowser(this.platformId)) {
            return;
        }

        this.route.queryParams
            .pipe(
                take(1),
                switchMap(params =>
                    this.paymentTransactionService.handleReturn(params)
                )
            )
            .subscribe({
                next: (response) => {
                    this.status = response.success
                        ? 'success'
                        : 'fail';

                    if (!response.success) {
                        console.error(response.message);
                    }
                },
                error: (err) => {
                    console.error(err);
                    this.status = 'fail';
                }
            });
    }
}