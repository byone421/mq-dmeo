package com.example.mqdemo.dead.ttl;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

public class Producer {

    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
            //设置消息的 TTL 时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("1000").build();
            //该信息是用作演示队列个数限制
            for (int i = 1; i < 11; i++) {
                String message = "info" + i;
                channel.basicPublish(NORMAL_EXCHANGE, "normalkey", properties, message.getBytes());
                System.out.println("生产者发送消息:" + message);
            }
        }
    }

}
