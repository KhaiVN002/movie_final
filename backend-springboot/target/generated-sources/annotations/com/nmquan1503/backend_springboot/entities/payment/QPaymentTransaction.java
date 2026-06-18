package com.nmquan1503.backend_springboot.entities.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPaymentTransaction is a Querydsl query type for PaymentTransaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentTransaction extends EntityPathBase<PaymentTransaction> {

    private static final long serialVersionUID = -2060938925L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPaymentTransaction paymentTransaction = new QPaymentTransaction("paymentTransaction");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPaymentMethod method;

    public final com.nmquan1503.backend_springboot.entities.reservation.QReservation reservation;

    public final QPaymentTransactionStatus status;

    public final DateTimePath<java.time.LocalDateTime> time = createDateTime("time", java.time.LocalDateTime.class);

    public final StringPath transactionId = createString("transactionId");

    public QPaymentTransaction(String variable) {
        this(PaymentTransaction.class, forVariable(variable), INITS);
    }

    public QPaymentTransaction(Path<? extends PaymentTransaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPaymentTransaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPaymentTransaction(PathMetadata metadata, PathInits inits) {
        this(PaymentTransaction.class, metadata, inits);
    }

    public QPaymentTransaction(Class<? extends PaymentTransaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.method = inits.isInitialized("method") ? new QPaymentMethod(forProperty("method"), inits.get("method")) : null;
        this.reservation = inits.isInitialized("reservation") ? new com.nmquan1503.backend_springboot.entities.reservation.QReservation(forProperty("reservation"), inits.get("reservation")) : null;
        this.status = inits.isInitialized("status") ? new QPaymentTransactionStatus(forProperty("status")) : null;
    }

}

