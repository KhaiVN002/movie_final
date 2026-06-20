import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { ShowtimeService } from "../../../core/services/showtime/showtime.service";
import { ShowtimeOptionResponse } from "../../../core/models/responses/showtime/showtime-option-response.model";
import { ProvinceDetailResponse } from "../../../core/models/responses/location/province-detail-response.model";
import { BranchOptionResponse } from "../../../core/models/responses/theater/branch-option-response.model";
import { RoomTypeSimpleResponse } from "../../../core/models/responses/theater/room-type-simple-response.model";
import dayjs from "dayjs";

interface DateItem {
    dateString: string; // YYYY-MM-DD
    dayName: string;   // Mon, Tue...
    dayNum: string;    // 18, 19...
    month: string;     // 06, 07...
}

interface GroupedShowtimeByRoomType {
    roomType: RoomTypeSimpleResponse;
    showtimes: ShowtimeOptionResponse[];
}

interface GroupedShowtimeByBranch {
    branch: BranchOptionResponse;
    roomTypes: GroupedShowtimeByRoomType[];
}

@Component({
    standalone: true,
    selector: "app-showtime-modal",
    templateUrl: "./showtime-modal.component.html",
    styleUrls: ["./showtime-modal.component.scss"],
    imports: [CommonModule]
})
export class ShowtimeModalComponent implements OnInit {
    isOpen: boolean = false;
    movieId!: number;
    allShowtimes: ShowtimeOptionResponse[] = [];
    dates: DateItem[] = [];
    provinces: ProvinceDetailResponse[] = [];

    currentDate!: string;
    currentProvinceId!: number;
    isLoading: boolean = false;

    dayjs = dayjs;

    constructor(
        private showtimeService: ShowtimeService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.dates = this.generateDates();
    }

    open(movieId: number): void {
        this.movieId = movieId;
        this.isOpen = true;
        this.isLoading = true;
        this.allShowtimes = [];
        this.provinces = [];

        this.showtimeService.getShowtimeOptionByMovieId(movieId).subscribe({
            next: (response) => {
                this.isLoading = false;
                if (response.success && response.data) {
                    this.allShowtimes = response.data;
                    this.extractProvinces();
                    this.setDefaultSelections();
                } else {
                    console.error("API error:", response.message);
                }
            },
            error: (err) => {
                this.isLoading = false;
                console.error("Error fetching showtimes:", err);
            }
        });
    }

    close(): void {
        this.isOpen = false;
        this.allShowtimes = [];
    }

    private generateDates(): DateItem[] {
        const dates: DateItem[] = [];
        let start = dayjs();
        for (let i = 0; i < 30; i++) {
            dates.push({
                dateString: start.format("YYYY-MM-DD"),
                dayName: start.format("ddd"),
                dayNum: start.format("DD"),
                month: start.format("MM")
            });
            start = start.add(1, "day");
        }
        return dates;
    }

    private extractProvinces(): void {
        const provinceMap = new Map<number, ProvinceDetailResponse>();
        for (const showtime of this.allShowtimes) {
            const province = showtime.room.branch.province;
            provinceMap.set(province.id, province);
        }
        this.provinces = Array.from(provinceMap.values());
    }

    private setDefaultSelections(): void {
        // Set default province
        const hcm = this.provinces.find(p => 
            p.name.toLowerCase().includes("hồ chí minh") || p.name.toLowerCase().includes("hcm")
        );
        if (hcm) {
            this.currentProvinceId = hcm.id;
        } else if (this.provinces.length > 0) {
            this.currentProvinceId = this.provinces[0].id;
        }

        // Set default date to the first date that has showtimes, or today
        const availableDates = this.allShowtimes.map(s => dayjs(s.startTime).format("YYYY-MM-DD"));
        const firstAvailableDate = this.dates.find(d => availableDates.includes(d.dateString));
        if (firstAvailableDate) {
            this.currentDate = firstAvailableDate.dateString;
        } else if (this.dates.length > 0) {
            this.currentDate = this.dates[0].dateString;
        }
    }

    changeDate(dateString: string): void {
        this.currentDate = dateString;
    }

    changeProvince(provinceId: number): void {
        this.currentProvinceId = provinceId;
    }

    selectShowtime(showtimeId: number): void {
        this.close();
        this.router.navigate(["/booking", showtimeId]);
    }

    get filteredBranches(): GroupedShowtimeByBranch[] {
        if (!this.currentDate || !this.currentProvinceId) {
            return [];
        }

        const filtered = this.allShowtimes.filter(showtime => {
            const showtimeDate = dayjs(showtime.startTime).format("YYYY-MM-DD");
            return showtimeDate === this.currentDate && showtime.room.branch.province.id === this.currentProvinceId;
        });

        const branchMap = new Map<number, GroupedShowtimeByBranch>();
        for (const showtime of filtered) {
            const branch = showtime.room.branch;
            const roomType = showtime.room.type;

            if (!branchMap.has(branch.id)) {
                branchMap.set(branch.id, {
                    branch: branch,
                    roomTypes: []
                });
            }

            const branchGroup = branchMap.get(branch.id)!;
            let typeGroup = branchGroup.roomTypes.find(rt => rt.roomType.id === roomType.id);
            if (!typeGroup) {
                typeGroup = {
                    roomType: roomType,
                    showtimes: []
                };
                branchGroup.roomTypes.push(typeGroup);
            }

            typeGroup.showtimes.push(showtime);
        }

        const result = Array.from(branchMap.values());
        for (const bg of result) {
            // Sort room types (e.g. 2D, 3D, GOLDCLASS)
            bg.roomTypes.sort((a, b) => a.roomType.name.localeCompare(b.roomType.name));
            // Sort showtimes within each roomType by startTime
            for (const rt of bg.roomTypes) {
                rt.showtimes.sort((a, b) => dayjs(a.startTime).diff(dayjs(b.startTime)));
            }
        }

        return result;
    }
}
