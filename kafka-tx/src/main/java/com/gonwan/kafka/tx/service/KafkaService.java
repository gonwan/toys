package com.gonwan.kafka.tx.service;

import com.gonwan.kafka.tx.model.User;
import com.gonwan.kafka.tx.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "#{application.kafkaTestTopic}")
    public void kafkaListener(User user) {
        userRepository.save(user);
    }

}
