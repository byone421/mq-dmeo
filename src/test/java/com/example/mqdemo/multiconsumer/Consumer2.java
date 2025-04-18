package com.example.mqdemo.multiconsumer;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;


/**
 * 演示 手动确认
 *
 */
public class Consumer2 {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("Consumer2等待接收消息.........");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println(message);
            //手动确认
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);

            //Channel.basicNack(用于否定确认)
            //Channel.basicReject(用于否定确认) 不处理该消息了直接拒绝，可以将其丢弃了
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断");
         };

         //消费哪个队列
         //消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         //消费者未成功消费的回调
         //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
