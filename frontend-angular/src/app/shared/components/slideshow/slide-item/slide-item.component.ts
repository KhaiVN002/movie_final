import { Component, ElementRef, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { CommonModule } from "@angular/common";
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

    @Output() buyClick = new EventEmitter<void>();
    @Output() detailClick = new EventEmitter<void>();

    constructor(
        public elementRef: ElementRef
    ) { }
}

export { SlideItemComponent };