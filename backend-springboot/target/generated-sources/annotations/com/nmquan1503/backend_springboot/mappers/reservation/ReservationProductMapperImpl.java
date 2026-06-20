package com.nmquan1503.backend_springboot.mappers.reservation;

import com.nmquan1503.backend_springboot.dtos.responses.product.ProductReservationItemResponse;
import com.nmquan1503.backend_springboot.entities.reservation.ReservationProduct;
import com.nmquan1503.backend_springboot.mappers.product.ProductMapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-20T14:12:49+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ReservationProductMapperImpl implements ReservationProductMapper {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ProductReservationItemResponse toProductReservationItemResponse(ReservationProduct reservationProduct) {
        if ( reservationProduct == null ) {
            return null;
        }

        ProductReservationItemResponse.ProductReservationItemResponseBuilder productReservationItemResponse = ProductReservationItemResponse.builder();

        productReservationItemResponse.product( productMapper.toProductCheckoutResponse( reservationProduct.getProduct() ) );
        productReservationItemResponse.quantity( reservationProduct.getQuantity() );

        return productReservationItemResponse.build();
    }

    @Override
    public List<ProductReservationItemResponse> toListProductReservationItemResponse(List<ReservationProduct> reservationProducts) {
        if ( reservationProducts == null ) {
            return null;
        }

        List<ProductReservationItemResponse> list = new ArrayList<ProductReservationItemResponse>( reservationProducts.size() );
        for ( ReservationProduct reservationProduct : reservationProducts ) {
            list.add( toProductReservationItemResponse( reservationProduct ) );
        }

        return list;
    }
}
