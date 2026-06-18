package com.nmquan1503.backend_springboot.entities.movie;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAgeRating is a Querydsl query type for AgeRating
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgeRating extends EntityPathBase<AgeRating> {

    private static final long serialVersionUID = 2072846507L;

    public static final QAgeRating ageRating = new QAgeRating("ageRating");

    public final StringPath code = createString("code");

    public final StringPath description = createString("description");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public QAgeRating(String variable) {
        super(AgeRating.class, forVariable(variable));
    }

    public QAgeRating(Path<? extends AgeRating> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAgeRating(PathMetadata metadata) {
        super(AgeRating.class, metadata);
    }

}

