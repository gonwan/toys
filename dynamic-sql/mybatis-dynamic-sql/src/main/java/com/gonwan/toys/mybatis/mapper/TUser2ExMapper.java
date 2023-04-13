package com.gonwan.toys.mybatis.mapper;

import com.gonwan.toys.mybatis.generated.mapper.TUser2Mapper;
import com.gonwan.toys.mybatis.model.TUser2Ex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TUser2ExMapper extends TUser2Mapper {

    @SelectProvider(type= SqlProviderAdapter.class, method="select")
    @ResultMap("TUser2JoinResult")
    Optional<TUser2Ex> selectOneJoin(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("TUser2JoinResult")
    List<TUser2Ex> selectManyJoin(SelectStatementProvider selectStatement);

}
