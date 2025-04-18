# 前言
这是一个RabbitMQ学习笔记总结

# 简介
RabbitMQ 是使用 Erlang 编写的一个开源的消息队列，它实现了AMQP协议(<font style="color:rgb(0,0,0);">高级消息队列协议</font>)，是主流的消息队列之一。它支持多种客户端如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP 等



## 消息队列的优缺点
## 优点：
**<font style="color:rgb(0,0,0);">流量消峰：</font>**<font style="color:rgb(0,0,0);">比如订单系统最大能处理10000个请求。超过了就处理不了了。我们可以使用消息队列作为缓冲，立马给用户返回结果。让用户稍后确认是否下单成功。</font>

**<font style="color:rgb(0,0,0);">应用解耦：</font>**比如A系统要依赖与B和C系统。当B，C有一个异常了，请求就异常了。我们将请求放在消息队列中，使得应用之间解耦，减少错误率

**<font style="color:rgb(0,0,0);">异步处理：</font>**比如系统在做完某些操作后要发短信，发邮件等。我们可以这些任务放入消息队列，降低请求响应时间。

比如A 和B系统可以利用消息队列进行异步通信。

## 缺点：
系统里面引入了中间件。增加了系统的复杂性。

对于rabbitmq而言Erlang 编写的。想要自定义的难度比较大。

# 架构模型
![](https://cdn.nlark.com/yuque/0/2025/png/12600036/1744686172011-4251e85f-2c1e-4813-a9e4-12af4f3c8f28.png)



## Message(消息)
+ **消息体（Payload）**  
  消息的实际数据内容，可以是任意格式的二进制数据（如 JSON、XML、纯文本等）。RabbitMQ 不解析消息体的内容，仅负责传输。
+ **元数据（属性）**  
  附加的元数据信息，用于控制消息的行为和传递逻辑。这些属性由 AMQP 协议定义，部分属性由 RabbitMQ 扩展。

![](https://cdn.nlark.com/yuque/0/2025/png/12600036/1744685689767-cea920c4-a81d-4a88-8d1f-053f6ae9b72c.png)

## Producer(生产者)和Consumer(消费者)
消息发送者 和消息的接受者

## Queue(消息队列)
它是消息的容器，用来保存消息等待消费者来进行消费。多个消费者可以订阅同一个队列，这时队列中的消息会被平均分摊给多个消费者进行处理，而不是每个消费者都收到所有的消息并处理，这样避免消息被重复消费。

## Exchange(交换机)
消息发送时一般会指定一个Routing Key。在RabbitMQ中消息不会直接投送到Queue中，消息需要由Exchange进行转发。

由Exchange把消息转发给对应的Queue中，Exchange通过Binding Key和某个消息队列关联在一起。

结合Routing Key 和 Binding Key才能知道具体可以转发到哪些队列中。

![](https://cdn.nlark.com/yuque/0/2025/png/12600036/1744688945297-fafcf543-cb4d-4f41-88f0-9213e22cba36.png)



而Exchange常见的类型有4种，不同类型的 Exchange 转发消息的策略有所区别。

### 扇型交换器(fanout)
fanout 类型的 Exchange 路由规则非常简单，它会把所有发送到该 Exchange 的消息路由到所有与它绑定的 Queue 中，不做任何判断，所以是所有类型里面速度最快的，常用来广播消息。

### 直连交换器（Direct）
direct 类型的 Exchange 路由规则也很简单，它会把消息路由到那些 Bindingkey 与 RoutingKey 完全匹配的 Queue 中

![](https://cdn.nlark.com/yuque/0/2025/jpeg/12600036/1744689084675-0a618c63-8986-4b68-b13f-e63e5c1a22bd.jpeg)

### 主题交换器（Topic）
topic 类型的交换器与 direct 类型的交换器相似，但在匹配规则上做了扩展。它约定：

+ RoutingKey 为一个点号“．”分隔的字符串（被点号“．”分隔开的每一段独立的字符串称为一个单词），如 “com.rabbitmq.client”、“java.util.concurrent”、“com.hidden.client”;
+ BindingKey 和 RoutingKey 一样也是点号“．”分隔的字符串；
+ BindingKey 中可以存在两种特殊字符串“*”和“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词(可以是零个)。



![](https://cdn.nlark.com/yuque/0/2025/jpeg/12600036/1744689099773-b35caf09-5e66-48ed-a734-8538b5f8bad5.jpeg)



+ 路由键为 “com.rabbitmq.client” 的消息会同时路由到 Queue1 和 Queue2;
+ 路由键为 “com.hidden.client” 的消息只会路由到 Queue2 中；
+ 路由键为 “com.hidden.demo” 的消息只会路由到 Queue2 中；
+ 路由键为 “java.rabbitmq.demo” 的消息只会路由到 Queue1 中；
+ 路由键为 “java.util.concurrent” 的消息将会被丢弃或者返回给生产者（需要设置 mandatory 参数），因为它没有匹配任何路由键。

### 头交换器（Headers）不推荐
headers 类型的交换器不依赖于路由键的匹配规则来路由消息，而是根据发送的消息内容中的 headers 属性进行匹配。在绑定队列和交换器时指定一组键值对，当发送消息到交换器时，RabbitMQ 会获取到该消息的 headers（也是一个键值对的形式)，对比其中的键值对是否完全匹配队列和交换器绑定时指定的键值对，如果完全匹配则消息会路由到该队列，否则不会路由到该队列。headers 类型的交换器性能会很差，而且也不实用，基本上不会看到它的存在。

## <font style="color:rgb(0,0,0);">Broker</font>
<font style="color:rgb(0,0,0);">接收和分发消息的应用，RabbitMQ Server 就是 Message Broker</font>

## <font style="color:rgb(0,0,0);">Virtual host</font>
<font style="color:rgb(0,0,0);">类似于 namespace 的概念。当多个不同的用户使用同一个 RabbitMQ server 提供的服务时，可以划分出 多个 vhost，每个用户在自己的 vhost 创建各自的 exchange／queue 等。默认的vhost为“/” </font>

![](https://cdn.nlark.com/yuque/0/2025/png/12600036/1744689640423-b64ff527-2a0a-4b91-86ab-299d46eea28d.png)

## <font style="color:rgb(0,0,0);">Connection</font>
<font style="color:rgb(0,0,0);">publisher／consumer 和 broker 之间的 TCP 连接</font>

## <font style="color:rgb(0,0,0);">Channel</font>
<font style="color:rgb(0,0,0);">Channel </font><font style="color:rgb(0,0,0);">是在 </font><font style="color:rgb(0,0,0);">connection </font><font style="color:rgb(0,0,0);">内部建立的逻辑连接，如果应用程 </font>

<font style="color:rgb(0,0,0);">序支持多线程，通常每个 thread 创建单独的 channel 进行通讯，AMQP method 包含了 channel id 帮助客 户端和 message broker 识别 channel，所以 channel 之间是完全隔离的。</font>

Channel 作为轻量级的 Connection 极大减少了操作系统建立 TCP connection 的开销

## <font style="color:rgb(0,0,0);">Binding</font>
<font style="color:rgb(0,0,0);">exchange 和 queue 之间的虚拟连接，binding 中可以包含 routing key，Binding 信息被保 </font>

<font style="color:rgb(0,0,0);">存到 exchange 中的查询表中，用于 message 的分发依据</font>



# 代码演示
## hello
通过代码简单演示

## ack
通过代码演示手动ack

## durable
通过代码演示队列和消息的持久化

## multiconsumer
通过代码演示多消费者

## prefetch
演示不公平分发

channel.basicQos(1)

## confirm
演示发送端确认

channel.confirmSelect();

## fanout
演示fanout交换机

**<font style="color:rgb(0,0,0);">direct</font>**

演示Direct交换机

## <font style="color:rgb(0,0,0);">topic</font>
演示topic交换机

# 死信队列
死信，顾名思义就是无法被消费的消息，字面意思可以这样理 解，一般来说，producer 将消息投递到 broker 或者直接到queue 里了，consumer 从 queue 取出消息 进行消费，但某些时候由于特定的原因导致 queue 中的某些消息无法被消费，这样的消息如果没有后续的处理，就变成了死信，有死信自然就有了死信队列



**<font style="color:rgb(0,0,0);">应用场景: </font>**<font style="color:rgb(0,0,0);">为了保证订单业务的消息数据不丢失，需要使用到 RabbitMQ 的死信队列机制，</font>

<font style="color:rgb(0,0,0);">当消息消费发生异常时，将消息投入死信队列中.</font>

<font style="color:rgb(0,0,0);">还有比如说: 用户在商城下单成功并点击去支付后在指定时 间未支付时自动失效</font>

## <font style="color:rgb(0,0,0);">异常情况</font>
<font style="color:rgb(0,0,0);">消息 </font><font style="color:rgb(0,0,0);">TTL </font><font style="color:rgb(0,0,0);">过期 </font>

<font style="color:rgb(0,0,0);">队列达到最大长度</font><font style="color:rgb(0,0,0);">(</font><font style="color:rgb(0,0,0);">队列满了，无法再添加数据到 </font><font style="color:rgb(0,0,0);">mq </font><font style="color:rgb(0,0,0);">中</font><font style="color:rgb(0,0,0);">) </font>

<font style="color:rgb(0,0,0);">消息被拒绝(basic.reject 或 basic.nack)并且 requeue=false.</font>

## <font style="color:rgb(0,0,0);">代码架构图</font>
![](https://cdn.nlark.com/yuque/0/2025/png/12600036/1744706872509-66fbd5e2-3d86-42fe-b65e-38f22302bf37.png)



# 队列消息优先级
<font style="color:rgb(0,0,0);">注意：队列需要设置为优先级队列，消息需要设置消息的优先级，消费者需要等待消息已经发送到队列中才去消费因为，这样才有机会对消息进行排序</font>

## <font style="color:rgb(0,0,0);">代码</font>
演示优先级

priority





