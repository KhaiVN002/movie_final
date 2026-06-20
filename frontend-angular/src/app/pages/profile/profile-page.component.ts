import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { UserService } from "../../core/services/user/user.service";
import { ReservationService } from "../../core/services/reservation/reservation.service";
import { WardService } from "../../core/services/location/ward.service";
import { UserDetailResponse } from "../../core/models/responses/user/user-detail-response.model";
import { ReservationDetailResponse } from "../../core/models/responses/reservation/reservation-detail-response.model";
import { WardDetailResponse } from "../../core/models/responses/location/ward-detail-response.model";
import { ProvinceDetailResponse } from "../../core/models/responses/location/province-detail-response.model";
import { DistrictDetailResponse } from "../../core/models/responses/location/district-detail-response.model";
import { UserUpdateRequest } from "../../core/models/requests/user/user-update-request.model";
import dayjs from "dayjs";

@Component({
    standalone: true,
    selector: 'profile-page',
    templateUrl: './profile-page.component.html',
    styleUrls: [
        './profile-page.component.scss'
    ],
    imports: [
        CommonModule,
        FormsModule
    ]
})
export class ProfilePageComponent implements OnInit {

    page: number = 1; // 1: THÔNG TIN CHUNG, 2: CHI TIẾT TÀI KHOẢN, 3: LỊCH SỬ GIAO DỊCH
    user!: UserDetailResponse;
    reservations: ReservationDetailResponse[] = [];
    isLoadingHistory = false;
    dayjs = dayjs;

    // Address list caches
    allWards: WardDetailResponse[] = [];
    provinces: ProvinceDetailResponse[] = [];
    districts: DistrictDetailResponse[] = [];
    wards: WardDetailResponse[] = [];

    // Edit profile form fields
    fullName: string = "";
    genderId: number = 1;
    selectedProvinceId: number = 0;
    selectedDistrictId: number = 0;
    selectedWardId: number = 0;
    specificAddress: string = "";

    changePassword = false;
    newPassword = "";

    genders = [
        { id: 1, name: "Nam" },
        { id: 2, name: "Nữ" },
        { id: 3, name: "None" }
    ];

    constructor(
        private userService: UserService,
        private reservationService: ReservationService,
        private wardService: WardService
    ) { }

    ngOnInit(): void {
        this.loadUserInfo();
        this.loadHistory();
        this.loadAddressData();
    }

    loadUserInfo(): void {
        this.userService.getMyInfo().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.user = response.data;
                    this.initializeEditForm();
                } else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`error: ${err.error?.message || err.message}`);
            }
        });
    }

    loadHistory(): void {
        this.isLoadingHistory = true;
        this.reservationService.getMyReservations().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.reservations = response.data;
                }
                this.isLoadingHistory = false;
            },
            error: () => {
                this.isLoadingHistory = false;
            }
        });
    }

    loadAddressData(): void {
        this.wardService.getAllWardDetails().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.allWards = response.data;
                    
                    // Extract unique provinces
                    const provinceMap = new Map<number, ProvinceDetailResponse>();
                    for (const w of this.allWards) {
                        const p = w.district.province;
                        provinceMap.set(p.id, p);
                    }
                    this.provinces = Array.from(provinceMap.values());
                    
                    // Reinitialize edit form to bind address dropdowns correctly
                    this.initializeEditForm();
                }
            },
            error: (err) => {
                console.error("Error loading address data:", err);
            }
        });
    }

    initializeEditForm(): void {
        if (!this.user) return;
        this.fullName = this.user.fullName;
        this.genderId = this.user.gender?.id || 1;
        this.specificAddress = this.user.specificAddress || "";
        this.changePassword = false;
        this.newPassword = "";

        if (this.user.ward && this.allWards.length > 0) {
            this.selectedWardId = this.user.ward.id;
            this.selectedDistrictId = this.user.ward.district.id;
            this.selectedProvinceId = this.user.ward.district.province.id;

            // Trigger non-resetting updates to load select options
            this.updateDistricts(false);
            this.updateWards(false);
        }
    }

    onProvinceChange(): void {
        this.updateDistricts(true);
    }

    onDistrictChange(): void {
        this.updateWards(true);
    }

    updateDistricts(resetSelect: boolean): void {
        const districtMap = new Map<number, DistrictDetailResponse>();
        for (const w of this.allWards) {
            if (w.district.province.id === Number(this.selectedProvinceId)) {
                districtMap.set(w.district.id, w.district);
            }
        }
        this.districts = Array.from(districtMap.values());
        if (resetSelect) {
            this.selectedDistrictId = this.districts[0]?.id || 0;
            this.updateWards(true);
        }
    }

    updateWards(resetSelect: boolean): void {
        this.wards = this.allWards.filter(w => w.district.id === Number(this.selectedDistrictId));
        if (resetSelect) {
            this.selectedWardId = this.wards[0]?.id || 0;
        }
    }

    saveProfile(): void {
        if (!this.fullName.trim()) {
            alert("Vui lòng nhập tên!");
            return;
        }
        if (!this.selectedWardId) {
            alert("Vui lòng chọn địa chỉ đầy đủ (Tỉnh/Quận/Phường)!");
            return;
        }

        const request: UserUpdateRequest = {
            fullName: this.fullName,
            avatarURL: this.user.avatarURL || "",
            password: this.changePassword && this.newPassword.trim() ? this.newPassword : "",
            genderId: Number(this.genderId),
            wardId: Number(this.selectedWardId),
            specificAddress: this.specificAddress
        };

        this.userService.updateUser(this.user.id, request).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.user = response.data;
                    alert("Cập nhật thông tin thành công!");
                    this.page = 1; // Return to General Info
                } else {
                    alert("Cập nhật thất bại: " + response.message);
                }
            },
            error: (err) => {
                console.error(err);
                alert("Có lỗi xảy ra: " + (err.error?.message || err.message));
            }
        });
    }

    getSeatsString(seats: any[]): string {
        return seats.map(s => s.name).join(', ');
    }

    getConcessionsString(items: any[]): string {
        if (!items || items.length === 0) return 'Không có';
        return items.map(i => `${i.product.name} (x${i.quantity})`).join(', ');
    }

    get formattedBirthday(): string {
        if (!this.user || !this.user.birthday) return "";
        return dayjs(this.user.birthday).format("DD/MM/YYYY");
    }

    get formattedLocation(): string {
        if (!this.user || !this.user.ward) return "Chưa cập nhật";
        if (this.user.specificAddress) {
            return `${this.user.specificAddress}, ${this.user.ward.name}, ${this.user.ward.district.name}, ${this.user.ward.district.province.name}`;
        }
        return `${this.user.ward.name}, ${this.user.ward.district.name}, ${this.user.ward.district.province.name}`;
    }

    onAvatarSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files[0]) {
            const file = input.files[0];
            this.userService.uploadAvatar(this.user.id, file).subscribe({
                next: (response) => {
                    if (response.success && response.data) {
                        this.user.avatarURL = response.data;
                        alert("Cập nhật ảnh đại diện thành công!");
                    } else {
                        alert("Không thể upload ảnh: " + response.message);
                    }
                },
                error: (err) => {
                    console.error("Upload avatar error:", err);
                    alert("Có lỗi xảy ra khi upload ảnh!");
                }
            });
        }
    }

}