package remoting.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * 工具类
 */
public class RemotingUtil {
    /**
     * 获取操作系统
     */
    public static final String OS_NAME=System.getProperty("os.name");
    public static final Logger log = LoggerFactory.getLogger(RemotingHelper.ROCKETMQ_REMOTING);
    private static boolean isLinuxPlatform=false;
    private static boolean isWindowsPlatform=false;

    static {
        if(OS_NAME!=null&&OS_NAME.toLowerCase().contains("linux")){
            isLinuxPlatform=true;
        }
        if(OS_NAME!=null&&OS_NAME.toLowerCase().contains("windows")){
            isWindowsPlatform=true;
        }
    }
    public static boolean isLinuxPlatform(){
        return isLinuxPlatform;
    }
    public static boolean isWindowsPlatform(){
        return  isWindowsPlatform;
    }

    /**
     * 创建多路复用器
     * @return Selector
     * @throws IOException
     */
    public static Selector openSelector()throws IOException{
        Selector selector=null;
        if(isLinuxPlatform()){
            try{
                final Class<?>providerClazz =Class.forName("sun.nio.ch.EPollSelectorProvider");
                if (providerClazz!=null){
                    try {
                        Method method=providerClazz.getMethod("provider");
                        if(method!=null){
                            final SelectorProvider selectorProvider=(SelectorProvider)method.invoke(null);
                            if(selectorProvider!=null){
                                selector=selectorProvider.openSelector();
                            }
                        }
                    }catch (Exception e){
                        log.warn("Open ePoll Selector for linux platform exception", e);
                    }
                }
            }catch (ClassNotFoundException e){
                log.warn("class sun.nio.ch.EPollSelectorProvider not found", e);
            }
        }
        if(selector==null){
            selector=Selector.open();
        }
        return selector;
    }

    public static SocketChannel connect(SocketAddress remote){
        return connect(remote,1000*5);
    }
    public static SocketChannel connect(SocketAddress remote,final int timeoutMills){
        SocketChannel sc=null;
        try {
            sc=SocketChannel.open();
            //该通道设置为阻塞模式
            sc.configureBlocking(true);
            //表示无论如何都等未发送给对方的数据发送完毕才按照4次挥手的过程正常关闭链接
            sc.socket().setSoLinger(false,-1);
            /**
             * 控制是否开启Nagle算法，该算法是为了提高较慢的广域网传输效率，减小小分组的报文个数,默认关闭
             * 该算法要求一个TCP连接上最多只能有一个未被确认的小分组，在该小分组的确认到来之前，不能发送其他小分组。
             */
            sc.socket().setTcpNoDelay(true);
            sc.socket().setReceiveBufferSize(1024*64);
            sc.socket().setSendBufferSize(1024*64);
            sc.socket().connect(remote,timeoutMills);
            //该通道设置为非阻塞
            sc.configureBlocking(false);
            return sc;
        }catch (Exception e){
            if(sc!=null){
                try {
                    sc.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void closeChannel(Channel channel){
        final String addrRemote = RemotingHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote,
                        channelFuture.isSuccess());
            }
        });
    }
    public static String getLocalAddress(){
        try {
            Enumeration<NetworkInterface> enumeration=NetworkInterface.getNetworkInterfaces();
            ArrayList<String> ipv4Result=new ArrayList<String>();
            ArrayList<String> ipv6Result=new ArrayList<String>();
            while (enumeration.hasMoreElements()){
                final NetworkInterface networkInterface=enumeration.nextElement();
                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
                while (en.hasMoreElements()){
                    final InetAddress address=en.nextElement();
                    if(!address.isLoopbackAddress()){
                        if(address instanceof Inet6Address){
                            ipv6Result.add(normalizeHostAddress(address));
                        }else {
                            ipv4Result.add(normalizeHostAddress(address));
                        }
                    }
                }
            }

            if(!ipv4Result.isEmpty()){
                for(String ip:ipv4Result){
                    if(ip.startsWith("127.0")||ip.startsWith("192.168")){
                        continue;
                    }
                    return ip;
                }
                //如果都是本地地址则返回最后一个
                return ipv4Result.get(ipv4Result.size()-1);
            }else if(!ipv6Result.isEmpty()){
                return ipv6Result.get(0);
            }
            final InetAddress inetAddress=InetAddress.getLocalHost();
            return normalizeHostAddress(inetAddress);
        }catch (SocketException e){
            e.printStackTrace();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String normalizeHostAddress(final InetAddress localHost){
        if(localHost instanceof Inet6Address){
            return "["+localHost.getHostAddress()+"]";
        }
        else {
            return localHost.getHostAddress();
        }
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }

    public static String socketAddress2String(final SocketAddress addr) {
        StringBuilder sb = new StringBuilder();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) addr;
        sb.append(inetSocketAddress.getAddress().getHostAddress());
        sb.append(":");
        sb.append(inetSocketAddress.getPort());
        return sb.toString();
    }
}
