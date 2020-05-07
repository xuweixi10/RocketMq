package broker.client;

import common.constant.LoggerName;
import common.consumer.ConsumeFromWhere;
import common.protocol.heartbeat.ConsumeType;
import common.protocol.heartbeat.MessageModel;
import common.protocol.heartbeat.SubscriptionData;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.common.RemotingHelper;
import remoting.common.RemotingUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/2 22:36
 */
public class ConsumerManager {
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    //通道超时过期
    private static final long CHANNEL_EXPIRED_TIMEOUT = 1000*120;
    //group-groupInformation
    private final ConcurrentHashMap<String,ConsumerGroupInfo> consumerTable=
            new ConcurrentHashMap<>(1024);
    private final ConsumerIdsChangeListener consumerIdsChangeListener;
    public ConsumerManager(ConsumerIdsChangeListener consumerIdsChangeListener){
        this.consumerIdsChangeListener=consumerIdsChangeListener;
    }

    /**
     * 获取某个组的用户的信道
     * @param group 组名
     * @param clientId 用户id
     * @return 信道
     */
    public ClientChannelInfo findChannl(final String group,final String clientId){
        ConsumerGroupInfo consumerGroupInfo=consumerTable.get(group);
        if(consumerGroupInfo!=null){
            return consumerGroupInfo.findChannel(clientId);
        }
        return null;
    }

    /**
     * 获取某个组的某个订阅节点
     * @param group 组名
     * @param topic 订阅节点名
     * @return 订阅节点的数据
     */
    public SubscriptionData findSubscriptionData(final String group,final String topic){
        ConsumerGroupInfo consumerGroupInfo=consumerTable.get(group);
        if(consumerGroupInfo!=null){
            return consumerGroupInfo.findSubscriptionData(topic);
        }
        return null;
    }

    public ConsumerGroupInfo getConsumerGroupInfo(final String group) {
        return this.consumerTable.get(group);
    }
    public int findSubscriptionDataCount(String group) {
        ConsumerGroupInfo consumerGroupInfo = consumerTable.get(group);
        if (consumerGroupInfo != null) {
            return consumerGroupInfo.getSubscriptionTable().size();
        }
        return 0;
    }

    public void doChannelCloseEvent(final String remoteAddr, final Channel channel){
        Iterator<Entry<String,ConsumerGroupInfo>> it=consumerTable.entrySet().iterator();
        while (it.hasNext()){
            Entry<String,ConsumerGroupInfo> next=it.next();
            ConsumerGroupInfo consumerGroupInfo=next.getValue();
            boolean removed=consumerGroupInfo.doChannelCloseEvent(channel,remoteAddr);
            if(removed){
                //整个组里已经没有占用的信道
                if(consumerGroupInfo.getChannelInfoTable().isEmpty()){
                    ConsumerGroupInfo remove=this.consumerTable.remove(consumerGroupInfo);
                    if (remove != null) {
                        log.info("unregister consumer ok, no any connection, and remove consumer group, {}",
                                next.getKey());
                    }
                }
                this.consumerIdsChangeListener.consumerIdsChanged(next.getKey(),consumerGroupInfo.getAllChannel());
            }
        }
    }

    /**
     * 注册消费者
     * @param group 消费者所属于的组
     * @param clientChannelInfo 消费者使用的信道信息
     * @param consumeType 消费类型
     * @param messageModel 消息模式
     * @param consumeFromWhere 消费位置
     * @param subList 订阅节点表
     * @param isNotifyConsumerIdsChangedEnable 是否发送通知
     * @return 注册是否成功
     */
    public boolean registerConsumer(final String group, final ClientChannelInfo clientChannelInfo,
                                    ConsumeType consumeType, MessageModel messageModel, ConsumeFromWhere consumeFromWhere,
                                    final Set<SubscriptionData> subList,boolean isNotifyConsumerIdsChangedEnable) {
        ConsumerGroupInfo consumerGroupInfo = this.consumerTable.get(group);
        if (consumerGroupInfo == null) {
            ConsumerGroupInfo tmp = new ConsumerGroupInfo(group, consumeType, messageModel, consumeFromWhere);
            ConsumerGroupInfo prev = this.consumerTable.putIfAbsent(group, tmp);
            consumerGroupInfo = prev != null ? prev : tmp;
        }


        //更新数据，如果发生改变且启用监听器则发送消息
        boolean r1 =
                consumerGroupInfo.updateChannel(clientChannelInfo, consumeType, messageModel, consumeFromWhere);
        boolean r2 = consumerGroupInfo.updateSubscription(subList);
        if (r1 || r2) {
            if (isNotifyConsumerIdsChangedEnable) {
                this.consumerIdsChangeListener.consumerIdsChanged(group, consumerGroupInfo.getAllChannel());
            }
        }
        return r1 || r2;
    }
    public void unregisterConsumer(final String group,ClientChannelInfo clientChannelInfo,
                                      boolean isNotifyConsumerIdsChangedEnable){
        ConsumerGroupInfo consumerGroupInfo=this.consumerTable.get(group);
        if(null!=group){
            consumerGroupInfo.unregisterChannel(clientChannelInfo);
            //该组没有用户了，移除该组
            if(consumerGroupInfo.getChannelInfoTable().isEmpty()){
                ConsumerGroupInfo remove=this.consumerTable.remove(group);
                if (remove != null) {
                    log.info("unregister consumer ok, no any connection, and remove consumer group, {}", group);
                }
            }
            if(isNotifyConsumerIdsChangedEnable){
                this.consumerIdsChangeListener.consumerIdsChanged(group,consumerGroupInfo.getAllChannel());
            }
        }
    }
    public void scanNotActiveChannel() {
        Iterator<Entry<String, ConsumerGroupInfo>> it = this.consumerTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, ConsumerGroupInfo> next = it.next();
            String group = next.getKey();
            ConsumerGroupInfo consumerGroupInfo = next.getValue();
            //获取每个组管理的通道表
            ConcurrentHashMap<Channel, ClientChannelInfo> channelInfoTable = consumerGroupInfo.getChannelInfoTable();
            Iterator<Entry<Channel, ClientChannelInfo>> itChannel = channelInfoTable.entrySet().iterator();
            //处理每个信道
            while (itChannel.hasNext()) {
                Entry<Channel, ClientChannelInfo> nextChannel = itChannel.next();
                ClientChannelInfo clientChannelInfo = nextChannel.getValue();
                long diff = System.currentTimeMillis() - clientChannelInfo.getLastUpdateTimestamp();
                if (diff > CHANNEL_EXPIRED_TIMEOUT) {
                    log.warn("SCAN: remove expired channel from ConsumerManager consumerTable. channel={}, consumerGroup={}" +
                            RemotingHelper.parseChannelRemoteAddr(clientChannelInfo.getChannel()), group);
                    RemotingUtil.closeChannel(clientChannelInfo.getChannel());
                    itChannel.remove();
                }
            }
            //该组管理的所有通道都过期了
            if (channelInfoTable.isEmpty()) {
                log.warn(
                        "SCAN: remove expired channel from ConsumerManager consumerTable, all clear, consumerGroup={}",
                        group);
                it.remove();
            }
        }
    }

    /**
     * 获取所有的订阅某一节点的组名
     * @param topic 订阅节点名
     * @return 组名
     */
    public HashSet<String> queryTopicConsumeByWho(final String topic){
        HashSet<String> groups=new HashSet<>();
        Iterator<Entry<String,ConsumerGroupInfo>> it=this.consumerTable.entrySet().iterator();
        while (it.hasNext()){
            Entry<String,ConsumerGroupInfo> entry=it.next();
            ConsumerGroupInfo consumerGroupInfo=entry.getValue();
            if(consumerGroupInfo.getSubscriptionTable().contains(topic)){
                groups.add(entry.getKey());
            }
        }
        return groups;
    }
}
