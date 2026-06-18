package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieCategory is a Querydsl query type for MovieCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieCategory extends EntityPathBase<MovieCategory> {

    private static final long serialVersionUID = -856095235L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieCategory movieCategory = new QMovieCategory("movieCategory");

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovie movie;

    public QMovieCategory(String variable) {
        this(MovieCategory.class, forVariable(variable), INITS);
    }

    public QMovieCategory(Path<? extends MovieCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieCategory(PathMetadata metadata, PathInits inits) {
        this(MovieCategory.class, metadata, inits);
    }

    public QMovieCategory(Class<? extends MovieCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie"), inits.get("movie")) : null;
    }

}

