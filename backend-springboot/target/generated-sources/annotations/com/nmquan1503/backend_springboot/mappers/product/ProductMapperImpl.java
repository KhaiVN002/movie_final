package com.nmquan1503.backend_springboot.mappers.product;

import com.nmquan1503.backend_springboot.dtos.requests.product.ProductCreationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.product.ProductUpdateRequest;
import com.nmquan1503.backend_springboot.dtos.responses.product.ProductCheckoutResponse;
import com.nmquan1503.backend_springboot.dtos.responses.product.ProductDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.product.ProductSimpleResponse;
import com.nmquan1503.backend_springboot.entities.product.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:50+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDetailResponse toProductDetailResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDetailResponse.ProductDetailResponseBuilder productDetailResponse = ProductDetailResponse.builder();

        productDetailResponse.id( product.getId() );
        productDetailResponse.name( product.getName() );
        productDetailResponse.price( product.getPrice() );
        productDetailResponse.thumbnailURL( product.getThumbnailURL() );

        return productDetailResponse.build();
    }

    @Override
    public List<ProductDetailResponse> toListProductDetailResponse(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductDetailResponse> list = new ArrayList<ProductDetailResponse>( products.size() );
        for ( Product product : products ) {
            list.add( toProductDetailResponse( product ) );
        }

        return list;
    }

    @Override
    public ProductCheckoutResponse toProductCheckoutResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductCheckoutResponse.ProductCheckoutResponseBuilder productCheckoutResponse = ProductCheckoutResponse.builder();

        productCheckoutResponse.id( product.getId() );
        productCheckoutResponse.name( product.getName() );
        productCheckoutResponse.price( product.getPrice() );

        return productCheckoutResponse.build();
    }

    @Override
    public ProductSimpleResponse toProductSimpleResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductSimpleResponse.ProductSimpleResponseBuilder productSimpleResponse = ProductSimpleResponse.builder();

        productSimpleResponse.id( product.getId() );
        productSimpleResponse.name( product.getName() );

        return productSimpleResponse.build();
    }

    @Override
    public Product toProduct(ProductCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.name( request.getName() );
        product.price( request.getPrice() );
        product.thumbnailURL( request.getThumbnailURL() );

        return product.build();
    }

    @Override
    public void update(Product product, ProductUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            product.setName( request.getName() );
        }
        if ( request.getPrice() != null ) {
            product.setPrice( request.getPrice() );
        }
        if ( request.getThumbnailURL() != null ) {
            product.setThumbnailURL( request.getThumbnailURL() );
        }
    }
}
