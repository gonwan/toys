package com.gonwan.kafka.tx.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("application")
@ConfigurationProperties("application")
public class Config {

    private String kafkaTestTopic;

    private String kafkaTestCompactTopic;

    public String getKafkaTestTopic() {
        return kafkaTestTopic;
    }

    public void setKafkaTestTopic(String kafkaTestTopic) {
        this.kafkaTestTopic = kafkaTestTopic;
    }

    public String getKafkaTestCompactTopic() {
        return kafkaTestCompactTopic;
    }

    public void setKafkaTestCompactTopic(String kafkaTestCompactTopic) {
        this.kafkaTestCompactTopic = kafkaTestCompactTopic;
    }

}
