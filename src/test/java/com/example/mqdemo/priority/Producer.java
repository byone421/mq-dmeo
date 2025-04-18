package com.example.mqdemo.priority;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class Producer {
    private static final String QUEUE_NAME="hello";
    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
        //该信息是用作演示队列个数限制
        for (int i = 1; i < 101; i++) {
            Thread.sleep(1000);
            String message = "info" + i;
//            if(i==5){
//                channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
//            }else {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//            }
            System.out.println("生产者发送消息:" + message);
        }
    }

}
