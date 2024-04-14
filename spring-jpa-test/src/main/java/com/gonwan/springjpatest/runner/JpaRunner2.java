package com.gonwan.springjpatest.runner;

import com.gonwan.springjpatest.model.TUser;
import com.gonwan.springjpatest.repository.TUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * MySQL with MySQL driver should be used.
 * Or the fractional seconds is truncated, since MariaDB does not honor sendFractionalSeconds=true.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
//@Component
public class JpaRunner2 implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JpaRunner2.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private TUserRepository userRepository;

    private TUser findUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public Long init() {
        userRepository.deleteAllInBatch();
        TUser user = new TUser();
        user.setUsername("11111user");
        user.setPassword("11111pass");
        user = userRepository.save(user);
        logger.info("saved user: {}", user);
        return user.getId();
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("--- running test2 ---");
        JpaRunner2 jpaRunner2 = appContext.getBean(JpaRunner2.class);
        Long id = jpaRunner2.init();
        ExecutorService es = Executors.newFixedThreadPool(2);
        /* user1 */
        es.execute(() -> {
            TUser u1 = findUser(id);
            u1.setPassword("22222pass");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                /* ignore */
            }
            try {
                userRepository.save(u1);
            } catch (DataAccessException e) {
                logger.info("user1 error", e);
                logger.info("user1 after error: {}", findUser(id));
                return;
            }
            logger.info("user1 finished: {}", findUser(id));
        });
        /* user2 */
        es.execute(() -> {
            TUser u2 = findUser(id);
            u2.setPassword("33333pass");
            try {
                userRepository.save(u2);
            } catch (DataAccessException e) {
                logger.info("user2 error", e);
                logger.info("user2 after error: {}", findUser(id));
                return;
            }
            logger.info("user2 finished: {}", findUser(id));
        });
        /* clean up */
        es.shutdown();
        try {
            es.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.info("interrupted", e);
        }
    }

}
