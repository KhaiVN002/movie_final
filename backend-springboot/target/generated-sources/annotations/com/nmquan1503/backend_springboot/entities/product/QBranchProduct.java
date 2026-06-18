package com.nmquan1503.backend_springboot.entities.product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBranchProduct is a Querydsl query type for BranchProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBranchProduct extends EntityPathBase<BranchProduct> {

    private static final long serialVersionUID = 2066161371L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBranchProduct branchProduct = new QBranchProduct("branchProduct");

    public final com.nmquan1503.backend_springboot.entities.theater.QBranch branch;

    public final NumberPath<Short> id = createNumber("id", Short.class);

    public final QProduct product;

    public final QProductStatus status;

    public QBranchProduct(String variable) {
        this(BranchProduct.class, forVariable(variable), INITS);
    }

    public QBranchProduct(Path<? extends BranchProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBranchProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBranchProduct(PathMetadata metadata, PathInits inits) {
        this(BranchProduct.class, metadata, inits);
    }

    public QBranchProduct(Class<? extends BranchProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.branch = inits.isInitialized("branch") ? new com.nmquan1503.backend_springboot.entities.theater.QBranch(forProperty("branch"), inits.get("branch")) : null;
        this.product = inits.isInitialized("product") ? new QProduct(forProperty("product")) : null;
        this.status = inits.isInitialized("status") ? new QProductStatus(forProperty("status")) : null;
    }

}

