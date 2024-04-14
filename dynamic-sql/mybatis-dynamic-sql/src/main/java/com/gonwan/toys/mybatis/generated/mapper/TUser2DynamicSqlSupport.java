package com.gonwan.toys.mybatis.generated.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TUser2DynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_user2")
    public static final TUser2 TUser2 = new TUser2();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user2.id")
    public static final SqlColumn<Long> id = TUser2.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user2.username")
    public static final SqlColumn<String> username = TUser2.username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user2.password")
    public static final SqlColumn<String> password = TUser2.password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: t_user2")
    public static final class TUser2 extends AliasableSqlTable<TUser2> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);

        public TUser2() {
            super("t_user2", TUser2::new);
        }
    }
}