package com.nmquan1503.backend_springboot.entities.ticket;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTicketStatus is a Querydsl query type for TicketStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicketStatus extends EntityPathBase<TicketStatus> {

    private static final long serialVersionUID = 1519228397L;

    public static final QTicketStatus ticketStatus = new QTicketStatus("ticketStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QTicketStatus(String variable) {
        super(TicketStatus.class, forVariable(variable));
    }

    public QTicketStatus(Path<? extends TicketStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTicketStatus(PathMetadata metadata) {
        super(TicketStatus.class, metadata);
    }

}

