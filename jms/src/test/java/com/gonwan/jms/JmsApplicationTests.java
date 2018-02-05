package com.gonwan.jms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.jms.Destination;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Component
class JmsDirectListener {

    private Map<String, String> msg;
    private CountDownLatch latch = new CountDownLatch(1);

    public Map<String, String> getMessages() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            /* ignore */
        }
        return msg;
    }

    @JmsListener(destination = "#{config.qpidDestinationTestDirect}")
    public void handleDirectMessage1(Map<String, String> m) {
        msg = m;
        latch.countDown();
    }

}

@Component
class JmsFanoutListener {

    private Map<String, String> msg1;
    private Map<String, String> msg2;
    private CountDownLatch latch = new CountDownLatch(2);

    public List<Map<String, String>> getMessages() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            /* ignore */
        }
        return Arrays.asList(msg1, msg2);
    }

    @JmsListener(destination = "#{config.qpidDestinationTestFanoutConsumer1}")
    public void handleFanoutMessage1(Map<String, String> m) {
        msg1 = m;
        latch.countDown();
    }

    @JmsListener(destination = "#{config.qpidDestinationTestFanoutConsumer2}")
    public void handleFanoutMessage2(Map<String, String> m) {
        msg2 = m;
        latch.countDown();
    }

}

@Component
class JmsTopicListener {

    private Map<String, String> msg1;
    private Map<String, String> msg2;
    private CountDownLatch latch = new CountDownLatch(2);

    public List<Map<String, String>> getMessages() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            /* ignore */
        }
        return Arrays.asList(msg1, msg2);
    }

    @JmsListener(destination = "#{config.qpidDestinationTestTopicConsumer1}")
    public void handleTopicMessage1(@Headers MessageHeaders headers, Map<String, String> m) {
        msg1 = m;
        System.out.println("1: " + headers.toString());
        latch.countDown();
    }

    @JmsListener(destination = "#{config.qpidDestinationTestTopicConsumer2}")
    public void handleTopicMessage2(@Headers MessageHeaders headers, Map<String, String> m) {
        msg2 = m;
        System.out.println("2: " + headers.toString());
        latch.countDown();
    }

}

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JmsApplication.class)
public class JmsApplicationTests {

    @Autowired
    private JmsTemplate jmsTemplate;

    /* use destination object seems to be faster */
    @Autowired
    @Qualifier("testDirect")
    private Destination testDirect;
    @Autowired
    @Qualifier("testFanout")
    private Destination testFanout;
    @Autowired
    @Qualifier("testTopic1")
    private Destination testTopic1;
    @Autowired
    @Qualifier("testTopic2")
    private Destination testTopic2;

    @Autowired
    private JmsDirectListener jmsDirectListener;
    @Autowired
    private JmsFanoutListener jmsFanoutListener;
    @Autowired
    private JmsTopicListener jmsTopicListener;

    @Test
    public void testDirect() throws InterruptedException {
        Thread.sleep(1000);
        Map<String, String> m = new HashMap<>();
        m.put("directkey1", "directvalue1");
        m.put("directkey2", "directvalue2");
        jmsTemplate.convertAndSend(testDirect, m);
        Map<String, String> msg = jmsDirectListener.getMessages();
        assertThat(msg, equalTo(m));
    }

    @Test
    public void testFanout() throws InterruptedException {
        Thread.sleep(1000);
        Map<String, String> m = new HashMap<>();
        m.put("fanoutkey1", "fanoutvalue1");
        m.put("fanoutkey2", "fanoutvalue2");
        jmsTemplate.convertAndSend(testFanout, m);
        List<Map<String, String>> msgs = jmsFanoutListener.getMessages();
        assertThat(msgs.get(0), equalTo(m));
        assertThat(msgs.get(1), equalTo(m));
    }

    @Test
    public void testTopic() throws InterruptedException {
        Thread.sleep(1000);
        Map<String, String> m = new HashMap<>();
        m.put("topickey1", "topicvalue1");
        m.put("topickey2", "topicvalue2");
        jmsTemplate.convertAndSend(testTopic1, m);
        jmsTemplate.convertAndSend(testTopic2, m);
        List<Map<String, String>> msgs = jmsTopicListener.getMessages();
        assertThat(msgs.get(0), equalTo(m));
        assertThat(msgs.get(1), equalTo(m));
    }

}
