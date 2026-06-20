package com.nmquan1503.backend_springboot.services.product;

import com.nmquan1503.backend_springboot.dtos.requests.product.ProductCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.ProductUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.product.ProductDetailResponse;
import com.nmquan1503.backend_springboot.entities.product.Product;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.mappers.product.ProductMapper;
import com.nmquan1503.backend_springboot.repositories.product.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;

    ProductMapper productMapper;

    public List<ProductDetailResponse> getProductDetailsByBranchId(Short branchId) {
        List<Product> products = productRepository.findByBranchId(branchId);
        return productMapper.toListProductDetailResponse(products);
    }

    public List<ProductDetailResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toListProductDetailResponse(products);
    }


    public List<Product> fetchByIds(List<Byte> ids) {
        List<Product> products = productRepository.findAllById(ids);
        if (products.size() < ids.size()) {
            throw new GeneralException(ResponseCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    public Product fetchById(Byte id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.PRODUCT_NOT_FOUND));
    }

    public List<Product> fetchByReservationId(Long reservationId) {
        return productRepository.findByReservationId(reservationId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createProduct(ProductCreationRequest request) {
        Product product = productMapper.toProduct(request);
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(Byte productId, ProductUpdateRequest request) {
        Product product = fetchById(productId);
        productMapper.update(product, request);
        productRepository.save(product);
    }

    public boolean existsById(Byte productId) {
        return productRepository.existsById(productId);
    }

}
