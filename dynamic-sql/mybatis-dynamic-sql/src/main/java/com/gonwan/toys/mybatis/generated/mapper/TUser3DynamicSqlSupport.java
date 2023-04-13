package com.gonwan.toys.mybatis.generated.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TUser3DynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_user3")
    public static final TUser3 TUser3 = new TUser3();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user3.id")
    public static final SqlColumn<Long> id = TUser3.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user3.username")
    public static final SqlColumn<String> username = TUser3.username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user3.password")
    public static final SqlColumn<String> password = TUser3.password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_user3")
    public static final class TUser3 extends AliasableSqlTable<TUser3> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);

        public TUser3() {
            super("t_user3", TUser3::new);
        }
    }
}