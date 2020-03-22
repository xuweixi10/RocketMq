package broker;

import common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.netty.NettyClientConfig;
import remoting.netty.NettyServerConfig;

public class BrokerController {
    /**
     * 三个日志类，用于输出
     * broker 日志
     * 保护日志
     * 水印
     */
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    private static final Logger LOG_PROTECTION = LoggerFactory.getLogger(LoggerName.PROTECTION_LOGGER_NAME);
    private static final Logger LOG_WATER_MARK = LoggerFactory.getLogger(LoggerName.WATER_MARK_LOGGER_NAME);

    /**
     * 1 Netty服务配置
     *
     */
    private final NettyServerConfig nettyServerConfig;
    private final NettyClientConfig nettyClientConfig;

    public BrokerController(//
                            final BrokerConfig brokerConfig, //
                            final NettyServerConfig nettyServerConfig, //
                            final NettyClientConfig nettyClientConfig, //
                            final MessageStoreConfig messageStoreConfig, //
                            NettyServerConfig nettyServerConfig1){

        this.nettyServerConfig = nettyServerConfig;
    }

}