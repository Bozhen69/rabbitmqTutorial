package com.bohzen.rabbitSender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Pavel Bozhenko <bozhenko@rekfost.ru>
 */
public class Main {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        String message = String.join(" ", args);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            for(int i=0;i<5;i++) {
                channel.basicPublish("", QUEUE_NAME, null, (message+" - "+i).getBytes());
                System.out.println("[X] Sent '" + message + "'");
            }
        }
    }

}
