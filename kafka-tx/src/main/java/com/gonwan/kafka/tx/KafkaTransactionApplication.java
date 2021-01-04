package com.gonwan.kafka.tx;

import com.gonwan.kafka.tx.config.Config;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
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

    private static Logger logger = LoggerFactory.getLogger(KafkaTransactionApplication.class);

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
    public AfterRollbackProcessor<?, ?> afterRollbackProcessor(KafkaOperations<?, ?> kafkaOperations) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaOperations);
        ConsumerRecordRecoverer recoverer = (r, e) -> {
            logger.info("Sending dead letter message: {}", r.value());
            deadLetterPublishingRecoverer.accept(r, e);
        };
        return new DefaultAfterRollbackProcessor<>(recoverer, new FixedBackOff(0L, 2), kafkaOperations, true);
    }

    @SuppressWarnings("unchecked")
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<?, ?> consumerFactory,
            AfterRollbackProcessor<?, ?> afterRollbackProcessor,
            ChainedKafkaTransactionManager<?, ?> chainedKafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, (ConsumerFactory<Object, Object>) consumerFactory);
        /* transaction id defaults '<transactionIdPrefix>.<group.id>.<topic>.<partition>' */
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager); /* transaction support */
        factory.setMessageConverter(new JsonMessageConverter()); /* json support */
        factory.setAfterRollbackProcessor((AfterRollbackProcessor<Object, Object>) afterRollbackProcessor);
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

    @Bean
    public NewTopic testKafkaTopicDLT(Config config) {
        return TopicBuilder.name(config.getKafkaTestTopic() + ".DLT").partitions(4).replicas(1).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaTransactionApplication.class, args);
    }

}
