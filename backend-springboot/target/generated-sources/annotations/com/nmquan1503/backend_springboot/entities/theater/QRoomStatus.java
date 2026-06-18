package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomStatus is a Querydsl query type for RoomStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomStatus extends EntityPathBase<RoomStatus> {

    private static final long serialVersionUID = -1152469731L;

    public static final QRoomStatus roomStatus = new QRoomStatus("roomStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QRoomStatus(String variable) {
        super(RoomStatus.class, forVariable(variable));
    }

    public QRoomStatus(Path<? extends RoomStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomStatus(PathMetadata metadata) {
        super(RoomStatus.class, metadata);
    }

}

