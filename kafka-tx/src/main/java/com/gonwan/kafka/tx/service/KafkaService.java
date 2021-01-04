package com.gonwan.kafka.tx.service;

import com.gonwan.kafka.tx.model.User;
import com.gonwan.kafka.tx.model.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "#{application.kafkaTestTopic}")
    public void kafkaListener(User user) {
        logger.info("Received: {}", user.toString());
        if (user.getId() % 5 == 0) {
            throw new RuntimeException("Unknown exception");
        }
        userRepository.save(user);
    }

}
