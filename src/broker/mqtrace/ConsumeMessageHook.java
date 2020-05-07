package broker.mqtrace;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/30 11:22
 * 消费信息钩子,消费起始点和结束点
 */
public interface ConsumeMessageHook {
    String hookName();

    void consumeMessageBefore(final ConsumeMessageContext context);

    void consumeMessageAfter(final ConsumeMessageContext context);
}
