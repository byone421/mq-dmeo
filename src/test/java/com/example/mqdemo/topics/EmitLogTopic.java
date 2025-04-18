package com.example.mqdemo.topics;

import com.example.mqdemo.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EmitLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            Map<String, String> routerKeyMap = new HashMap<>();
            routerKeyMap.put("quick.orange.rabbit", "被队列 Q1Q2 接收到");
            routerKeyMap.put("lazy.orange.elephant", "被队列 Q1Q2 接收到");
            routerKeyMap.put("quick.orange.fox", "被队列 Q1 接收到");
            routerKeyMap.put("lazy.brown.fox", "被队列 Q2 接收到");
            routerKeyMap.put("lazy.pink.rabbit", "虽然满足两个绑定但只被队列 Q2 接收一次");
            routerKeyMap.put("quick.brown.fox", "不匹配任何绑定不会被任何队列接收到会被丢弃");
            routerKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定会被丢弃");
            routerKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配 Q2");
            for (Map.Entry<String, String> bindingKeyEntry : routerKeyMap.entrySet()) {
                String routerKey =
                        bindingKeyEntry.getKey();
                String message = bindingKeyEntry.getValue();
                channel.basicPublish(EXCHANGE_NAME, routerKey, null,
                        message.getBytes(StandardCharsets.UTF_8));
                System.out.println("生产者发出消息" + message);
            }
        }
    }
}
