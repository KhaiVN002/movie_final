import { CommonModule } from "@angular/common";
import { Component, ElementRef, Input, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { RouterModule } from "@angular/router";
import { BaseComponent } from "../../base/base.component";
import { LoaderService } from "../../../../core/services/ui/loader.service";

@Component({
    standalone: true,
    selector: "app-slide-item",
    imports: [
        CommonModule,
        RouterModule
    ],
    templateUrl: "./slide-item.component.html",
    styleUrls: [
        "./slide-item.component.scss"
    ]
})
class SlideItemComponent {
    @Input() href?: string;
    @Input() routerLink?: string | any[];

    constructor(
        public elementRef: ElementRef
    ) { }
}

export { SlideItemComponent };