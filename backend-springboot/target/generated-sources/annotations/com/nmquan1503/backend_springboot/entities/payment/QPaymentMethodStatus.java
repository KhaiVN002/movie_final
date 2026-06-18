package com.nmquan1503.backend_springboot.entities.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentMethodStatus is a Querydsl query type for PaymentMethodStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentMethodStatus extends EntityPathBase<PaymentMethodStatus> {

    private static final long serialVersionUID = -433672098L;

    public static final QPaymentMethodStatus paymentMethodStatus = new QPaymentMethodStatus("paymentMethodStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QPaymentMethodStatus(String variable) {
        super(PaymentMethodStatus.class, forVariable(variable));
    }

    public QPaymentMethodStatus(Path<? extends PaymentMethodStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentMethodStatus(PathMetadata metadata) {
        super(PaymentMethodStatus.class, metadata);
    }

}

