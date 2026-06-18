package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoomTypeFeature is a Querydsl query type for RoomTypeFeature
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomTypeFeature extends EntityPathBase<RoomTypeFeature> {

    private static final long serialVersionUID = 1604348945L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoomTypeFeature roomTypeFeature = new QRoomTypeFeature("roomTypeFeature");

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath imageURL = createString("imageURL");

    public final QRoomType roomType;

    public final StringPath title = createString("title");

    public QRoomTypeFeature(String variable) {
        this(RoomTypeFeature.class, forVariable(variable), INITS);
    }

    public QRoomTypeFeature(Path<? extends RoomTypeFeature> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoomTypeFeature(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoomTypeFeature(PathMetadata metadata, PathInits inits) {
        this(RoomTypeFeature.class, metadata, inits);
    }

    public QRoomTypeFeature(Class<? extends RoomTypeFeature> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.roomType = inits.isInitialized("roomType") ? new QRoomType(forProperty("roomType")) : null;
    }

}

