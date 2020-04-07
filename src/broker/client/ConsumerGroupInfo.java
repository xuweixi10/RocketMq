package broker.client;

import common.constant.LoggerName;
import common.consumer.ConsumeFromWhere;
import common.protocol.heartbeat.ConsumeType;
import common.protocol.heartbeat.MessageModel;
import common.protocol.heartbeat.SubscriptionData;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/2 22:40
 */
public class ConsumerGroupInfo {
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    private final String groupName;
    private final ConcurrentHashMap<String, SubscriptionData> subscriptionTable=
            new ConcurrentHashMap<>(16);
    private final ConcurrentHashMap<Channel,ClientChannelInfo> channelInfoTable=
            new ConcurrentHashMap<Channel, ClientChannelInfo>();
    private volatile ConsumeType consumeType;
    private volatile MessageModel messageModel;
    private volatile ConsumeFromWhere consumeFromWhere;
    private volatile long lastUpdateTimestamp = System.currentTimeMillis();

    public ConsumerGroupInfo(String groupName, ConsumeType consumeType, MessageModel messageModel
            , ConsumeFromWhere consumeFromWhere, long lastUpdateTimestamp) {
        this.groupName = groupName;
        this.consumeType = consumeType;
        this.messageModel = messageModel;
        this.consumeFromWhere = consumeFromWhere;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    /**
     * 客户端通道信息
     * @param clientId 客户端ID
     * @return
     */
    public ClientChannelInfo findChannel(final String clientId){
        Iterator<Entry<Channel,ClientChannelInfo>> it = this.channelInfoTable.entrySet().iterator();
        while (it.hasNext()){
            Entry<Channel,ClientChannelInfo> next=it.next();
            if(next.getValue().getClientId().equals(clientId)){
                return next.getValue();
            }
        }
        return null;
    }

    /**
     * 获取所有的信道
     * @return
     */

    public List<Channel> getAllChannel(){
        List<Channel> result=new ArrayList<Channel>();
        result.addAll(this.channelInfoTable.keySet());
        return result;
    }

    public ConcurrentHashMap<String, SubscriptionData> getSubscriptionTable() {
        return subscriptionTable;
    }

    public ConcurrentHashMap<Channel, ClientChannelInfo> getChannelInfoTable() {
        return channelInfoTable;
    }
    /**
     * 获取所有的客户端id
     * @return
     */
    public List<String> getAllClientId(){
        List<String> result=new ArrayList<String>();
        Iterator<Entry<Channel,ClientChannelInfo>> it=this.channelInfoTable.entrySet().iterator();
        while (it.hasNext()){
            Entry<Channel,ClientChannelInfo> entry=it.next();
            ClientChannelInfo clientChannelInfo=entry.getValue();
            result.add(clientChannelInfo.getClientId());
        }
        return result;
    }

    /**
     * 注销某个客户端连接的通道,
     * @param clientChannelInfo 客户端信道信息
     */
    public void unregisterChannel(final ClientChannelInfo clientChannelInfo) {
        ClientChannelInfo old = this.channelInfoTable.remove(clientChannelInfo.getChannel());
        if (old != null) {
            log.info("unregister a consumer[{}] from consumerGroupInfo {}", this.groupName, old.toString());
        }
    }

    /**
     * 关闭某个通道
     * @param channel 对应的通道
     * @param remotingAddr 未知？
     * @return 是否关闭
     */
    public boolean doChannelCloseEvent(final Channel channel,final String remotingAddr) {
        final ClientChannelInfo clientChannelInfo = this.channelInfoTable.remove(channel);
        if (null != clientChannelInfo) {
            log.warn("NETTY EVENT: remove not active channel[{}] from ConsumerGroupInfo groupChannelTable, consumer group: {}",
                    clientChannelInfo.toString(), this.groupName);
            return true;
        }
        return false;
    }

    public boolean updateChannel(final ClientChannelInfo infoNew, ConsumeType consumeType,
    MessageModel messageModel, ConsumeFromWhere consumeFromWhere) {
        boolean updated = false;
        this.consumeType = consumeType;
        this.messageModel = messageModel;
        this.consumeFromWhere = consumeFromWhere;
        ClientChannelInfo infoOld=this.channelInfoTable.get(infoNew.getChannel());
        //没有对应通道的信息
        if(null==infoOld){
            ClientChannelInfo prev = this.channelInfoTable.put(infoNew.getChannel(),infoNew);
            if(null!=prev){
                log.info("new consumer connected, group: {} {} {} channel: {}", this.groupName, consumeType,
                        messageModel, infoNew.toString());
                updated=true;
            }
            infoOld=infoNew;
        }
        else{
            //对应的通道已被占用，但是不是同一个客户端
            if(!infoOld.getClientId().equals(infoNew.getClientId())){
                log.error("[BUG] consumer channel exist in broker, but clientId not equal. GROUP: {} OLD: {} NEW: {} ",
                        this.groupName,
                        infoOld.toString(),
                        infoNew.toString());
                this.channelInfoTable.put(infoNew.getChannel(), infoNew);
            }
        }
        this.lastUpdateTimestamp=System.currentTimeMillis();
        infoOld.setLastUpdateTimestamp(lastUpdateTimestamp);
        return updated;
    }
    public boolean updateSubscription(final Set<SubscriptionData> subList){
        boolean updated=false;
        for(SubscriptionData sub:subList){
            SubscriptionData old=this.subscriptionTable.get(sub.getTopic());
            if(null==old){
                //增加新的订阅消息
                SubscriptionData prev=this.subscriptionTable.putIfAbsent(sub.getTopic(),sub);
                if(null!=prev){
                    updated = true;
                    log.info("subscription changed, add new topic, group: {} {}",
                            this.groupName,
                            sub.toString());
                }

            }else if(sub.getSubVersion()>old.getSubVersion()){
                //类型为被动式的消费组
                if(this.consumeType==ConsumeType.CONSUME_PASSIVELY){
                    log.info("subscription changed, group: {} OLD: {} NEW: {}",
                            this.groupName,
                            old.toString(),
                            sub.toString()
                    );
                }
                this.subscriptionTable.put(sub.getTopic(), sub);
            }
        }
        //检查订阅是否都有订阅信息，没有则删除
        Iterator<Entry<String,SubscriptionData>> it=this.subscriptionTable.entrySet().iterator();
        while (it.hasNext()){
            Entry<String,SubscriptionData> next=it.next();
            String oldTopic=next.getKey();
            boolean exist=false;
            for(SubscriptionData sub:subList){
                if(sub.getTopic().equals(oldTopic)){
                    exist=true;
                    break;
                }
            }
            if (!exist) {
                log.warn("subscription changed, group: {} remove topic {} {}",
                        this.groupName,
                        oldTopic,
                        next.getValue().toString()
                );

                it.remove();
                updated = true;
            }
        }
        this.lastUpdateTimestamp = System.currentTimeMillis();

        return updated;
    }
    public Set<String> getSubscribeTopics() {
        return subscriptionTable.keySet();
    }

    public SubscriptionData findSubscriptionData(final String topic) {
        return this.subscriptionTable.get(topic);
    }

    public String getGroupName() {
        return groupName;
    }

    public ConsumeType getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(ConsumeType consumeType) {
        this.consumeType = consumeType;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public ConsumeFromWhere getConsumeFromWhere() {
        return consumeFromWhere;
    }

    public void setConsumeFromWhere(ConsumeFromWhere consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }
}

