package com.nmquan1503.backend_springboot.entities.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentTransactionStatus is a Querydsl query type for PaymentTransactionStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentTransactionStatus extends EntityPathBase<PaymentTransactionStatus> {

    private static final long serialVersionUID = 697287461L;

    public static final QPaymentTransactionStatus paymentTransactionStatus = new QPaymentTransactionStatus("paymentTransactionStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QPaymentTransactionStatus(String variable) {
        super(PaymentTransactionStatus.class, forVariable(variable));
    }

    public QPaymentTransactionStatus(Path<? extends PaymentTransactionStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentTransactionStatus(PathMetadata metadata) {
        super(PaymentTransactionStatus.class, metadata);
    }

}

