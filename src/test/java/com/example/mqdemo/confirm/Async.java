package com.example.mqdemo.confirm;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class Async {
    public static void main(String[] args) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();

            ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

            ConfirmCallback ackCallback = (sequenceNumber, multiple) -> {
                if (multiple) {
                    //返回的是小于等于当前序列号的未确认消息 是一个 map
                    ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(sequenceNumber, true);
                    //清除该部分未确认消息
                    confirmed.clear();
                } else {
                    //只清除当前序列号的消息
                    outstandingConfirms.remove(sequenceNumber);
                }
            };
//            ConfirmCallback nackCallback = (sequenceNumber, multiple) ->
//            {
//                String message = outstandingConfirms.get(sequenceNumber);
//                System.out.println("发布的消息" + message + "未被确认，序列号" + sequenceNumber);
//            };

            channel.addConfirmListener(ackCallback, null);
            long begin = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                String message = "消息" + i;
                //channel.getNextPublishSeqNo()获取下一个消息的序列号，通过序列号与消息体进行一个关联
                outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish("", queueName, null, message.getBytes());

            }
            long end = System.currentTimeMillis();
            System.out.println("发布100个异步确认消息,耗时" + (end - begin) + "ms");
        }

    }
}
