package com.bohzen.rabbitSender;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * @author Pavel Bozhenko <bozhenko@rekfost.ru>
 */
public class Main {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            for(int i=0;i<32;i++) {
                System.out.println(" [x] Requesting fib(" + i + ")");
                final String replyQueueName = channel.queueDeclare().getQueue();
                final String corrId = UUID.randomUUID().toString();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .replyTo(replyQueueName)
                        .correlationId(corrId)
                        .build();
                channel.basicPublish("", RPC_QUEUE_NAME, replyProps, String.valueOf(i).getBytes(StandardCharsets.UTF_8));
                final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
                String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                    if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                        response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
                    }
                }, consumerTag -> {
                });
                String result = response.take();
                channel.basicCancel(ctag);
                System.out.println(" [.] Got '" + result + "'");
            }
        }
    }
}
