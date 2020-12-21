package com.bohzen.rabbitClient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * @author Pavel Bozhenko <bozhenko@rekfost.ru>
 */
public class Main {
    private static final String EXCHANGE_NAME = "logs2";
    private static final String[] rout = new String[]{"crit","normal"};

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String routKey = rout[new Random().nextInt(2)];
        System.out.println("rout key is - " + routKey);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,routKey);
        DeliverCallback callback = (consumerTag, message) -> {
            String text = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + text + "'");
        };
        channel.basicConsume(queueName, true, callback, consumerTag -> {
        });
    }
}
