package broker.processor;

import broker.BrokerController;
import broker.mqtrace.ConsumeMessageContext;
import broker.mqtrace.ConsumeMessageHook;
import common.constant.LoggerName;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.exception.RemotingCommandException;
import remoting.netty.NettyRequestProcessor;
import remoting.protocol.RemotingCommand;

import java.util.List;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:05
 *
 * 拉取消息的处理模块
 */
public class PullMessageProcessor implements NettyRequestProcessor {
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    private final BrokerController brokerController;
    private List<ConsumeMessageHook> consumeMessageHookList;

    public PullMessageProcessor(BrokerController brokerController){
        this.brokerController=brokerController;
    }
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingCommandException {
        return processRequest(ctx.channel(),request,true);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    /**
     * 处理请求
     * @param channel 连接对应的handler
     * @param request 封装的请求
     * @param BrokerAllowSuspend 是否允许暂停？
     * @return
     */
    private RemotingCommand processRequest(Channel channel, RemotingCommand request, boolean BrokerAllowSuspend){
        return null;
    }
}
