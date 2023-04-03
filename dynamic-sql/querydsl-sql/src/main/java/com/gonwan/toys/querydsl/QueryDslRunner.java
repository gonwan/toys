package com.gonwan.toys.querydsl;

import com.gonwan.toys.querydsl.model.generated.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class QueryDslRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(QueryDslRunner.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private MySQLQueryFactory mySQLQueryFactory;

    @Override
    public void run(String... args) throws Exception {
        QueryDslRunner querydslRunner = appContext.getBean(QueryDslRunner.class);
        querydslRunner.init();
        //querydslRunner.test1();
        //querydslRunner.test2();
        querydslRunner.test3();
    }

    @Transactional
    public void init() {
        STUser stUser = STUser.tUser;
        mySQLQueryFactory.delete(stUser).execute();
        logger.info("=== running insert ===");
        SQLInsertClause insertClause = mySQLQueryFactory.insert(stUser);
        //insertClause.setBatchToBulk(true);
        for (int i = 0; i < 10; i++) {
            TUser tUser = new TUser();
            tUser.setUsername("username_" + i);
            tUser.setPassword("password_" + i);
            tUser.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            tUser.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            insertClause.populate(tUser).addBatch();
        }
        List<Long> ids = insertClause.executeWithKeys(stUser.id);
        logger.info("ids: {}", ids);
    }

    @Transactional
    public void test1() {
        logger.info("--- running test1 ---");
        STUser stUser = STUser.tUser;
        TUser tUser1 = mySQLQueryFactory.selectFrom(stUser)
                .where(stUser.username.eq("username_1"))
                .fetchFirst();
        if (tUser1 != null) {
            logger.info("users1: username={} password={}", tUser1.getUsername(), tUser1.getPassword());
        }
        mySQLQueryFactory.update(stUser)
                .where(stUser.username.eq("username_4"))
                .set(stUser.password, "password_444")
                .execute();
    }

    @Transactional(readOnly = true)
    public void test2() {
        /* see: http://querydsl.com/static/querydsl/5.0.0/reference/html/ch03s02.html */
        logger.info("--- running test2 ---");
        STUser2 qtUser2 = STUser2.tUser2;
        STUser3 qtUser3 = STUser3.tUser3;
        Tuple res = mySQLQueryFactory.select(qtUser2, Projections.bean(TUser3.class, qtUser3.id, qtUser3.username))
                .from(qtUser2, qtUser3)
                .where(qtUser2.id.eq(qtUser3.id))
                .fetchFirst();
        TUser2 tUser2 = res.get(0, TUser2.class);
        TUser3 tUser3 = res.get(1, TUser3.class);
        if (tUser2 != null && tUser3 != null) {
            logger.info("tuser2={} tuser3={}", tUser2.getUsername(), tUser3.getUsername());
        }
    }

    @Transactional
    public void test3() {
        logger.info("--- running test3 ---");
        STUser stUser = STUser.tUser;
        /* populate */
        TUser tUser = new TUser();
        tUser.setUsername("username_100");
        tUser.setPassword("password_100");
        tUser.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        tUser.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        mySQLQueryFactory.insert(stUser).populate(tUser).execute();
        /* replace */
        SQLInsertClause insertClause = mySQLQueryFactory.replace(stUser);
        for (int i = 2; i <= 3; i++) {
            insertClause.set(stUser.id, (long) i)
                    .set(stUser.username, "username_" + i*100)
                    .set(stUser.password, "password_" + i*100)
                    .set(stUser.createTime, Timestamp.valueOf(LocalDateTime.now()))
                    .set(stUser.updateTime, new Timestamp(System.currentTimeMillis()))
                    .addBatch();
        }
        List<Long> replaceIds = insertClause.executeWithKeys(stUser.id);
        logger.info("replaceIds: {}", replaceIds); /* [2,3,3,4] */
        /* on duplicate update */
        SQLInsertClause insertClause2 = mySQLQueryFactory.insertOnDuplicateKeyUpdate(stUser,
                        SQLExpressions.set(stUser.username, stUser.username),
                        SQLExpressions.set(stUser.password, stUser.password));
        for (int i = 4; i <= 5; i++) {
            insertClause2.set(stUser.id, (long) i)
                    .set(stUser.username, "username_" + i*100)
                    .set(stUser.password, "password_" + i*100)
                    .set(stUser.createTime, Timestamp.valueOf(LocalDateTime.now()))
                    .set(stUser.updateTime, new Timestamp(System.currentTimeMillis()))
                    .addBatch();
        }
        List<Long> duplicateIds = insertClause2.executeWithKeys(stUser.id);
        logger.info("duplicateIds: {}", duplicateIds);
    }

}
