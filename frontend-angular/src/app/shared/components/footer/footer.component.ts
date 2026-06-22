import { Component } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { finalize } from "rxjs";
import { SupportRequestService } from "../../../core/services/support/support-request.service";

@Component({
    standalone:true,
    selector: "app-footer",
    templateUrl: "./footer.component.html",
    styleUrls: [
        "./footer.component.scss"
    ],
    imports: [
        RouterModule,
        CommonModule,
        FormsModule
    ]
})
export class FooterComponent {
    supportMessage = '';
    supportFeedback = '';
    supportError = false;
    isSubmittingSupport = false;

    constructor(private supportRequestService: SupportRequestService) {}

    submitSupportRequest(): void {
        const message = this.supportMessage.trim();
        this.supportFeedback = '';
        this.supportError = false;

        if (message.length < 5) {
            this.supportFeedback = 'Vui lòng mô tả vấn đề ít nhất 5 ký tự.';
            this.supportError = true;
            return;
        }

        this.isSubmittingSupport = true;
        this.supportRequestService.create(message).pipe(
            finalize(() => this.isSubmittingSupport = false)
        ).subscribe({
            next: response => {
                if (response.success) {
                    this.supportMessage = '';
                    this.supportFeedback = 'Yêu cầu đã được gửi tới quản trị viên.';
                    return;
                }
                this.supportFeedback = response.message || 'Không thể gửi yêu cầu lúc này.';
                this.supportError = true;
            },
            error: () => {
                this.supportFeedback = 'Không thể gửi yêu cầu lúc này. Vui lòng thử lại.';
                this.supportError = true;
            }
        });
    }

}
