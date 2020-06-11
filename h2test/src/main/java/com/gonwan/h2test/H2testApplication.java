package com.gonwan.h2test;

import com.gonwan.h2test.model.User;
import com.gonwan.h2test.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
class H2Runner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(H2Runner.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setUsername("username1");
        user.setPassword("password1");
        userRepository.save(user);
        user = new User();
        user.setUsername("username2");
        user.setPassword("password2");
        userRepository.save(user);
        logger.info("Got users: {}", userRepository.findAll());
    }

}

@SpringBootApplication
public class H2testApplication {

    public static void main(String[] args) {
        SpringApplication.run(H2testApplication.class, args);
    }

}
