package com.gonwan.amqp.basic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.impl.NetworkConnection;

public class Receiver implements AutoCloseable {

    private static final byte[] LINE_SEPARATOR = System.lineSeparator().getBytes();

    private StatTask statTask;
    private Parameters params;
    private Channel channel;
    private String queue;

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
        @Parameter(order = 6, names = { "-r", "--routing-key"}, description = "routing key")
        private String routingKey = "";

        @Parameter(order = 7, names = { "-f", "--file" }, description = "file to write received messages")
        private String file = "";

        @Parameter(order = 8, names = { "-v", "--verbose" }, description = "whether to print received messages")
        private boolean verbose = false;

        private void check() {
            if ((StringUtils.isEmpty(queue) && StringUtils.isEmpty(exchange))
                    || (StringUtils.isNotEmpty(queue) && StringUtils.isNotEmpty(exchange))) {
                throw new ParameterException("one of -q or -e required.");
            }
            if (StringUtils.isNotEmpty(queue)) {
                type = "direct";
            }
        }

    }

    public Receiver(Parameters params) throws IOException, TimeoutException {
        this.params = params;
        this.statTask = new StatTask(false);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(params.host);
        factory.setPort(params.port);
        factory.setConnectionTimeout(3000);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        /* declare a named queue */
        boolean exclusive = true;
        if (StringUtils.isEmpty(params.queue)) {
            /* add possibility to specify a different queue name */
            queue = params.exchange + ".receiver" + (StringUtils.isEmpty(params.routingKey) ? "" : "_" + params.routingKey);
        } else {
            queue = params.queue;
            exclusive = false;
        }
        /* NOTE: messages are load balanced between consumers, not between queues. */
        /* NOTE: every queue created is automatically bound to the default (direct) exchange if no further bind operations. */
        /* NOTE: exclusive implies autodelete. */
        /* NOTE: exclusive also implies no ha support in different nodes. */
        channel.queueDeclare(queue, false, exclusive, false, null);
        /* declare the exchange and bindings if fanout or topic type used. */
        if (StringUtils.isEmpty(params.queue)) {
            /* do not declare an exchange for a receiver */
            /* NOTE: routing keys works for queues bound to a topic exchange, and ignored for ones bound to a fanout exchange. */
            channel.queueBind(queue, params.exchange, params.routingKey);
        }
    }

    public void receive() throws IOException {
        ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
        svc.scheduleAtFixedRate(statTask, 15, 15, TimeUnit.SECONDS);
        /* hack here: add host and port to the consumer tag */
        NetworkConnection networkConnection = ((NetworkConnection) channel.getConnection());
        String consumerTag = String.format("receiver@%s:%d",
                networkConnection.getLocalAddress().getHostAddress(), networkConnection.getLocalPort());
        FileOutputStream fos = StringUtils.isEmpty(params.file) ? null : new FileOutputStream(params.file);
        /* auto-ack is false */
        channel.basicConsume(queue, false, consumerTag, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                    Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                long deliveryTag = envelope.getDeliveryTag(); /* a per-channel counter */
                if (params.verbose) {
                    System.out.println(String.format("[%s:%d]: %s", routingKey, deliveryTag, new String(body, StandardCharsets.UTF_8)));
                }
                if (fos != null) {
                    fos.write(body);
                    fos.write(LINE_SEPARATOR);
                }
                statTask.addMessages(1);
                channel.basicAck(deliveryTag, false);
            }
        });
    }

    @Override
    public void close() {
        if (channel != null) {
            try {
                channel.getConnection().close();
            } catch (IOException e) {
                /* ignore */
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Message receiver for RabbitMQ ===");
        Receiver.Parameters params = new Receiver.Parameters();
        JCommander jcommander = JCommander.newBuilder().addObject(params).build();
        jcommander.setProgramName(Receiver.class.getSimpleName().toLowerCase());
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
        try (Receiver receiver = new Receiver(params)) {
            receiver.receive();
            /* do not close receiver */
            while (true) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                }
            }
        } catch (IOException | TimeoutException e) {
            System.err.println(ExceptionUtils.getStackTrace(e));
            System.exit(-1);
        }
    }

}

