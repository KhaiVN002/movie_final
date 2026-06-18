package com.nmquan1503.backend_springboot.entities.theater;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBranchStatus is a Querydsl query type for BranchStatus
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBranchStatus extends EntityPathBase<BranchStatus> {

    private static final long serialVersionUID = -1985672028L;

    public static final QBranchStatus branchStatus = new QBranchStatus("branchStatus");

    public final NumberPath<Byte> id = createNumber("id", Byte.class);

    public final StringPath name = createString("name");

    public QBranchStatus(String variable) {
        super(BranchStatus.class, forVariable(variable));
    }

    public QBranchStatus(Path<? extends BranchStatus> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBranchStatus(PathMetadata metadata) {
        super(BranchStatus.class, metadata);
    }

}

