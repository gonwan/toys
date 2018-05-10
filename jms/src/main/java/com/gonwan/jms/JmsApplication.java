package com.gonwan.jms;

import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.PooledConnectionFactory;
import org.apache.qpid.url.URLSyntaxException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import java.net.URISyntaxException;

@Component
@ConfigurationProperties(prefix = "application")
class Config {

    private String qpidConnectionUrlString;

    private String qpidDestinationTestDirect;

    private String qpidDestinationTestFanout;

    private String qpidDestinationTestFanoutConsumer1;

    private String qpidDestinationTestFanoutConsumer2;

    private String qpidDestinationTestTopic1;

    private String qpidDestinationTestTopic2;

    private String qpidDestinationTestTopicConsumer1;

    private String qpidDestinationTestTopicConsumer2;

    public String getQpidConnectionUrlString() {
        return qpidConnectionUrlString;
    }

    public void setQpidConnectionUrlString(String qpidConnectionUrlString) {
        this.qpidConnectionUrlString = qpidConnectionUrlString;
    }

    public String getQpidDestinationTestDirect() {
        return qpidDestinationTestDirect;
    }

    public void setQpidDestinationTestDirect(String qpidDestinationTestDirect) {
        this.qpidDestinationTestDirect = qpidDestinationTestDirect;
    }

    public String getQpidDestinationTestFanout() {
        return qpidDestinationTestFanout;
    }

    public void setQpidDestinationTestFanout(String qpidDestinationTestFanout) {
        this.qpidDestinationTestFanout = qpidDestinationTestFanout;
    }

    public String getQpidDestinationTestFanoutConsumer1() {
        return qpidDestinationTestFanoutConsumer1;
    }

    public void setQpidDestinationTestFanoutConsumer1(String qpidDestinationTestFanoutConsumer1) {
        this.qpidDestinationTestFanoutConsumer1 = qpidDestinationTestFanoutConsumer1;
    }

    public String getQpidDestinationTestFanoutConsumer2() {
        return qpidDestinationTestFanoutConsumer2;
    }

    public void setQpidDestinationTestFanoutConsumer2(String qpidDestinationTestFanoutConsumer2) {
        this.qpidDestinationTestFanoutConsumer2 = qpidDestinationTestFanoutConsumer2;
    }

    public String getQpidDestinationTestTopic1() {
        return qpidDestinationTestTopic1;
    }

    public void setQpidDestinationTestTopic1(String qpidDestinationTestTopic1) {
        this.qpidDestinationTestTopic1 = qpidDestinationTestTopic1;
    }

    public String getQpidDestinationTestTopic2() {
        return qpidDestinationTestTopic2;
    }

    public void setQpidDestinationTestTopic2(String qpidDestinationTestTopic2) {
        this.qpidDestinationTestTopic2 = qpidDestinationTestTopic2;
    }

    public String getQpidDestinationTestTopicConsumer1() {
        return qpidDestinationTestTopicConsumer1;
    }

    public void setQpidDestinationTestTopicConsumer1(String qpidDestinationTestTopicConsumer1) {
        this.qpidDestinationTestTopicConsumer1 = qpidDestinationTestTopicConsumer1;
    }

    public String getQpidDestinationTestTopicConsumer2() {
        return qpidDestinationTestTopicConsumer2;
    }

    public void setQpidDestinationTestTopicConsumer2(String qpidDestinationTestTopicConsumer2) {
        this.qpidDestinationTestTopicConsumer2 = qpidDestinationTestTopicConsumer2;
    }

}

@SpringBootApplication
public class JmsApplication {

    @Bean
    public ConnectionFactory connectionFactory(Config config) throws URLSyntaxException {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectionURLString(config.getQpidConnectionUrlString());
        return factory;
    }

    @Bean
    public Destination testDirect(Config config) throws URISyntaxException {
        Destination dst = new AMQAnyDestination(config.getQpidDestinationTestDirect());
        return dst;
    }

    @Bean
    public Destination testFanout(Config config) throws URISyntaxException {
        Destination dst = new AMQAnyDestination(config.getQpidDestinationTestFanout());
        return dst;
    }

    @Bean
    public Destination testTopic1(Config config) throws URISyntaxException {
        Destination dst = new AMQAnyDestination(config.getQpidDestinationTestTopic1());
        return dst;
    }

    @Bean
    public Destination testTopic2(Config config) throws URISyntaxException {
        Destination dst = new AMQAnyDestination(config.getQpidDestinationTestTopic2());
        return dst;
    }

    public static void main(String[] args) {
        SpringApplication.run(JmsApplication.class, args);
    }

}
