package com.nmquan1503.backend_springboot.entities.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1240993915L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final StringPath avatarURL = createString("avatarURL");

    public final DatePath<java.time.LocalDate> birthday = createDate("birthday", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> creationTime = createDateTime("creationTime", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final StringPath fullName = createString("fullName");

    public final QGender gender;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final StringPath specificAddress = createString("specificAddress");

    public final com.nmquan1503.backend_springboot.entities.location.QWard ward;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.gender = inits.isInitialized("gender") ? new QGender(forProperty("gender")) : null;
        this.ward = inits.isInitialized("ward") ? new com.nmquan1503.backend_springboot.entities.location.QWard(forProperty("ward"), inits.get("ward")) : null;
    }

}

