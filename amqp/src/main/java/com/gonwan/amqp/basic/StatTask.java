package com.gonwan.amqp.basic;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(StatTask.class);
	private boolean sender;
	private volatile int messages;

	public StatTask(boolean sender) {
		this.sender = sender;
	}
	
	public void addMessage(int i) {
		messages += i;
	}

	@Override
	public void run() {
		logger.info("{} messages {}.", messages, sender ? "sent" : "received");
	}

}

