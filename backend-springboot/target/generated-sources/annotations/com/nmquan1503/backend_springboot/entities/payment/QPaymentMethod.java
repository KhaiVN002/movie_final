package com.nmquan1503.backend_springboot.entities.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPaymentMethod is a Querydsl query type for PaymentMethod
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentMethod extends EntityPathBase<PaymentMethod> {

    private static final long serialVersionUID = 65281356L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPaymentMethod paymentMethod = new QPaymentMethod("paymentMethod");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public final QPaymentMethodStatus status;

    public final StringPath thumbnailURL = createString("thumbnailURL");

    public QPaymentMethod(String variable) {
        this(PaymentMethod.class, forVariable(variable), INITS);
    }

    public QPaymentMethod(Path<? extends PaymentMethod> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPaymentMethod(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPaymentMethod(PathMetadata metadata, PathInits inits) {
        this(PaymentMethod.class, metadata, inits);
    }

    public QPaymentMethod(Class<? extends PaymentMethod> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.status = inits.isInitialized("status") ? new QPaymentMethodStatus(forProperty("status")) : null;
    }

}

