package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieCrew is a Querydsl query type for MovieCrew
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieCrew extends EntityPathBase<MovieCrew> {

    private static final long serialVersionUID = 1484216800L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieCrew movieCrew = new QMovieCrew("movieCrew");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMovie movie;

    public final QPerson person;

    public final QPosition position;

    public QMovieCrew(String variable) {
        this(MovieCrew.class, forVariable(variable), INITS);
    }

    public QMovieCrew(Path<? extends MovieCrew> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieCrew(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieCrew(PathMetadata metadata, PathInits inits) {
        this(MovieCrew.class, metadata, inits);
    }

    public QMovieCrew(Class<? extends MovieCrew> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie"), inits.get("movie")) : null;
        this.person = inits.isInitialized("person") ? new QPerson(forProperty("person"), inits.get("person")) : null;
        this.position = inits.isInitialized("position") ? new QPosition(forProperty("position"), inits.get("position")) : null;
    }

}

