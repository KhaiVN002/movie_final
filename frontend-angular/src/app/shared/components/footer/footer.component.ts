import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { RouterModule } from "@angular/router";
import { RoomTypeService } from "../../../core/services/theater/room-type.service";
import { RoomTypeIconResponse } from "../../../core/models/responses/theater/room-type-icon-response.model";
import { SlideShowComponent } from "../slideshow/slideshow.component";
import { SlideItemComponent } from "../slideshow/slide-item/slide-item.component";
import { CommonModule } from "@angular/common";
import { ApiResponse } from "../../../core/models/responses/api-response.model";
import { BaseComponent } from "../base/base.component";

@Component({
    standalone:true,
    selector: "app-footer",
    templateUrl: "./footer.component.html",
    styleUrls: [
        "./footer.component.scss"
    ],
    imports: [
        RouterModule,
        CommonModule
    ]
})
export class FooterComponent {

}
