package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSeatType is a Querydsl query type for SeatType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeatType extends EntityPathBase<SeatType> {

    private static final long serialVersionUID = 114715471L;

    public static final QSeatType seatType = new QSeatType("seatType");

    public final NumberPath<Double> extraFee = createNumber("extraFee", Double.class);

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QSeatType(String variable) {
        super(SeatType.class, forVariable(variable));
    }

    public QSeatType(Path<? extends SeatType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSeatType(PathMetadata metadata) {
        super(SeatType.class, metadata);
    }

}

