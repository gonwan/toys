package com.gonwan.toys.mybatis;

import com.gonwan.toys.mybatis.mapper.*;
import com.gonwan.toys.mybatis.model.TUser;
import com.gonwan.toys.mybatis.model.TUser2;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class MybatisRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MybatisRunner.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void run(String... args) throws Exception {
        MybatisRunner mybatisRunner = appContext.getBean(MybatisRunner.class);
        mybatisRunner.init();
        //mybatisRunner.test1();
        mybatisRunner.test2();
    }

    @Transactional
    public void init() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TUserDynamicSqlSupport.TUser tUser = TUserDynamicSqlSupport.TUser;
            TUserMapper mapper = session.getMapper(TUserMapper.class);
            DeleteStatementProvider dsProvider = SqlBuilder.deleteFrom(tUser)
                    .build()
                    .render(RenderingStrategies.MYBATIS3);
            mapper.delete(dsProvider);
            logger.info("=== running insert ===");
            List<TUser> users = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                TUser user = new TUser();
                user.setUsername("username_" + i);
                user.setPassword("password_" + i);
                user.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
                user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                users.add(user);
            }
            /*
             * MultiRowInsertStatementProvider requires manual mapping.
             * see: https://mybatis.org/mybatis-dynamic-sql/docs/insert.html
             */
            mapper.insertMultiple(users); /* ids are filled */
        }
    }

    @Transactional
    public void test1() {
        logger.info("--- running test1 ---");
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TUserDynamicSqlSupport.TUser tUser = TUserDynamicSqlSupport.TUser;
            TUserMapper mapper = session.getMapper(TUserMapper.class);
            SelectStatementProvider ssProvider = SqlBuilder.select(tUser.allColumns())
                    .from(tUser)
                    .where(tUser.username, SqlBuilder.isEqualTo(("username_1")))
                    .build()
                    .render(RenderingStrategies.MYBATIS3);
            TUser user1 = mapper.selectOne(ssProvider).orElse(null);
            if (user1 != null) {
                logger.info("user1: username={} password={}", user1.getUsername(), user1.getPassword());
            }
            UpdateStatementProvider usProvider = SqlBuilder.update(tUser)
                    .set(tUser.password).equalTo("password_444")
                    .where(tUser.username, SqlBuilder.isEqualTo("username_4"))
                    .build()
                    .render(RenderingStrategies.MYBATIS3);
            mapper.update(usProvider);
        }
    }

    @Transactional(readOnly = true)
    public void test2() {
        logger.info("--- running test2 ---");
        try (SqlSession session = sqlSessionFactory.openSession()) {
            TUser2DynamicSqlSupport.TUser2 tUser2 = TUser2DynamicSqlSupport.TUser2;
            TUser3DynamicSqlSupport.TUser3 tUser3 = TUser3DynamicSqlSupport.TUser3;
            TUser2Mapper mapper = session.getMapper(TUser2Mapper.class);
            /*
             * 1. alias required, or jdbc cannot distinguish between same column names
             * 2. add a property to TUser2 with type of TUser3.
             * 3. add an XML mapper: TUser2Mapper.xml.
             * 4. modify TUser2Mapper class to use `TUser2JoinResult` result map.
             * 5. see: https://mybatis.org/mybatis-dynamic-sql/docs/select.html
             */
            SelectStatementProvider ssProvider = SqlBuilder.select(tUser2.allColumns(), tUser3.id.as("id3"), tUser3.username.as("username3"))
                    .from(tUser2)
                    .join(tUser3).on(tUser2.id, SqlBuilder.equalTo(tUser3.id))
                    .build()
                    .render(RenderingStrategies.MYBATIS3);
            /* cannot return tuple, as in querydsl. */
            TUser2 user2 = mapper.selectOne(ssProvider).orElse(null);
            if (user2 != null) {
                logger.info("user2={}", user2.getUsername());
            }
        }
    }

}
