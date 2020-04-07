package common.protocol.heartbeat;

/**
 * 消息模式
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/4 23:05
 */
public enum MessageModel {
    /**
     * broadcast
     */
    BROADCASTING("BROADCASTING"),
    /**
     * clustering
     */
    CLUSTERING("CLUSTERING");

    private String modeCN;

    MessageModel(String modeCN) {
        this.modeCN = modeCN;
    }

    public String getModeCN() {
        return modeCN;
    }
}
