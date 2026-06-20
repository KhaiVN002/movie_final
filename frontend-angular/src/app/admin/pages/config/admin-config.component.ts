import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminConfigService } from '../../services/admin-config.service';

@Component({
  standalone: true,
  selector: 'app-admin-config',
  templateUrl: './admin-config.component.html',
  styleUrls: ['./admin-config.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AdminConfigComponent implements OnInit {
  ticketPrices: any[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Pricing Form State
  editingPriceId: number | null = null;
  priceForm!: FormGroup;

  // Website Config State
  websiteForm!: FormGroup;

  // Day mapping helper
  daysOfWeek = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

  constructor(
    private fb: FormBuilder,
    private configService: AdminConfigService
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadTicketPrices();
    this.loadWebsiteConfig();
  }

  initForms(): void {
    this.priceForm = this.fb.group({
      price: [0, [Validators.required, Validators.min(0)]]
    });

    this.websiteForm = this.fb.group({
      siteName: ['CGV Cinema', [Validators.required]],
      maintenanceMode: [false],
      announcement: ['Welcome to CGV Cinema! Book your favorite movies today!'],
      bannerUrl: ['https://images.unsplash.com/photo-1489599849927-2ee91cede3ba']
    });
  }

  loadTicketPrices(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.configService.getTicketPrices().subscribe({
      next: (res) => {
        if (res.success) {
          this.ticketPrices = res.data || [];
        } else {
          this.errorMessage = res.message || 'Failed to load ticket prices';
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'An error occurred while loading ticket prices.';
        this.isLoading = false;
      }
    });
  }

  loadWebsiteConfig(): void {
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem('cgv_website_config');
      if (stored) {
        try {
          this.websiteForm.patchValue(JSON.parse(stored));
        } catch {
          // Fallback to defaults
        }
      }
    }
  }

  getDayName(dayNum: number): string {
    return this.daysOfWeek[dayNum] || `Day ${dayNum}`;
  }

  startEditPrice(item: any): void {
    this.editingPriceId = item.id;
    this.priceForm.patchValue({ price: item.price });
    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelEditPrice(): void {
    this.editingPriceId = null;
  }

  savePrice(id: number): void {
    if (this.priceForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const newPrice = Number(this.priceForm.value.price);

    this.configService.updateTicketPrice(id, newPrice).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = 'Ticket price updated successfully!';
          this.loadTicketPrices();
          this.editingPriceId = null;
        } else {
          this.errorMessage = res.message || 'Failed to update ticket price';
          this.isLoading = false;
        }
      },
      error: () => {
        this.errorMessage = 'An error occurred while saving the price.';
        this.isLoading = false;
      }
    });
  }

  saveWebsiteConfig(): void {
    if (this.websiteForm.invalid) return;

    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';

    if (typeof window !== 'undefined') {
      localStorage.setItem('cgv_website_config', JSON.stringify(this.websiteForm.value));
      this.successMessage = 'Website configuration updated successfully!';
    } else {
      this.errorMessage = 'Cannot persist configuration outside the browser environment.';
    }
    this.isLoading = false;
  }
}
