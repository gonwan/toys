package com.gonwan.kafka.tx;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.util.backoff.FixedBackOff;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class KafkaTransactionApplication {

    private static Logger logger = LoggerFactory.getLogger(KafkaTransactionApplication.class);

    /* add json support for KafkaTemplate & @KafkaListener. */
    @Bean
    public ObjectMapper kafkaObjectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addHandler(new DeserializationProblemHandler() {
                    /* to work with @JsonFormat */
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
                        logger.warn("Failed to parse {}={}: {}", ctxt.getParser().getCurrentName(), valueToConvert, failureMsg);
                        return null;
                    }
                })
                .build();
    }

    /* single & batch, non-tx. */
    public CommonErrorHandler commonErrorHandler() {
        return new CommonLoggingErrorHandler();
    }

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
            logger.error("Sending dead letter message: {}", r.value());
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
        factory.getContainerProperties().setEosMode(ContainerProperties.EOSMode.V1); /* keep compatible with brokers < 2.5 */
        factory.setMessageConverter(new JsonMessageConverter(kafkaObjectMapper())); /* json support */
        factory.setAfterRollbackProcessor((AfterRollbackProcessor<Object, Object>) afterRollbackProcessor);
        return factory;
    }

    /* Used in consumer-initiated transactions */
    @Primary
    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<?, ?> producerFactory) {
        KafkaTemplate<?, ?> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setMessageConverter(new StringJsonMessageConverter(kafkaObjectMapper())); /* json support */
        return kafkaTemplate;
    }

    /* Used in consumer-initiated transactions */
    @Primary
    @Bean
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    /*
     * Standalone KafkaTemplate used in producer-initiated transactions: executeInTransaction().
     * so that zombie fencing is handled properly when partitions move from one instance to another after a rebalance.
     * See: https://docs.spring.io/spring-kafka/docs/2.8.0/reference/html/#transaction-id-prefix
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
        return TopicBuilder.name(config.getKafkaTestTopic())
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic testKafkaTopicDLT(Config config) {
        return TopicBuilder.name(config.getKafkaTestTopic() + ".DLT")
                .partitions(4)
                .replicas(1)
                .build();
    }

    /* a compact topic is just about compact clean policy, which saves storage. */
    //@Bean
    public NewTopic testKafkaCompactTopic(Config config) {
        return TopicBuilder.name(config.getKafkaTestCompactTopic())
                .partitions(4)
                .replicas(1)
                .compact()
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaTransactionApplication.class, args);
    }

}
