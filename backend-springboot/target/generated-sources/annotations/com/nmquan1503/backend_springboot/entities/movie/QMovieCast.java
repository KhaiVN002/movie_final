package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieCast is a Querydsl query type for MovieCast
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieCast extends EntityPathBase<MovieCast> {

    private static final long serialVersionUID = 1484200894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieCast movieCast = new QMovieCast("movieCast");

    public final StringPath character = createString("character");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovie movie;

    public final NumberPath<Short> order = createNumber("order", Short.class);

    public final QPerson person;

    public QMovieCast(String variable) {
        this(MovieCast.class, forVariable(variable), INITS);
    }

    public QMovieCast(Path<? extends MovieCast> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieCast(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieCast(PathMetadata metadata, PathInits inits) {
        this(MovieCast.class, metadata, inits);
    }

    public QMovieCast(Class<? extends MovieCast> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie"), inits.get("movie")) : null;
        this.person = inits.isInitialized("person") ? new QPerson(forProperty("person"), inits.get("person")) : null;
    }

}

