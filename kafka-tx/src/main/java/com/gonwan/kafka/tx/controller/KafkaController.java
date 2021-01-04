package com.gonwan.kafka.tx.controller;

import com.gonwan.kafka.tx.config.Config;
import com.gonwan.kafka.tx.model.User;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RequestMapping("/kafka")
@RestController
public class KafkaController {

    private long id = 0;

    @Autowired
    private Config config;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    @Qualifier("standaloneKafkaTemplate")
    private KafkaTemplate<?, ?> kafkaTemplate;

    @GetMapping("/topic/send")
    public void topicSend() {
        User user = new User();
        user.setId(++id);
        user.setUsername("hahausername");
        user.setPassword("hahapassword");
        Message<User> message = MessageBuilder
                .withPayload(user)
                .setHeader(KafkaHeaders.TOPIC, config.getKafkaTestTopic())
                .build();
        kafkaTemplate.send(message);
    }

    @GetMapping("/topic/delete")
    public void topicDelete(String topic) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.deleteTopics(Collections.singletonList(topic));
        }
    }

}
