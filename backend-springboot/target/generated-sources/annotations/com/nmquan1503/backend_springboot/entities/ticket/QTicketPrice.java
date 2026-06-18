package com.nmquan1503.backend_springboot.entities.ticket;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTicketPrice is a Querydsl query type for TicketPrice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicketPrice extends EntityPathBase<TicketPrice> {

    private static final long serialVersionUID = -2032025618L;

    public static final QTicketPrice ticketPrice = new QTicketPrice("ticketPrice");

    public final NumberPath<Byte> dayOfWeek = createNumber("dayOfWeek", Byte.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public final TimePath<java.time.LocalTime> timeRangeEnd = createTime("timeRangeEnd", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> timeRangeStart = createTime("timeRangeStart", java.time.LocalTime.class);

    public QTicketPrice(String variable) {
        super(TicketPrice.class, forVariable(variable));
    }

    public QTicketPrice(Path<? extends TicketPrice> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTicketPrice(PathMetadata metadata) {
        super(TicketPrice.class, metadata);
    }

}

