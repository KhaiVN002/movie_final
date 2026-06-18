package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieReview is a Querydsl query type for MovieReview
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieReview extends EntityPathBase<MovieReview> {

    private static final long serialVersionUID = 821130263L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieReview movieReview = new QMovieReview("movieReview");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> creationTime = createDateTime("creationTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovie movie;

    public final NumberPath<Byte> rating = createNumber("rating", Byte.class);

    public final com.nmquan1503.backend_springboot.entities.user.QUser user;

    public QMovieReview(String variable) {
        this(MovieReview.class, forVariable(variable), INITS);
    }

    public QMovieReview(Path<? extends MovieReview> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieReview(PathMetadata metadata, PathInits inits) {
        this(MovieReview.class, metadata, inits);
    }

    public QMovieReview(Class<? extends MovieReview> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie"), inits.get("movie")) : null;
        this.user = inits.isInitialized("user") ? new com.nmquan1503.backend_springboot.entities.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

