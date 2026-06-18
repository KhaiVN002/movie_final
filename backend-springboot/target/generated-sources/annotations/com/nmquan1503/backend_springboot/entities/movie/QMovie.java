package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovie is a Querydsl query type for Movie
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovie extends EntityPathBase<Movie> {

    private static final long serialVersionUID = 2120365023L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovie movie = new QMovie("movie");

    public final QAgeRating ageRating;

    public final StringPath backdropURL = createString("backdropURL");

    public final NumberPath<Short> duration = createNumber("duration", Short.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QLanguage originalLanguage;

    public final StringPath overview = createString("overview");

    public final StringPath posterURL = createString("posterURL");

    public final DatePath<java.time.LocalDate> releasedDate = createDate("releasedDate", java.time.LocalDate.class);

    public final StringPath tagline = createString("tagline");

    public final StringPath title = createString("title");

    public final StringPath trailerURL = createString("trailerURL");

    public QMovie(String variable) {
        this(Movie.class, forVariable(variable), INITS);
    }

    public QMovie(Path<? extends Movie> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovie(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovie(PathMetadata metadata, PathInits inits) {
        this(Movie.class, metadata, inits);
    }

    public QMovie(Class<? extends Movie> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ageRating = inits.isInitialized("ageRating") ? new QAgeRating(forProperty("ageRating")) : null;
        this.originalLanguage = inits.isInitialized("originalLanguage") ? new QLanguage(forProperty("originalLanguage")) : null;
    }

}

