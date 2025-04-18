package com.example.mqdemo.prefetch;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

public class Producer {
    private final static String QUEUE_NAME = "prefetchqueue";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "hello world";
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            channel.basicPublish("", QUEUE_NAME, null, (message + i).getBytes());
        }
        System.out.println("消息发送完毕");
        channel.close();
    }
}