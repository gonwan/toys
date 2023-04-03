package com.gonwan.toys.mybatis.mapper;

import static com.gonwan.toys.mybatis.mapper.TUserDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.gonwan.toys.mybatis.model.TUser;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface TUserMapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<TUser>, CommonUpdateMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.927+08:00", comments="Source Table: t_user")
    BasicColumn[] selectList = BasicColumn.columnList(id, username, password, version, createTime, updateTime);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.918+08:00", comments="Source Table: t_user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TUserResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR),
        @Result(column="version", property="version", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<TUser> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.92+08:00", comments="Source Table: t_user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TUserResult")
    Optional<TUser> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.92+08:00", comments="Source Table: t_user")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, TUser, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.92+08:00", comments="Source Table: t_user")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, TUser, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.921+08:00", comments="Source Table: t_user")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.921+08:00", comments="Source Table: t_user")
    default int insert(TUser row) {
        return MyBatis3Utils.insert(this::insert, row, TUser, c ->
            c.map(id).toProperty("id")
            .map(username).toProperty("username")
            .map(password).toProperty("password")
            .map(version).toProperty("version")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.924+08:00", comments="Source Table: t_user")
    default int insertMultiple(Collection<TUser> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, TUser, c ->
            c.map(id).toProperty("id")
            .map(username).toProperty("username")
            .map(password).toProperty("password")
            .map(version).toProperty("version")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.924+08:00", comments="Source Table: t_user")
    default int insertSelective(TUser row) {
        return MyBatis3Utils.insert(this::insert, row, TUser, c ->
            c.map(id).toPropertyWhenPresent("id", row::getId)
            .map(username).toPropertyWhenPresent("username", row::getUsername)
            .map(password).toPropertyWhenPresent("password", row::getPassword)
            .map(version).toPropertyWhenPresent("version", row::getVersion)
            .map(createTime).toPropertyWhenPresent("createTime", row::getCreateTime)
            .map(updateTime).toPropertyWhenPresent("updateTime", row::getUpdateTime)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.927+08:00", comments="Source Table: t_user")
    default Optional<TUser> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, TUser, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.928+08:00", comments="Source Table: t_user")
    default List<TUser> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, TUser, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.928+08:00", comments="Source Table: t_user")
    default List<TUser> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, TUser, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.929+08:00", comments="Source Table: t_user")
    default Optional<TUser> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.929+08:00", comments="Source Table: t_user")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, TUser, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.929+08:00", comments="Source Table: t_user")
    static UpdateDSL<UpdateModel> updateAllColumns(TUser row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(username).equalTo(row::getUsername)
                .set(password).equalTo(row::getPassword)
                .set(version).equalTo(row::getVersion)
                .set(createTime).equalTo(row::getCreateTime)
                .set(updateTime).equalTo(row::getUpdateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.93+08:00", comments="Source Table: t_user")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(TUser row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(username).equalToWhenPresent(row::getUsername)
                .set(password).equalToWhenPresent(row::getPassword)
                .set(version).equalToWhenPresent(row::getVersion)
                .set(createTime).equalToWhenPresent(row::getCreateTime)
                .set(updateTime).equalToWhenPresent(row::getUpdateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.93+08:00", comments="Source Table: t_user")
    default int updateByPrimaryKey(TUser row) {
        return update(c ->
            c.set(username).equalTo(row::getUsername)
            .set(password).equalTo(row::getPassword)
            .set(version).equalTo(row::getVersion)
            .set(createTime).equalTo(row::getCreateTime)
            .set(updateTime).equalTo(row::getUpdateTime)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.931+08:00", comments="Source Table: t_user")
    default int updateByPrimaryKeySelective(TUser row) {
        return update(c ->
            c.set(username).equalToWhenPresent(row::getUsername)
            .set(password).equalToWhenPresent(row::getPassword)
            .set(version).equalToWhenPresent(row::getVersion)
            .set(createTime).equalToWhenPresent(row::getCreateTime)
            .set(updateTime).equalToWhenPresent(row::getUpdateTime)
            .where(id, isEqualTo(row::getId))
        );
    }
}