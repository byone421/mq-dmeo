package com.example.mqdemo.utils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqUtils {

    //得到一个连接的 channel
    public static Channel getChannel() throws Exception {
    //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("43.138.7.209");
        factory.setPort(5672);
        //控制台添加
        factory.setVirtualHost("mqdemo");
        factory.setUsername("kx_credit");
        factory.setPassword("Kaixuan123~");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(2);

        return channel;

    }
}