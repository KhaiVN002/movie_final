package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSeatStatus is a Querydsl query type for SeatStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeatStatus extends EntityPathBase<SeatStatus> {

    private static final long serialVersionUID = -1461257529L;

    public static final QSeatStatus seatStatus = new QSeatStatus("seatStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QSeatStatus(String variable) {
        super(SeatStatus.class, forVariable(variable));
    }

    public QSeatStatus(Path<? extends SeatStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSeatStatus(PathMetadata metadata) {
        super(SeatStatus.class, metadata);
    }

}

