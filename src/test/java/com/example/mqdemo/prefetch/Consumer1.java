package com.example.mqdemo.prefetch;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.TimeUnit;


public class Consumer1 {
    private final static String QUEUE_NAME = "prefetchqueue";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println("Consumer1等待接收消息.........");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println(message);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);

        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断");
         };




        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);


    }
}
