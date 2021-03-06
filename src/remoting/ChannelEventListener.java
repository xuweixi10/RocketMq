package remoting;

import io.netty.channel.Channel;

/**
 * @author 17194
 */
public interface ChannelEventListener {
    void onChannelConnect(final String remoteAddr, final Channel channel);

    void onChannelClose(final String remoteAddr,final Channel channel);

    void onChannelExpection(final String remoteAddr,final Channel channel);

    void onChannelIdle(final String remoteAddr,final Channel channel);
}
