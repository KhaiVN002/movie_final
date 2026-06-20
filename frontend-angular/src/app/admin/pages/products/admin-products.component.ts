import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminProductService } from '../../services/admin-product.service';
import { ProductDetailResponse } from '../../../core/models/responses/product/product-detail-response.model';
import { BranchOptionResponse } from '../../../core/models/responses/theater/branch-option-response.model';

@Component({
  standalone: true,
  selector: 'app-admin-products',
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AdminProductsComponent implements OnInit {
  products: any[] = [];
  branches: BranchOptionResponse[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  totalElements = 0;

  // Modals/Forms State
  showProductForm = false;
  showAssignForm = false;
  isEditingProduct = false;
  selectedProductId: number | null = null;

  productForm!: FormGroup;
  assignForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private productService: AdminProductService
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadData();
  }

  initForms(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      price: [0, [Validators.required, Validators.min(0)]],
      thumbnailURL: ['', [Validators.required]]
    });

    this.assignForm = this.fb.group({
      branchId: ['', [Validators.required]],
      productId: ['', [Validators.required]],
      productStatusId: [1, [Validators.required]] // Default: 1 (ACTIVE)
    });
  }

  loadData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.productService.getProducts().subscribe({
      next: (res) => {
        if (res.success) {
          const data: any = res.data || {};
          this.products = data.content || [];
          this.totalElements = data.totalElements || this.products.length;
        } else {
          this.errorMessage = res.message || 'Failed to load products';
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'An error occurred while loading products.';
        this.isLoading = false;
      }
    });

    this.productService.getBranches().subscribe({
      next: (res) => {
        if (res.success) {
          this.branches = res.data || [];
        }
      }
    });
  }

  openAddProduct(): void {
    this.isEditingProduct = false;
    this.selectedProductId = null;
    this.productForm.reset({ price: 0 });
    this.showProductForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  openEditProduct(product: ProductDetailResponse): void {
    this.isEditingProduct = true;
    this.selectedProductId = product.id;
    this.productForm.patchValue({
      name: product.name,
      price: product.price,
      thumbnailURL: product.thumbnailURL
    });
    this.showProductForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeProductForm(): void {
    this.showProductForm = false;
  }

  saveProduct(): void {
    if (this.productForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload = this.productForm.value;

    if (this.isEditingProduct && this.selectedProductId !== null) {
      this.productService.updateProduct(this.selectedProductId, payload).subscribe({
        next: (res) => {
          if (res.success) {
            this.successMessage = 'Product updated successfully!';
            this.loadData();
            this.showProductForm = false;
          } else {
            this.errorMessage = res.message || 'Failed to update product';
          }
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Error occurred during product update.';
          this.isLoading = false;
        }
      });
    } else {
      this.productService.createProduct(payload).subscribe({
        next: (res) => {
          if (res.success) {
            this.successMessage = 'Product created successfully!';
            this.loadData();
            this.showProductForm = false;
          } else {
            this.errorMessage = res.message || 'Failed to create product';
          }
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Error occurred during product creation.';
          this.isLoading = false;
        }
      });
    }
  }

  openAssignForm(product?: ProductDetailResponse): void {
    this.assignForm.reset({
      productId: product ? product.id : '',
      branchId: '',
      productStatusId: 1
    });
    this.showAssignForm = true;
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeAssignForm(): void {
    this.showAssignForm = false;
  }

  assignProduct(): void {
    if (this.assignForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload = {
      branchId: Number(this.assignForm.value.branchId),
      productId: Number(this.assignForm.value.productId),
      productStatusId: Number(this.assignForm.value.productStatusId)
    };

    this.productService.assignProductToBranch(payload).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = 'Product assigned to branch successfully!';
          this.showAssignForm = false;
        } else {
          this.errorMessage = res.message || 'Failed to assign product';
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Product might already be assigned to this branch.';
        this.isLoading = false;
      }
    });
  }
}
