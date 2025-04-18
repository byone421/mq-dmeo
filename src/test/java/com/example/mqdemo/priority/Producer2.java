package com.example.mqdemo.priority;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class Producer2 {
    private static final String QUEUE_NAME="hello";
    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
        //该信息是用作演示队列个数限制
        for (int i = 1; i < 11; i++) {
            Thread.sleep(3000);
            String message = "Producer2:" + "info" + i;
//            if(i==5){
                channel.basicPublish("", QUEUE_NAME, properties, ("a" + message).getBytes());
                channel.basicPublish("", QUEUE_NAME, properties, ("b" + message).getBytes());
                channel.basicPublish("", QUEUE_NAME, properties, ("c" + message).getBytes());
                channel.basicPublish("", QUEUE_NAME, properties, ("d" + message).getBytes());
                channel.basicPublish("", QUEUE_NAME, properties, ("e" + message).getBytes());

//            }else {
//                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//            }
            System.out.println("生产者发送消息:" + message);
        }
    }

}
