package com.gonwan.kafka.tx;

import com.gonwan.kafka.tx.config.Config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class KafkaTransactionApplication {

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public ChainedKafkaTransactionManager<?, ?> chainedKafkaTransactionManager(
            KafkaTransactionManager<?, ?> kafkaTransactionManager,
            JpaTransactionManager transactionManager) {
        return new ChainedKafkaTransactionManager<>(kafkaTransactionManager, transactionManager);
    }

    @SuppressWarnings("unchecked")
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<?, ?> consumerFactory,
            ChainedKafkaTransactionManager<?, ?> chainedKafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, (ConsumerFactory<Object, Object>) consumerFactory);
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);
        factory.setMessageConverter(new JsonMessageConverter()); /* json support */
        return factory;
    }

    @Bean
    @Primary
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Primary
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    /*
     * Add standalone KafkaTemplate & KafkaTransactionManager,
     * so that zombie fencing is handled properly when partitions move from one instance to another after a rebalance.
     * See: https://docs.spring.io/spring-kafka/docs/2.6.0/reference/html/#transaction-id-prefix
     */
    @Bean("standaloneKafkaTemplate")
    public KafkaTemplate<?, ?> standaloneKafkaTemplate(KafkaProperties kafkaProperties, ProducerFactory<?, ?> producerFactory) {
        KafkaTemplate<?, ?> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setTransactionIdPrefix(String.format("%s%s-",
                kafkaProperties.getProducer().getTransactionIdPrefix(), UUID.randomUUID().toString()));
        kafkaTemplate.setAllowNonTransactional(true);
        return kafkaTemplate;
    }

    @Bean("standaloneKafkaTransactionManager")
    public KafkaTransactionManager<?, ?> standaloneKafkaTransactionManager(KafkaProperties kafkaProperties, ProducerFactory<?, ?> producerFactory) {
        KafkaTransactionManager<?, ?> kafkaTransactionManager = new KafkaTransactionManager<>(producerFactory);
        kafkaTransactionManager.setTransactionIdPrefix(String.format("%s%s-",
                kafkaProperties.getProducer().getTransactionIdPrefix(), UUID.randomUUID().toString()));
        return kafkaTransactionManager;
    }

    @Bean
    public NewTopic testKafkaTopic(Config config) {
        return TopicBuilder.name(config.getKafkaTestTopic()).partitions(4).replicas(1).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaTransactionApplication.class, args);
    }

}
