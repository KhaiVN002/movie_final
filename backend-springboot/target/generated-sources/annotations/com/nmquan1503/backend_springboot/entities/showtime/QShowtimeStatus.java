package com.nmquan1503.backend_springboot.entities.showtime;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QShowtimeStatus is a Querydsl query type for ShowtimeStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShowtimeStatus extends EntityPathBase<ShowtimeStatus> {

    private static final long serialVersionUID = -1266977619L;

    public static final QShowtimeStatus showtimeStatus = new QShowtimeStatus("showtimeStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QShowtimeStatus(String variable) {
        super(ShowtimeStatus.class, forVariable(variable));
    }

    public QShowtimeStatus(Path<? extends ShowtimeStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QShowtimeStatus(PathMetadata metadata) {
        super(ShowtimeStatus.class, metadata);
    }

}

