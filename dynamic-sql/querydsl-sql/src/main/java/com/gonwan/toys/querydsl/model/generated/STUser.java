package com.gonwan.toys.querydsl.model.generated;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * STUser is a Querydsl query type for TUser
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class STUser extends com.querydsl.sql.RelationalPathBase<TUser> {

    private static final long serialVersionUID = 2009778840;

    public static final STUser tUser = new STUser("t_user");

    public final DateTimePath<java.sql.Timestamp> createTime = createDateTime("createTime", java.sql.Timestamp.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final DateTimePath<java.sql.Timestamp> updateTime = createDateTime("updateTime", java.sql.Timestamp.class);

    public final StringPath username = createString("username");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public final com.querydsl.sql.PrimaryKey<TUser> primary = createPrimaryKey(id);

    public STUser(String variable) {
        super(TUser.class, forVariable(variable), "null", "t_user");
        addMetadata();
    }

    public STUser(String variable, String schema, String table) {
        super(TUser.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public STUser(String variable, String schema) {
        super(TUser.class, forVariable(variable), schema, "t_user");
        addMetadata();
    }

    public STUser(Path<? extends TUser> path) {
        super(path.getType(), path.getMetadata(), "null", "t_user");
        addMetadata();
    }

    public STUser(PathMetadata metadata) {
        super(TUser.class, metadata, "null", "t_user");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(createTime, ColumnMetadata.named("create_time").withIndex(5).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(password, ColumnMetadata.named("password").withIndex(3).ofType(Types.VARCHAR).withSize(50));
        addMetadata(updateTime, ColumnMetadata.named("update_time").withIndex(6).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(username, ColumnMetadata.named("username").withIndex(2).ofType(Types.VARCHAR).withSize(50));
        addMetadata(version, ColumnMetadata.named("version").withIndex(4).ofType(Types.BIGINT).withSize(19));
    }

}

