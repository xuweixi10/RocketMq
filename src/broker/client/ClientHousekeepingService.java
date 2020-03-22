package broker.client;

import common.constant.LoggerName;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.ChannelEventListener;

public class ClientHousekeepingService implements ChannelEventListener {
    private static final Logger log=LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelExpection(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {

    }
}
