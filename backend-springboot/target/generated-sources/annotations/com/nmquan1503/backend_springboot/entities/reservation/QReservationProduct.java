package com.nmquan1503.backend_springboot.entities.reservation;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservationProduct is a Querydsl query type for ReservationProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationProduct extends EntityPathBase<ReservationProduct> {

    private static final long serialVersionUID = 1705535192L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservationProduct reservationProduct = new QReservationProduct("reservationProduct");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.nmquan1503.backend_springboot.entities.product.QProduct product;

    public final NumberPath<Byte> quantity = createNumber("quantity", Byte.class);

    public final QReservation reservation;

    public QReservationProduct(String variable) {
        this(ReservationProduct.class, forVariable(variable), INITS);
    }

    public QReservationProduct(Path<? extends ReservationProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservationProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservationProduct(PathMetadata metadata, PathInits inits) {
        this(ReservationProduct.class, metadata, inits);
    }

    public QReservationProduct(Class<? extends ReservationProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.nmquan1503.backend_springboot.entities.product.QProduct(forProperty("product")) : null;
        this.reservation = inits.isInitialized("reservation") ? new QReservation(forProperty("reservation"), inits.get("reservation")) : null;
    }

}

