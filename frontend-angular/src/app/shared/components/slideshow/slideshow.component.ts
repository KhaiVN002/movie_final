import { AfterContentInit, AfterViewInit, ChangeDetectorRef, Component, ContentChildren, ElementRef, HostListener, Input, OnChanges, OnDestroy, QueryList, SimpleChanges, TemplateRef, ViewChild } from "@angular/core";
import { SlideItemComponent } from "./slide-item/slide-item.component";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SlideData } from "../../../core/models/common/slide-data.model";
import { WindowService } from "../../../core/services/platform-browser/window.service";
import { DocumentService } from "../../../core/services/platform-browser/document.service";
import { PreventClickOnDragDirective } from "../../directives/prevent-click-on-drag.directive";
import { BaseComponent } from "../base/base.component";
import { LoaderService } from "../../../core/services/ui/loader.service";
import { Subject, Subscription, takeUntil } from "rxjs";

@Component({
    standalone: true,
    selector: "app-slideshow",
    templateUrl: "./slideshow.component.html",
    styleUrls: ["./slideshow.component.scss"],
    imports: [
        CommonModule,
        RouterModule,
        PreventClickOnDragDirective
    ]
})
class SlideShowComponent implements AfterContentInit, OnDestroy {
    @ContentChildren(SlideItemComponent) itemComponents!: QueryList<SlideItemComponent>;
    
    items: SlideItemComponent[] = [];
    slides: SlideItemComponent[] = [];

    @ViewChild('wrapper')
    wrapperRef!: ElementRef<HTMLDivElement>
    
    @Input() widthPercent = 100;
    @Input() visibleCount = 3;
    @Input() interval = 3000;
    @Input() hasNavigationButtons:boolean = false;
    @Input() hasPager:boolean = false;
    
    get slideWidthPercent(): number {
        return 100 / this.visibleCount;
    };

    get slideWidthPx(): number {
        if (this.wrapperRef && this.wrapperRef.nativeElement) {
            return this.wrapperRef.nativeElement.clientWidth / this.visibleCount;
        }
        return 0;
    }
    
    isAnimateTrack = false;

    currentIndex = 0;
    currentOffset = 0;

    slideActions: number[] = [];

    autoSlideTimer: any = null;

    dragStartX = 0;
    dragCurrentX = 0;
    dragStartOffset = 0;
    isDragging = false;

    constructor(
        private windowService: WindowService, 
        private documentService: DocumentService,
        private cdr: ChangeDetectorRef
    ) { }

    private destroy$ = new Subject<void>();

    ngAfterContentInit(): void {
        this.itemComponents.changes
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
            this.initialSlides();
        });
        this.initialSlides();
    }

    initialSlides(): void {
        this.items = this.itemComponents.toArray();
        if (this.items.length === 0) {
            return;
        }

        this.slides =[
            ...this.items.slice(-this.visibleCount),
            ...this.items,
            ...this.items.slice(0, this.visibleCount)
        ];
        this.currentIndex = this.visibleCount;
        this.updateOffset();

        this.windowService.requestAnimationFrame(() => {
            this.isAnimateTrack = true;  
            this.startAutoSlide();
        });

        this.cdr.detectChanges();
    }

    ngOnDestroy(): void {
        this.windowService.clearInterval(this.autoSlideTimer);
        this.destroy$.next();
        this.destroy$.complete();
    }

    startAutoSlide(): void {
        if (this.autoSlideTimer) return;
        this.autoSlideTimer = this.windowService.setInterval(() => {
            this.nextSlide();
        }, this.interval);
    }

    get trackMovePercent(): number {
        return  - this.currentIndex * this.slideWidthPercent;
    }

    get trackMovieOffsetPx(): number {
        return this.dragCurrentX - this.dragStartX;
    }

    get trackTranslateX(): number {
        if (this.wrapperRef && this.wrapperRef.nativeElement) {
            return - this.currentIndex * this.wrapperRef.nativeElement.clientWidth / this.visibleCount 
                + this.dragCurrentX - this.dragStartX;
        }
        return 0;
    }

    updateOffset() {
        if (this.wrapperRef && this.wrapperRef.nativeElement) {
            this.currentOffset = - this.currentIndex * this.wrapperRef.nativeElement.clientWidth / this.visibleCount;
        }
        else {
            this.currentOffset = 0;
        }
    }

    nextSlide(): void {
        this.slideActions.push(1);
        if (this.slideActions.length === 1) {
            this.performSlide(1);
        }
    }

    prevSlide(): void {
        this.slideActions.push(-1);
        if (this.slideActions.length === 1) {
            this.performSlide(-1);
        }
    }

    performSlide(action: number) {
        this.currentIndex += action;
        this.updateOffset();
    }

    onTransitionEnd() {
        if (this.currentIndex >= this.slides.length - this.visibleCount) {
            this.windowService.requestAnimationFrame(() => {
                this.isAnimateTrack = false;
                this.currentIndex = this.visibleCount;
                this.updateOffset();
                this.windowService.requestAnimationFrame(() => {
                    this.isAnimateTrack = true;
                    this.slideActions.shift();
                    if (this.slideActions.length > 0) {
                        this.performSlide(this.slideActions[0]);
                        return;
                    }
                });
            });
        }
        else if (this.currentIndex <= 0) {
            this.windowService.requestAnimationFrame(() => {
                this.isAnimateTrack = false;
                this.currentIndex = this.items.length;
                this.updateOffset();
                this.windowService.requestAnimationFrame(() => {
                    this.isAnimateTrack = true;
                    this.slideActions.shift();
                    if (this.slideActions.length > 0) {
                        this.performSlide(this.slideActions[0]);
                        return;
                    }
                });
            });
        }
        else {
            this.slideActions.shift();
            if (this.slideActions.length > 0) {
                this.performSlide(this.slideActions[0]);
                return;
            }
        }
        // if (this.autoSlideTimer === null) {
        //     this.startAutoSlide();
        // }
    }

    onMouseEnter() {
        
        if (this.isDragging) {
            return;
        }
        if (this.slideActions.length > 1) {
            this.slideActions=[this.slideActions[0]];
        }
        this.windowService.clearInterval(this.autoSlideTimer);
        this.autoSlideTimer = null;
    }

    onMouseLeave() {
        if (this.isDragging) {
            return;
        }        
        this.startAutoSlide();
    }

    onMouseDown(event: MouseEvent) {
        this.documentService.addEventListener("mousemove", this.onMouseMove);
        this.documentService.addEventListener("mouseup", this.onMouseUp);
        this.isDragging = true;
        this.isAnimateTrack = false;
        this.dragStartX = event.clientX;
        this.dragStartOffset = this.currentOffset;
    }

    onMouseMove = (event: Event) => {
        if (!this.isDragging) {
            return;
        }
        const mouseEvent = event as MouseEvent;
        this.dragCurrentX = mouseEvent.clientX;
        this.windowService.requestAnimationFrame(
            () => {
                this.currentOffset = this.dragStartOffset + this.dragCurrentX - this.dragStartX;
                if (this.currentOffset > 0) {
                    this.currentOffset -= (Math.floor(this.currentOffset / (this.slideWidthPx * this.items.length)) + 1) * this.slideWidthPx * this.items.length;
                }
                else if (this.currentOffset < - (this.slideWidthPx * (this.visibleCount + this.items.length))) {
                    this.currentOffset += Math.floor((- this.currentOffset - this.visibleCount * this.slideWidthPx) / (this.slideWidthPx * this.items.length)) * this.slideWidthPx * this.items.length;
                }
            }
        );
    }

    onMouseUp = (event: Event) => {
        if (!this.isDragging) {
            return;
        }        
        const mouseEvent = event as MouseEvent;
        const dragCurrentX = mouseEvent.clientX;
        this.currentOffset = this.dragStartOffset + dragCurrentX - this.dragStartX;
        if (this.currentOffset > 0) {
            this.currentOffset -= (Math.floor(this.currentOffset / (this.slideWidthPx * this.items.length)) + 1) * this.slideWidthPx * this.items.length;
        }
        else if (this.currentOffset < - (this.slideWidthPx * (this.visibleCount + this.items.length))) {
            this.currentOffset += Math.floor((- this.currentOffset - this.visibleCount * this.slideWidthPx) / (this.slideWidthPx * this.items.length)) * this.slideWidthPx * this.items.length;
        }
        this.currentIndex = Math.floor(- (this.currentOffset - this.slideWidthPx / 2) / this.slideWidthPx);
        this.isDragging = false;
        this.isAnimateTrack = true;
        this.windowService.requestAnimationFrame(() => {
            this.updateOffset();
        });
        
        this.documentService.removeEventListener("mousemove", this.onMouseMove);
        this.documentService.removeEventListener("mouseup", this.onMouseUp);
    }

    clickNextSlide(event: MouseEvent): void {
        // this.windowService.clearInterval(this.autoSlideTimer);
        // this.autoSlideTimer = null;
        this.nextSlide();
    }

    clickPrevSlide(): void {
        this.windowService.clearInterval(this.autoSlideTimer);
        this.autoSlideTimer = null;
        this.prevSlide();
    }

    goToSlide(index: number): void {
        this.windowService.clearInterval(this.autoSlideTimer);
        this.autoSlideTimer = null;
        this.currentIndex = index + this.visibleCount;
        this.updateOffset();
    } 

    stopPropagation(event: MouseEvent): void {
        event.stopPropagation();
    }

    onSlideClick(event: MouseEvent, slide: SlideItemComponent): void {
        const target = event.target as HTMLElement;
        const buyBtn = target.closest(".buy-btn");
        const detailBtn = target.closest(".detail-btn");
        const playBtn = target.closest(".play-btn-wrapper");

        if (buyBtn) {
            event.preventDefault();
            event.stopPropagation();
            slide.buyClick.emit();
        } else if (detailBtn) {
            event.preventDefault();
            event.stopPropagation();
            slide.detailClick.emit();
        } else if (playBtn) {
            event.preventDefault();
            event.stopPropagation();
            slide.detailClick.emit();
        }
    }

}

export { SlideShowComponent };