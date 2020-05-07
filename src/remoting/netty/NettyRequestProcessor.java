package remoting.netty;

import io.netty.channel.ChannelHandlerContext;
import remoting.protocol.RemotingCommand;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:07
 *
 * netty 请求处理抽象类
 */
public interface NettyRequestProcessor {
    /**
     * 处理请求并返回
     * @param ctx 处理类
     * @param request 请求
     * @return response
     * @throws Exception
     */
    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws Exception;

    /**
     * 拒绝请求
     * @return
     */
    boolean rejectRequest();
}
