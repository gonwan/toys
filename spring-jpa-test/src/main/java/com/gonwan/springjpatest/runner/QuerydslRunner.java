package com.gonwan.springjpatest.runner;

import com.gonwan.springjpatest.model.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

interface TUserDto {

    Long getId();

    String getUsername();

}

/*
 * Jpa QueryByExample can be also used, but QueryDSL seems to be easier to understand.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Component
public class QuerydslRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(QuerydslRunner.class);

    @Autowired
    private ApplicationContext appContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TUserRepository userRepository;

    @Autowired
    private MySQLQueryFactory mySQLQueryFactory;

    @Transactional
    public void init() {
        userRepository.deleteAllInBatch();
        List<TUser> userList = new ArrayList<>();
        logger.info("=== running insert ===");
        for (int i = 0; i < 10; i++) {
            TUser user = new TUser();
            user.setUsername("username_" + i);
            user.setPassword("password_" + i);
            userList.add(user);
        }
        userRepository.saveAll(userList);
    }

    @Override
    public void run(String... args) throws Exception {
        QuerydslRunner querydslRunner = appContext.getBean(QuerydslRunner.class);
        querydslRunner.init();
        //querydslRunner.test1();
        //querydslRunner.test2();
        querydslRunner.test3();
    }

    @Transactional
    public void test1() {
        logger.info("--- running test1 ---");
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QTUser qtUser = QTUser.tUser;
        List<TUser> users1 = queryFactory.selectFrom(qtUser).where(qtUser.username.eq("username_1")).fetch();
        logger.info("users1: {}", users1);
        Iterable<TUser> users2 = userRepository.findAll(qtUser.username.eq("username_2"));
        logger.info("users2: {}", users2);
        List<TUserDto> users3 = userRepository.findBy(
                    qtUser.username.eq("username_3"),
                    q -> q.as(TUserDto.class).sortBy(Sort.unsorted()).all()); /* still selecting all fields, nonsense. */
        logger.info("users3: {}", users3);
        queryFactory.update(qtUser).where(qtUser.username.eq("username_4")).set(qtUser.password, "password_444").execute();
    }

    public void test2() {
        /* see: http://querydsl.com/static/querydsl/5.0.0/reference/html/ch03s02.html */
        logger.info("--- running test2 ---");
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QTUser2 qtUser2 = QTUser2.tUser2;
        QTUser3 qtUser3 = QTUser3.tUser3;
        Tuple res = queryFactory.select(qtUser2, Projections.bean(TUser3.class, qtUser3.id, qtUser3.username))
                .from(qtUser2, qtUser3)
                .where(qtUser2.id.eq(qtUser3.id))
                .fetchFirst();
        logger.info("tuser2={} tuser3={}", res.get(0, TUser2.class), res.get(1, TUser3.class));
    }

    @Transactional
    public void test3() {
        logger.info("--- running test3 ---");
        STUser stUser = STUser.tUser;
        /* populate */
        TUser tUser = new TUser();
        tUser.setUsername("username_100");
        tUser.setPassword("password_100");
        tUser.setCreateTime(new Date(System.currentTimeMillis()));
        tUser.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        mySQLQueryFactory.insert(stUser).populate(tUser).execute();
        /* replace */
        mySQLQueryFactory.replace(stUser)
                .set(stUser.id, 200L)
                .set(stUser.username, "username_200")
                .set(stUser.password, "password_200")
                .set(stUser.createTime, new Timestamp(System.currentTimeMillis()))
                .set(stUser.updateTime, new Timestamp(System.currentTimeMillis()))
                .execute();
        /* on duplicate update */
        mySQLQueryFactory.insertOnDuplicateKeyUpdate(stUser,
                    stUser.username.eq("username_300"),
                    stUser.password.eq("password_300"))
                .set(stUser.id, 300L)
                .set(stUser.username, "username_300")
                .set(stUser.password, "password_300")
                .set(stUser.createTime, new Timestamp(System.currentTimeMillis()))
                .set(stUser.updateTime, new Timestamp(System.currentTimeMillis()))
                .execute();
    }

}
