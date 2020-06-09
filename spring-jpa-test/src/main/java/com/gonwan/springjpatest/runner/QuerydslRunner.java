package com.gonwan.springjpatest.runner;

import com.gonwan.springjpatest.model.QTUser;
import com.gonwan.springjpatest.model.STUser;
import com.gonwan.springjpatest.model.TUser;
import com.gonwan.springjpatest.model.TUserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.mysql.MySQLQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@Component
public class QuerydslRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(QuerydslRunner.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TUserRepository userRepository;

    @Autowired
    private QuerydslRunner querydslRunner;

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
        querydslRunner.init();
        //querydslRunner.test1();
        querydslRunner.test2();
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
        queryFactory.update(qtUser).where(qtUser.username.eq("username_3")).set(qtUser.password, "password_333").execute();
    }

    @Transactional
    public void test2() {
        logger.info("--- running test2 ---");
        STUser stUser = STUser.tUser;
        /* populate */
        TUser tUser = new TUser();
        tUser.setUsername("username_100");
        tUser.setPassword("password_100");
        tUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
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
