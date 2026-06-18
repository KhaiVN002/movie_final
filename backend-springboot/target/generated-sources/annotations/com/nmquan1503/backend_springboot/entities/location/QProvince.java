package com.nmquan1503.backend_springboot.entities.location;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProvince is a Querydsl query type for Province
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProvince extends EntityPathBase<Province> {

    private static final long serialVersionUID = -1046487882L;

    public static final QProvince province = new QProvince("province");

    public final NumberPath<Short> id = createNumber("id", Short.class);

    public final StringPath name = createString("name");

    public QProvince(String variable) {
        super(Province.class, forVariable(variable));
    }

    public QProvince(Path<? extends Province> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProvince(PathMetadata metadata) {
        super(Province.class, metadata);
    }

}

