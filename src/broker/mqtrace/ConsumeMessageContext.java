package broker.mqtrace;

import store.stats.BrokerStatsManager;

import java.util.Map;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/28 10:12
 * 消费信息内容
 */
public class ConsumeMessageContext {
    private String consumerGroup;
    private String topic;
    private Integer queueId;
    private String clientHost;
    private String storeHost;
    private Map<String, Long> messageIds;
    private int bodyLength;
    private boolean success;
    private String status;
    private Object mqTraceContext;

    private String commercialOwner;
    private BrokerStatsManager.StatsType commercialRcvStats;
    private int commercialRcvTimes;
    private int commercialRcvSize;

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public String getStoreHost() {
        return storeHost;
    }

    public void setStoreHost(String storeHost) {
        this.storeHost = storeHost;
    }

    public Map<String, Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(Map<String, Long> messageIds) {
        this.messageIds = messageIds;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getMqTraceContext() {
        return mqTraceContext;
    }

    public void setMqTraceContext(Object mqTraceContext) {
        this.mqTraceContext = mqTraceContext;
    }

    public String getCommercialOwner() {
        return commercialOwner;
    }

    public void setCommercialOwner(String commercialOwner) {
        this.commercialOwner = commercialOwner;
    }

    public BrokerStatsManager.StatsType getCommercialRcvStats() {
        return commercialRcvStats;
    }

    public void setCommercialRcvStats(BrokerStatsManager.StatsType commercialRcvStats) {
        this.commercialRcvStats = commercialRcvStats;
    }

    public int getCommercialRcvTimes() {
        return commercialRcvTimes;
    }

    public void setCommercialRcvTimes(int commercialRcvTimes) {
        this.commercialRcvTimes = commercialRcvTimes;
    }

    public int getCommercialRcvSize() {
        return commercialRcvSize;
    }

    public void setCommercialRcvSize(int commercialRcvSize) {
        this.commercialRcvSize = commercialRcvSize;
    }
}
