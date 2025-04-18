package com.example.mqdemo.hello;

import com.rabbitmq.client.*;

public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
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
        System.out.println("等待接收消息.........");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println(message);
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断");
         };



         //消费哪个队列
         //消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         //消费者未成功消费的回调
         //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
