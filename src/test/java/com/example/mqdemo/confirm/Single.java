package com.example.mqdemo.confirm;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.UUID;

public class Single {

    public static void main(String[] args) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();
            long begin = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                String message = i + "";
                channel.basicPublish("", queueName, null, message.getBytes());
                //服务端返回 false表示没有发送成功
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("消息发送成功");
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("发布100个单独确认消息,耗时" + (end - begin) + "ms");
        }
    }

}
