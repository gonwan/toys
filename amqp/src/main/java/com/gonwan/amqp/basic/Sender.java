package com.gonwan.amqp.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    private StatTask statTask;
    private Parameters params;
    private Channel channel;
    private String exchange;
    private String routingKey;

    private static class Parameters {

        @Parameter(order = 1, names = { "-h", "--host" }, description = "host of broker")
        private String host = "127.0.0.1";

        @Parameter(order = 2, names = { "-p", "--port" }, description = "port of broker")
        private int port = 5672;

        /* shortcut for sending/receiving messages from/to the default direct exchange with empty name */
        /* -q and -e are exclusive */
        @Parameter(order = 3, names = { "-q", "--queue" }, description = "queue name")
        private String queue = "";

        /* -q and -e are exclusive */
        @Parameter(order = 4, names = { "-e", "--exchange" }, description = "exchange name")
        private String exchange = "";

        @Parameter(order = 5, names = { "-t", "--type" }, description = "exchange type")
        private String type = "fanout";

        /* direct: empty or same as queue name??, fanout: no use, topic: routing key. */
        @Parameter(order = 6, names = { "-r", "--routing-key " }, description = "routing key")
        private String routingKey = "";

        /* -m and -f are exclusive */
        @Parameter(order = 7, names = { "-m", "--message" }, description = "message to send")
        private String message = "";

        /* -m and -f are exclusive */
        @Parameter(order = 8, names = { "-f", "--file" }, description = "file to read messages to send")
        private String file = "";

        @Parameter(order = 9, names = { "-i", "--interval" }, description = "send interval: in millisecond")
        private int interval = 1000;

        @Parameter(order = 10, names = { "-b", "--batch" }, description = "send mode: line by line or as a whole")
        private boolean batch = false;

        private void check() {
            if ((StringUtils.isEmpty(queue) && StringUtils.isEmpty(exchange))
                    || (StringUtils.isNotEmpty(queue) && StringUtils.isNotEmpty(exchange))) {
                throw new ParameterException("one of -q or -e required.");
            }
            if ((StringUtils.isEmpty(message) && StringUtils.isEmpty(file))
                    || (StringUtils.isNotEmpty(message) && StringUtils.isNotEmpty(file))) {
                throw new ParameterException("one of -m or -f required.");
            }
            if (StringUtils.isNotEmpty(queue)) {
                type = "direct";
            }
            if (interval <= 0) {
                interval = 1000;
            }
        }

    }

    public Sender(Parameters params) throws IOException, TimeoutException {
        this.params = params;
        this.statTask = new StatTask(true);
        new Timer().scheduleAtFixedRate(this.statTask, 15000, 15000);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(params.host);
        factory.setPort(params.port);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        if (StringUtils.isEmpty(params.queue)) {
            channel.exchangeDeclare(params.exchange, params.type, false, false, null);
            exchange = params.exchange;
            routingKey = params.routingKey;
        } else {
            /* declare a named queue that bind to the default (direct) exchange */
            channel.queueDeclare(params.queue, false, false, false, null);
            exchange = "";
            routingKey = params.queue;
        }
    }

    public void send() throws IOException {
        if (StringUtils.isEmpty(params.file)) {
            send(params.message);
        } else {
            File f = new File(params.file);
            if (!params.batch) {
                if (f.length() > 1024*1024) {
                    throw new IOException("file to large to send as a whole");
                }
                byte[] body = Files.readAllBytes(f.toPath());
                send(body);
            } else {
                /* iso-8859-1 is good for bytes */
                InputStreamReader isr = new InputStreamReader(new FileInputStream(f), StandardCharsets.ISO_8859_1);
                try (BufferedReader br = new BufferedReader(isr)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        send(line.getBytes(StandardCharsets.ISO_8859_1));
                        statTask.addMessage(1);
                        try {
                            Thread.sleep(params.interval);
                        } catch (InterruptedException e) {
                            /* ignore */
                        }
                    }
                }
            }
        }
    }

    private void send(byte[] body) throws IOException {
        channel.basicPublish(exchange, routingKey, false, null, body);
    }

    private void send(String body) throws IOException {
        channel.basicPublish(exchange, routingKey, false, null, body.getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        System.out.println("=== Message sender for RabbitMQ ===");
        Sender.Parameters params = new Sender.Parameters();
        JCommander jcommander = JCommander.newBuilder().addObject(params).build();
        jcommander.setProgramName(Sender.class.getSimpleName().toLowerCase());
        /* help */
        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();
            jcommander.usage(sb);
            System.out.println(sb.toString());
            System.exit(0);
        }
        /* parse */
        try {
            jcommander.parse(args);
            params.check();
        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
        /* run */
        try {
            Sender sender = new Sender(params);
            sender.send();
        } catch (IOException | TimeoutException e) {
            logger.error("", e);
        }
    }

}
