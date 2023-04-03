package com.gonwan.toys.mybatis.mapper;

import static com.gonwan.toys.mybatis.mapper.TUser3DynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.gonwan.toys.mybatis.model.TUser3;
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
public interface TUser3Mapper extends CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<TUser3>, CommonUpdateMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    BasicColumn[] selectList = BasicColumn.columnList(id, username, password);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.936+08:00", comments="Source Table: t_user3")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="TUser3Result", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR)
    })
    List<TUser3> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.936+08:00", comments="Source Table: t_user3")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TUser3Result")
    Optional<TUser3> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.936+08:00", comments="Source Table: t_user3")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, TUser3, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.936+08:00", comments="Source Table: t_user3")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, TUser3, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.936+08:00", comments="Source Table: t_user3")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.936+08:00", comments="Source Table: t_user3")
    default int insert(TUser3 row) {
        return MyBatis3Utils.insert(this::insert, row, TUser3, c ->
            c.map(id).toProperty("id")
            .map(username).toProperty("username")
            .map(password).toProperty("password")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default int insertMultiple(Collection<TUser3> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, TUser3, c ->
            c.map(id).toProperty("id")
            .map(username).toProperty("username")
            .map(password).toProperty("password")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default int insertSelective(TUser3 row) {
        return MyBatis3Utils.insert(this::insert, row, TUser3, c ->
            c.map(id).toPropertyWhenPresent("id", row::getId)
            .map(username).toPropertyWhenPresent("username", row::getUsername)
            .map(password).toPropertyWhenPresent("password", row::getPassword)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default Optional<TUser3> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, TUser3, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default List<TUser3> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, TUser3, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default List<TUser3> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, TUser3, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default Optional<TUser3> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, TUser3, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    static UpdateDSL<UpdateModel> updateAllColumns(TUser3 row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(row::getId)
                .set(username).equalTo(row::getUsername)
                .set(password).equalTo(row::getPassword);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(TUser3 row, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(row::getId)
                .set(username).equalToWhenPresent(row::getUsername)
                .set(password).equalToWhenPresent(row::getPassword);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default int updateByPrimaryKey(TUser3 row) {
        return update(c ->
            c.set(username).equalTo(row::getUsername)
            .set(password).equalTo(row::getPassword)
            .where(id, isEqualTo(row::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-04-03T20:24:40.937+08:00", comments="Source Table: t_user3")
    default int updateByPrimaryKeySelective(TUser3 row) {
        return update(c ->
            c.set(username).equalToWhenPresent(row::getUsername)
            .set(password).equalToWhenPresent(row::getPassword)
            .where(id, isEqualTo(row::getId))
        );
    }
}