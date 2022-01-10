package com.gonwan.springjpatest.runner;

import com.gonwan.springjpatest.model.TUser;
import com.gonwan.springjpatest.model.TUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
//@Component
public class JpaRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(JpaRunner.class);

    @Autowired
    private TUserRepository userRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        logger.info("--- running test1 ---");
        logger.info("=== running named query ===");
        TUser u = userRepository.queryById(1L);
        logger.info("user={}", u);
        logger.info("=== running join query ===");
        List<Object[]> usersAll = userRepository.queryAllById(1L);
        logger.info("user={}, user2={}, user3={}", usersAll.get(0)[0], usersAll.get(0)[1], usersAll.get(0)[2]);
        userRepository.deleteAllInBatch();
        logger.info("=== running insert ===");
        List<TUser> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TUser user = new TUser();
            user.setUsername("username_" + i);
            user.setPassword("password_" + i);
            userList.add(user);
        }
        userList = userRepository.saveAll(userList);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            /* ignore */
        }
        logger.info("=== running update ===");
        for (int i = 0; i < 5; i++) {
            TUser user = userList.get(i);
            user.setPassword("password2_" + i);
            userList.add(user);
        }
        userRepository.saveAll(userList);
    }

}
