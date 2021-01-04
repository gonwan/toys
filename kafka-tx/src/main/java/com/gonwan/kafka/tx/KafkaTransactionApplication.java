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
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.util.backoff.FixedBackOff;

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

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaOperations<?, ?> kafkaOperations) {
        return new DeadLetterPublishingRecoverer(kafkaOperations);
    }

    @SuppressWarnings("unchecked")
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaTransactionListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<?, ?> consumerFactory,
            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer,
            ChainedKafkaTransactionManager<?, ?> chainedKafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, (ConsumerFactory<Object, Object>) consumerFactory);
        /* transaction id defaults '<transactionIdPrefix>.<group.id>.<topic>.<partition>' */
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager); /* transaction support */
        factory.setMessageConverter(new JsonMessageConverter()); /* json support */
        factory.setAfterRollbackProcessor(new DefaultAfterRollbackProcessor<>(deadLetterPublishingRecoverer, new FixedBackOff(0L, 2)));
        return factory;
    }

    /* Used in consumer-initiated transactions */
    @Bean
    @Primary
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<?, ?> producerFactory) {
        KafkaTemplate<?, ?> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setMessageConverter(new StringJsonMessageConverter());
        return kafkaTemplate;
    }

    /* Used in consumer-initiated transactions */
    @Bean
    @Primary
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    /*
     * Standalone KafkaTemplate used in producer-initiated transactions: executeInTransaction().
     * so that zombie fencing is handled properly when partitions move from one instance to another after a rebalance.
     * See: https://docs.spring.io/spring-kafka/docs/2.6.0/reference/html/#transaction-id-prefix
     */
    @Bean("standaloneKafkaTemplate")
    public KafkaTemplate<?, ?> standaloneKafkaTemplate(KafkaProperties kafkaProperties, ProducerFactory<?, ?> producerFactory) {
        KafkaTemplate<?, ?> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setMessageConverter(new StringJsonMessageConverter());
        /* transaction id defaults to 'transactionIdPrefix + n', override it. */
        kafkaTemplate.setTransactionIdPrefix(String.format("%s%s-",
                kafkaProperties.getProducer().getTransactionIdPrefix(), UUID.randomUUID().toString()));
        kafkaTemplate.setAllowNonTransactional(true);
        return kafkaTemplate;
    }

    /*
     * Standalone KafkaTransactionManager used in producer-initiated transactions: @Transactional.
     */
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
