package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieKeyword is a Querydsl query type for MovieKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieKeyword extends EntityPathBase<MovieKeyword> {

    private static final long serialVersionUID = 2065840394L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieKeyword movieKeyword = new QMovieKeyword("movieKeyword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QKeyword keyword;

    public final QMovie movie;

    public QMovieKeyword(String variable) {
        this(MovieKeyword.class, forVariable(variable), INITS);
    }

    public QMovieKeyword(Path<? extends MovieKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieKeyword(PathMetadata metadata, PathInits inits) {
        this(MovieKeyword.class, metadata, inits);
    }

    public QMovieKeyword(Class<? extends MovieKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.keyword = inits.isInitialized("keyword") ? new QKeyword(forProperty("keyword")) : null;
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie"), inits.get("movie")) : null;
    }

}

