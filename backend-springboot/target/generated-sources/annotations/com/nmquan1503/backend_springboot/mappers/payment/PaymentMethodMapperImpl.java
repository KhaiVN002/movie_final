package com.nmquan1503.backend_springboot.mappers.payment;

import com.nmquan1503.backend_springboot.dtos.responses.payment.PaymentMethodDetailResponse;
import com.nmquan1503.backend_springboot.dtos.responses.payment.PaymentMethodStatusResponse;
import com.nmquan1503.backend_springboot.entities.payment.PaymentMethod;
import com.nmquan1503.backend_springboot.entities.payment.PaymentMethodStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T10:11:09+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PaymentMethodMapperImpl implements PaymentMethodMapper {

    @Override
    public PaymentMethodDetailResponse toPaymentMethodDetailResponse(PaymentMethod paymentMethod) {
        if ( paymentMethod == null ) {
            return null;
        }

        PaymentMethodDetailResponse.PaymentMethodDetailResponseBuilder paymentMethodDetailResponse = PaymentMethodDetailResponse.builder();

        paymentMethodDetailResponse.id( paymentMethod.getId() );
        paymentMethodDetailResponse.name( paymentMethod.getName() );
        paymentMethodDetailResponse.status( paymentMethodStatusToPaymentMethodStatusResponse( paymentMethod.getStatus() ) );
        paymentMethodDetailResponse.thumbnailURL( paymentMethod.getThumbnailURL() );

        return paymentMethodDetailResponse.build();
    }

    protected PaymentMethodStatusResponse paymentMethodStatusToPaymentMethodStatusResponse(PaymentMethodStatus paymentMethodStatus) {
        if ( paymentMethodStatus == null ) {
            return null;
        }

        PaymentMethodStatusResponse.PaymentMethodStatusResponseBuilder paymentMethodStatusResponse = PaymentMethodStatusResponse.builder();

        paymentMethodStatusResponse.id( paymentMethodStatus.getId() );
        paymentMethodStatusResponse.name( paymentMethodStatus.getName() );

        return paymentMethodStatusResponse.build();
    }
}
