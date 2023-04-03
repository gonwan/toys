package com.gonwan.toys.mybatis.mapper;

import java.sql.JDBCType;
import java.util.Date;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TUserDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_user")
    public static final TUser TUser = new TUser();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.id")
    public static final SqlColumn<Long> id = TUser.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.username")
    public static final SqlColumn<String> username = TUser.username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.password")
    public static final SqlColumn<String> password = TUser.password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.version")
    public static final SqlColumn<Long> version = TUser.version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.create_time")
    public static final SqlColumn<Date> createTime = TUser.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.update_time")
    public static final SqlColumn<Date> updateTime = TUser.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_user")
    public static final class TUser extends AliasableSqlTable<TUser> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);

        public final SqlColumn<Long> version = column("version", JDBCType.BIGINT);

        public final SqlColumn<Date> createTime = column("create_time", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> updateTime = column("update_time", JDBCType.TIMESTAMP);

        public TUser() {
            super("t_user", TUser::new);
        }
    }
}