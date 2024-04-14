package com.gonwan.toys.querydsl.model.generated;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * STUser3 is a Querydsl query type for TUser3
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class STUser3 extends com.querydsl.sql.RelationalPathBase<TUser3> {

    private static final long serialVersionUID = -2121365349;

    public static final STUser3 tUser3 = new STUser3("t_user3");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final StringPath username = createString("username");

    public final com.querydsl.sql.PrimaryKey<TUser3> primary = createPrimaryKey(id);

    public STUser3(String variable) {
        super(TUser3.class, forVariable(variable), "null", "t_user3");
        addMetadata();
    }

    public STUser3(String variable, String schema, String table) {
        super(TUser3.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public STUser3(String variable, String schema) {
        super(TUser3.class, forVariable(variable), schema, "t_user3");
        addMetadata();
    }

    public STUser3(Path<? extends TUser3> path) {
        super(path.getType(), path.getMetadata(), "null", "t_user3");
        addMetadata();
    }

    public STUser3(PathMetadata metadata) {
        super(TUser3.class, metadata, "null", "t_user3");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(password, ColumnMetadata.named("password").withIndex(3).ofType(Types.VARCHAR).withSize(50));
        addMetadata(username, ColumnMetadata.named("username").withIndex(2).ofType(Types.VARCHAR).withSize(50));
    }

}

