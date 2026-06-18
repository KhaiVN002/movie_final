package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBranch is a Querydsl query type for Branch
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBranch extends EntityPathBase<Branch> {

    private static final long serialVersionUID = 1263969810L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBranch branch = new QBranch("branch");

    public final NumberPath<Short> id = createNumber("id", Short.class);

    public final StringPath name = createString("name");

    public final StringPath specificAddress = createString("specificAddress");

    public final QBranchStatus status;

    public final com.nmquan1503.backend_springboot.entities.location.QWard ward;

    public QBranch(String variable) {
        this(Branch.class, forVariable(variable), INITS);
    }

    public QBranch(Path<? extends Branch> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBranch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBranch(PathMetadata metadata, PathInits inits) {
        this(Branch.class, metadata, inits);
    }

    public QBranch(Class<? extends Branch> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.status = inits.isInitialized("status") ? new QBranchStatus(forProperty("status")) : null;
        this.ward = inits.isInitialized("ward") ? new com.nmquan1503.backend_springboot.entities.location.QWard(forProperty("ward"), inits.get("ward")) : null;
    }

}

