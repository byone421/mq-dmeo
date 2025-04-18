package com.example.mqdemo.durable;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

/**
 * 默认情况下 RabbitMQ 退出或由于某种原因崩溃时，它忽视队列和消息。
 * 确保消息不会丢失需要做两件事：我们需要将队列和消息都标记为持久化。
 */
public class Producer {
    private final static String QUEUE_NAME = "durablequeue";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();


        //需要在声明队列的时候把 durable 参数设置为持久化，避免重启队列被删除
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        String message = "hello world";

        //设置消息持久化
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println("消息发送完毕");
        channel.close();
    }
}