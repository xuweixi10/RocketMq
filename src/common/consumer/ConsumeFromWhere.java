package common.consumer;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/4 23:08
 */
public enum ConsumeFromWhere {
    CONSUME_FROM_LAST_OFFSET,

    @Deprecated
    CONSUME_FROM_LAST_OFFSET_AND_FROM_MIN_WHEN_BOOT_FIRST,
    @Deprecated
    CONSUME_FROM_MIN_OFFSET,
    @Deprecated
    CONSUME_FROM_MAX_OFFSET,
    CONSUME_FROM_FIRST_OFFSET,
    CONSUME_FROM_TIMESTAMP,
}
