# 日志类型分析
## 警告(warn)
### broker
#### client
* NETTY EVENT: remove channel[{}][{}] from ProducerManager groupChannelTable, producer group: {}
    - 源：broker.client.ProducerManager
    - 方法：doChannelCloseEvent(final String remoteAddr,final Channel channel)
    - 原因：关闭了指定的通道
* ProducerManager doChannelCloseEvent lock timeout
    - 源：broker.client.ProducerManager
    - 方法：doChannelCloseEvent(final String remoteAddr,final Channel channel)
    - 原因：等待获取消费组信息操作的锁的时间过长
* ProducerManager registerProducer lock timeout
    - 源 broker.client.ProducerManager
    - 方法：registerProducer(final String group,final ClientChannelInfo clientChannelInfo)
    - 原因 注册消费者时等待锁的时间超时。
* ProducerManager unregisterProducer lock timeout
    - 源 broker.client.ProducerManager
    - 方法 unregisterProducer(final String group, final ClientChannelInfo clientChannelInfo)
    - 原因 注销消费者时等待获取锁的时间过长