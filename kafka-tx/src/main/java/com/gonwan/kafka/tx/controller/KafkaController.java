package com.gonwan.kafka.tx.controller;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RequestMapping("/kafka")
@RestController
public class KafkaController {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @GetMapping("/topic/delete")
    public void topicDelete(String topic) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.deleteTopics(Collections.singletonList(topic));
        }
    }

}
