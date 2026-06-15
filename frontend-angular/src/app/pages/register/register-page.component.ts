import { Component, OnInit } from "@angular/core";
import { GenderOptionResponse } from "../../core/models/responses/user/gender-option-response.model";
import { ProvinceDetailResponse } from "../../core/models/responses/location/province-detail-response.model";
import { DistrictDetailResponse } from "../../core/models/responses/location/district-detail-response.model";
import { WardDetailResponse } from "../../core/models/responses/location/ward-detail-response.model";
import { GenderService } from "../../core/services/user/gender.service";
import { WardService } from "../../core/services/location/ward.service";
import { UserService } from "../../core/services/user/user.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { UserCreationRequest } from "../../core/models/requests/user/user-creation-request.model";
import dayjs from "dayjs";

@Component({
    standalone: true,
    selector: 'register-page',
    templateUrl: './register-page.component.html',
    styleUrls: [
        './register-page.component.scss'
    ],
    imports: [
        FormsModule,
        CommonModule,
        RouterModule,
    ]
})
export class RegisterPageComponent implements OnInit {

    fullName: string = '';
    phone: string = '';
    email: string = '';
    password: string = '';
    birthday_day: number = 0;
    birthday_month: number = 0;
    birthday_year: number = 0;
    genderId: number = 0;
    provinceId: number = 0;
    districtId: number = 0;
    wardId: number = 0;
    specificAddress: string | null = null;

    policy1: boolean = false;
    policy2: boolean = false;
    policy3: boolean = false;
    policy4: boolean = false;

    days = Array.from({length: 31}, (_, i) => i + 1);
    months = Array.from({length: 12}, (_, i) => i + 1);
    years = Array.from({length: 80}, (_, i) => 2013 - i);

    genders: GenderOptionResponse[] = [];

    locationMap: Map<number, Map<number, WardDetailResponse[]>> = new Map();

    provinces: Map<number, ProvinceDetailResponse> = new Map();
    districts: Map<number, DistrictDetailResponse> = new Map();

    notificationId: number = 0;
    notifications = [
        'Vui lòng nhập đầy đủ họ và tên',
        'Họ và tên không hợp lệ',
        'Vui lòng nhập số điện thoại',
        'Số điện thoại không hợp lệ',
        'Vui lòng nhập email',
        'Email không hợp lệ',
        'Vui lòng nhập mật khẩu',
        'Mật khẩu phải chứa ít nhất một chữ cái thường, một chữ cái in hoa, một chữ số, một ký tự đặc biệt, và có độ dài từ 8 đến 32 kí tự',
        'Vui lòng điền đầy đủ ngày sinh',
        'Ngày sinh không hợp lệ',
        'Vui lòng chọn giới tính',
        'Vui lòng chọn địa chỉ',
        'Vui lòng đánh dấu xác nhận thông tin và đồng ý với điều khoản sử dụng của CGV'
    ]

    success: boolean = false;

    constructor(
        private genderService: GenderService,
        private wardService: WardService,
        private userService: UserService
    ) { console.log(2) }

    ngOnInit(): void {
        this.loadGenders();
        this.loadLocation();
        console.log(1)
    }

    get listProvince(): ProvinceDetailResponse[] {
        if (!this.provinces) {
            return [];
        }
        return Array.from(this.provinces.values());
    }

    get listDistrict(): DistrictDetailResponse[] {
        if (!this.provinceId) return [];
        const keys = this.locationMap.get(this.provinceId)?.keys();
        if (keys) {
            let list = [];
            for (let id of Array.from(keys)) {
                const district = this.districts.get(id);
                if (district) {
                    list.push(district);
                }
            }
            return list;
        }
        return [];
    }

    get listWard(): WardDetailResponse[] {
        if (!this.provinceId || !this.districtId) {
            return [];
        }
        const wards = this.locationMap.get(this.provinceId)?.get(this.districtId);
        if (wards) {
            return wards;
        }
        return [];
    }

    loadLocation(): void {
        this.wardService.getAllWardDetails().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    for (const ward of response.data) {
                        const tempProvinceId = ward.district.province.id;
                        const tempDistrictId = ward.district.id;
                        if (!this.locationMap.has(tempProvinceId)) {
                            this.locationMap.set(tempProvinceId, new Map());
                            this.provinces.set(tempProvinceId, ward.district.province);
                        }
                        if (!this.locationMap.get(tempProvinceId)?.has(tempDistrictId)) {
                            this.locationMap.get(tempProvinceId)?.set(tempDistrictId, []);
                            this.districts.set(tempDistrictId, ward.district);
                        }
                        this.locationMap.get(tempProvinceId)?.get(tempDistrictId)?.push(ward);
                    }
                }
                else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`api error: ${err.error.message}`);
            }
        });
    }

    loadGenders(): void {
        this.genderService.getAllGenderOptions().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.genders = response.data;
                }
                else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`api error: ${err.error.message}`);
            }
        });
    }

    name_regexp = /^[a-zA-ZÀ-ỹ\s]+$/;
    phone_regexp = /^(\+84|0)\d{9}$/;
    mail_regexp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    pass_regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&\-+=()!?"]).{8,20}$/;
    register(): void {
        if (!this.fullName.trim()) {
            this.notificationId = 1;
            return;
        }
        if (!this.name_regexp.test(this.fullName)) {
            this.notificationId = 2;
            return;
        }
        if (!this.phone.trim()) {
            this.notificationId = 3;
            return;
        }
        if (!this.phone_regexp.test(this.phone)) {
            this.notificationId = 4;
            return;
        }
        if (!this.email.trim()) {
            this.notificationId = 5;
            return;
        }
        if (!this.mail_regexp.test(this.email)) {
            this.notificationId = 6;
            return;
        }
        if (!this.password) {
            this.notificationId = 7;
            return;
        }
        if (!this.pass_regexp.test(this.password)) {
            this.notificationId = 8;
            return;
        }
        if (!this.birthday_day || !this.birthday_month || !this.birthday_year) {
            this.notificationId = 9;
            return;
        }
        if (!this.isDate(this.birthday_day, this.birthday_month, this.birthday_year)) {
            this.notificationId = 10;
            return;
        }
        if (!this.genderId) {
            this.notificationId = 11;
            return;
        }
        if (!this.provinceId || !this.districtId || !this.wardId) {
            this.notificationId = 12;
            return;
        }
        if (!this.policy1 || !this.policy2 || !this.policy3 || !this.policy4) {
            this.notificationId = 13;
            return;
        }
        let request: UserCreationRequest = {
            fullName: this.fullName,
            phone: this.phone,
            email: this.email,
            password: this.password,
            birthday: dayjs(new Date(this.birthday_year, this.birthday_month - 1, this.birthday_day)).format('YYYY-MM-DD'),
            genderId: this.genderId,
            wardId: this.wardId,
            specificAddress: this.specificAddress
        };
        this.userService.createUser(request).subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.success = true;
                }
                else {
                    console.log(`api error: ${response.message}`);
                }
            },
            error: (err) => {
                console.log(`api error: ${err.error.message}`);
            }
        });
    }

    isDate(day: number, month: number, year: number): boolean {
        if (day < 1 || month < 1 || month > 12) {
            return false;
        }
        if (month in [1, 3, 5, 7, 8, 10, 12]) {
            if (day <= 31) {
                return true;
            }
            else {
                return false;
            }
        }
        if (month in [2, 4, 6, 9, 11]) {
            if (day <= 30) {
                return true;
            }
            else {
                return false;
            }
        }
        if ((year % 4 === 0 && year % 100 !== 0) || (year % 400 === 0)) {
            if (day <= 29) {
                return true;
            }
            else {
                return false;
            }
        }
        if (day <= 28) {
            return true;
        }
        return false;
    }

}