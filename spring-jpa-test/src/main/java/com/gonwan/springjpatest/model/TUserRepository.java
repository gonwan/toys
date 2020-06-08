package com.gonwan.springjpatest.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface TUserRepository extends JpaRepository<TUser, Long>, QuerydslPredicateExecutor<TUser> {

}
