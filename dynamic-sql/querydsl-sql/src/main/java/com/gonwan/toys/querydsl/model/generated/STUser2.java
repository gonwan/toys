package com.gonwan.toys.querydsl.model.generated;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * STUser2 is a Querydsl query type for TUser2
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class STUser2 extends com.querydsl.sql.RelationalPathBase<TUser2> {

    private static final long serialVersionUID = -2121365350;

    public static final STUser2 tUser2 = new STUser2("t_user2");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final StringPath username = createString("username");

    public final com.querydsl.sql.PrimaryKey<TUser2> primary = createPrimaryKey(id);

    public STUser2(String variable) {
        super(TUser2.class, forVariable(variable), "null", "t_user2");
        addMetadata();
    }

    public STUser2(String variable, String schema, String table) {
        super(TUser2.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public STUser2(String variable, String schema) {
        super(TUser2.class, forVariable(variable), schema, "t_user2");
        addMetadata();
    }

    public STUser2(Path<? extends TUser2> path) {
        super(path.getType(), path.getMetadata(), "null", "t_user2");
        addMetadata();
    }

    public STUser2(PathMetadata metadata) {
        super(TUser2.class, metadata, "null", "t_user2");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(password, ColumnMetadata.named("password").withIndex(3).ofType(Types.VARCHAR).withSize(50));
        addMetadata(username, ColumnMetadata.named("username").withIndex(2).ofType(Types.VARCHAR).withSize(50));
    }

}

