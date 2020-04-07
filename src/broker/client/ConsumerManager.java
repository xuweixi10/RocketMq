package broker.client;

import common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/2 22:36
 */
public class ConsumerManager {
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    //通道超时过期
    private static final long CHANNEL_EXPIRED_TIMEOUT = 1000*120;
}
