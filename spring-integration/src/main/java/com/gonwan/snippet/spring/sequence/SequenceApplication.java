package com.gonwan.snippet.spring.sequence;

import com.gonwan.snippet.spring.msgbus.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.endpoint.MethodInvokingMessageSource;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.handler.LoggingHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO:
 * 1. reliable messaging
 * 2. retry advice
 * 3. message store
 */
@SpringBootApplication
public class SequenceApplication {

    private static final Logger logger = LoggerFactory.getLogger(SequenceApplication.class);

    @Bean
    public MessageSource<?> integerMessageSource() {
        MethodInvokingMessageSource source = new MethodInvokingMessageSource();
        source.setObject(new AtomicInteger());
        source.setMethodName("incrementAndGet");
        return source;
    }

    /* See: https://github.com/spring-projects/spring-integration-java-dsl/wiki/spring-integration-java-dsl-reference */
    @Bean
    public IntegrationFlow integrationFlow() {
        return IntegrationFlows.from(integerMessageSource(), c -> c.poller(Pollers.fixedRate(100)))
                .enrichHeaders(e -> e.header(IntegrationMessageHeaderAccessor.CORRELATION_ID, "test"))
                .aggregate(a -> a.releaseStrategy(g -> g.size() >= 10).expireGroupsUponCompletion(true).groupTimeout(1200).sendPartialResultOnExpiry(true)) /* add group */
                .split() /* add sequence id and sequence size */
                .channel(MessageChannels.executor(Executors.newFixedThreadPool(5))) /* parallel processing */
                .handle((GenericHandler<Integer>) (p, h) -> { /* do not throw inside */
                        try {
                            int s = 1 + (int) (Math.random() * 5000);
                            Thread.sleep(s);
                            logger.info("Processed: payload={}", p);
                        } catch (InterruptedException e) {
                            /* ignore */
                        }
                        return p;
                    }
                )
                .resequence() /* sort by sequence id in group */
                .log(LoggingHandler.Level.INFO, m -> String.format("Got message: ts=%d seq=%04d payload=%04d",
                        m.getHeaders().getTimestamp(), m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER), m.getPayload()
                ))
                .get();
    }

    public static void main(String[] args) {
        SpringApplication.run(SequenceApplication.class, args);
    }

}
