package com.example.mqdemo.ack;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        // queue:队列名称
        // durable：是否持久化，消息存储在内存中
        // exclusive：队列是否共享，表示可以多个消费者消费
        // autoDelete：最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
        // arguments：其他参数
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "hello world";

        //exchange:发送到那个交换机
        //routingKey: 消息的路由key
        //props：消息属性
        //body：发送消息的消息体
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
        channel.close();
    }
}