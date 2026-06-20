package com.nmquan1503.backend_springboot.controllers.product;

import com.nmquan1503.backend_springboot.dtos.requests.product.ProductCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.ProductUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.dtos.responses.product.ProductDetailResponse;
import com.nmquan1503.backend_springboot.services.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @GetMapping("/details/branch/{branchId}")
    ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getProductDetailsByBranchId(
            @PathVariable Short branchId
    ) {
        ApiResponse<List<ProductDetailResponse>> response = ApiResponse.success(
                productService.getProductDetailsByBranchId(branchId)
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<List<ProductDetailResponse>>> getAllProducts() {
        ApiResponse<List<ProductDetailResponse>> response = ApiResponse.success(
                productService.getAllProducts()
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> createProduct(
            @RequestBody ProductCreationRequest request
    ) {
        productService.createProduct(request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Byte productId,
            @RequestBody ProductUpdateRequest request
    ) {
        productService.updateProduct(productId, request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

}

