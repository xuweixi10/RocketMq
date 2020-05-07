package broker.client;

import io.netty.channel.Channel;

import java.util.List;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/14 23:28
 */
public interface ConsumerIdsChangeListener {
    void consumerIdsChanged(final String group, List<Channel> channels);
}
