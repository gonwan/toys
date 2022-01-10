package com.gonwan.springjpatest.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface TUserRepository extends JpaRepository<TUser, Long>, QuerydslPredicateExecutor<TUser> {

    /* named query, see jpa-named-queries.properties */
    @Query
    TUser queryById(Long id);

    /* hard to avoid n+1 fetch if tuple construction is used. */
    @Query("select tu, tu2, tu3" +
            " from TUser tu" +
            " left join TUser2 tu2 on tu2.id = tu.id" +
            " left join TUser3 tu3 on tu3.id = tu2.id" +
            " where tu.id = :id")
    List<Object[]> queryAllById(Long id);

}
