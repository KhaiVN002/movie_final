package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBranchImage is a Querydsl query type for BranchImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBranchImage extends EntityPathBase<BranchImage> {

    private static final long serialVersionUID = -1043329431L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBranchImage branchImage = new QBranchImage("branchImage");

    public final QBranch branch;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath imageLink = createString("imageLink");

    public QBranchImage(String variable) {
        this(BranchImage.class, forVariable(variable), INITS);
    }

    public QBranchImage(Path<? extends BranchImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBranchImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBranchImage(PathMetadata metadata, PathInits inits) {
        this(BranchImage.class, metadata, inits);
    }

    public QBranchImage(Class<? extends BranchImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.branch = inits.isInitialized("branch") ? new QBranch(forProperty("branch"), inits.get("branch")) : null;
    }

}

