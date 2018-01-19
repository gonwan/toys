package com.gonwan.amqp.basic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatTask implements Runnable {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private boolean sender;
    private volatile int messages;

    public StatTask(boolean sender) {
        this.sender = sender;
    }

    public void addMessages(int c) {
        messages += c;
    }

    @Override
    public void run() {
        System.out.println(String.format("[%s] %d message %s.",
                formatter.format(LocalDateTime.now()), messages, sender ? "sent" : "received"));
    }

}

