package com.gonwan.jms;


import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class MyListener {
    @JmsListener(destination = "#{config.qpidDestinationTestFanoutConsumer1}")
    public void handleText(byte[] bytes) {
        System.out.println(new String(bytes, UTF_8));
    }

}
