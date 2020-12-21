package com.bohzen.rabbitSender;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
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
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String message = args.length < 1 ? "log: Hello World!" :
                    String.join(" ", args);
            for(int i=0;i<5;i++) {
                channel.basicPublish(EXCHANGE_NAME, rout[new Random().nextInt(2)], null, (message+" - "+i).getBytes());
                System.out.println("[X] Sent '" + message + "'");
            }
        }
    }
}
