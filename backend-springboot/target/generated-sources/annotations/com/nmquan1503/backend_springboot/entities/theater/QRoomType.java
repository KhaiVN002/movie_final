package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomType is a Querydsl query type for RoomType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomType extends EntityPathBase<RoomType> {

    private static final long serialVersionUID = -948649179L;

    public static final QRoomType roomType = new QRoomType("roomType");

    public final NumberPath<Double> extraFee = createNumber("extraFee", Double.class);

    public final StringPath iconURL = createString("iconURL");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public final StringPath overview = createString("overview");

    public final StringPath slogan = createString("slogan");

    public final StringPath thumbnailURL = createString("thumbnailURL");

    public QRoomType(String variable) {
        super(RoomType.class, forVariable(variable));
    }

    public QRoomType(Path<? extends RoomType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomType(PathMetadata metadata) {
        super(RoomType.class, metadata);
    }

}

