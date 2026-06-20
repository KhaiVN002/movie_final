package com.nmquan1503.backend_springboot.controllers.product;

import com.nmquan1503.backend_springboot.dtos.requests.product.BranchProductCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.BranchProductUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.ApiResponse;
import com.nmquan1503.backend_springboot.services.product.BranchProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/branch-product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BranchProductController {

    BranchProductService branchProductService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> createBranchProduct(
            @RequestBody BranchProductCreationRequest request
    ) {
        branchProductService.createBranchProduct(request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{branchProductId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ApiResponse<Void>> updateBranchProduct(
            @PathVariable Short branchProductId,
            @RequestBody BranchProductUpdateRequest request
    ) {
        branchProductService.updateBranchProduct(branchProductId, request);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.ok().body(response);
    }

}

