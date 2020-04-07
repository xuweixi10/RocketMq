package common.protocol.heartbeat;

/**
 * 拉取和推送两种类型
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/4 23:04
 */
public enum ConsumeType {
    CONSUME_ACTIVELY("PULL"),

    CONSUME_PASSIVELY("PUSH");

    private String typeCN;

    ConsumeType(String typeCN) {
        this.typeCN = typeCN;
    }

    public String getTypeCN() {
        return typeCN;
    }
}
