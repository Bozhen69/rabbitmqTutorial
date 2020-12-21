package com.bohzen.rabbitClient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author Pavel Bozhenko <bozhenko@rekfost.ru>
 */
public class Main {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DeliverCallback callback = (consumerTag, message) -> {
            String text = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + text + "'");
            try {
                doWork(text);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(" [x] done ");
            }
        };
        channel.basicConsume(QUEUE_NAME, true, callback, consumerTag -> {
        });
    }

    private static void doWork(String task) throws InterruptedException {
        for (char c : task.toCharArray()) {
            if (c == '.') Thread.sleep(1000);
        }
    }
}
