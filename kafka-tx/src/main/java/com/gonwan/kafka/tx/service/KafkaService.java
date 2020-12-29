package com.gonwan.kafka.tx.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @KafkaListener(topics = "#{application.kafkaTestTopic}")
    public void listener(String s) {
        System.out.println(s);
    }

}
