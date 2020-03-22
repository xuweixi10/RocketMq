package remoting.netty;

public class NettyServerConfig implements Cloneable {
    /**
     * 1 监听端口
     * 2 Netty业务线程池线程个数
     * 3 Netty服务异步回调线程池线程数量
     * 4 Netty Selector线程个数
     * 5 控制单向的信号量
     * 6 控制异步的信号量
     * 7 服务空闲心跳检测 单位 秒
     * 8 Netty系统中 发送缓冲区的大小 65535 byte == 64 m
     * 9 Netty系统中 接受缓冲区的大小 65535 byte == 64 m
     * 10 是否开启缓存
     * 11 是否启用E_poll IO模型。Linux环境建议开启
     */
    private int listenePort=8888;
    private int serverWorkerThread=8;
    private int serverCallbackExecutorThreads=0;
    private int serverSelectorThreads=3;
    private int serverOneWaySemaphoreValue=256;
    private int serverAsyncSemaphoreValue = 64;
    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSzie=NettySystemConfig.socketSndbufSize;
    private int serverSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
    private boolean serverPooledByteBufAllocatorEnable = true;
    private boolean useEpollNativeSelector = false;

    public int getListenePort() {
        return listenePort;
    }

    public void setListenePort(int listenePort) {
        this.listenePort = listenePort;
    }

    public int getServerWorkerThread() {
        return serverWorkerThread;
    }

    public void setServerWorkerThread(int serverWorkerThread) {
        this.serverWorkerThread = serverWorkerThread;
    }

    public int getServerCallbackExecutorThreads() {
        return serverCallbackExecutorThreads;
    }

    public void setServerCallbackExecutorThreads(int serverCallbackExecutorThreads) {
        this.serverCallbackExecutorThreads = serverCallbackExecutorThreads;
    }

    public int getServerSelectorThreads() {
        return serverSelectorThreads;
    }

    public void setServerSelectorThreads(int serverSelectorThreads) {
        this.serverSelectorThreads = serverSelectorThreads;
    }

    public int getServerOneWaySemaphoreValue() {
        return serverOneWaySemaphoreValue;
    }

    public void setServerOneWaySemaphoreValue(int serverOneWaySemaphoreValue) {
        this.serverOneWaySemaphoreValue = serverOneWaySemaphoreValue;
    }

    public int getServerAsyncSemaphoreValue() {
        return serverAsyncSemaphoreValue;
    }

    public void setServerAsyncSemaphoreValue(int serverAsyncSemaphoreValue) {
        this.serverAsyncSemaphoreValue = serverAsyncSemaphoreValue;
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return serverChannelMaxIdleTimeSeconds;
    }

    public void setServerChannelMaxIdleTimeSeconds(int serverChannelMaxIdleTimeSeconds) {
        this.serverChannelMaxIdleTimeSeconds = serverChannelMaxIdleTimeSeconds;
    }

    public int getServerSocketSndBufSzie() {
        return serverSocketSndBufSzie;
    }

    public void setServerSocketSndBufSzie(int serverSocketSndBufSzie) {
        this.serverSocketSndBufSzie = serverSocketSndBufSzie;
    }

    public int getServerSocketRcvBufSize() {
        return serverSocketRcvBufSize;
    }

    public void setServerSocketRcvBufSize(int serverSocketRcvBufSize) {
        this.serverSocketRcvBufSize = serverSocketRcvBufSize;
    }

    public boolean isServerPooledByteBufAllocatorEnable() {
        return serverPooledByteBufAllocatorEnable;
    }

    public void setServerPooledByteBufAllocatorEnable(boolean serverPooledByteBufAllocatorEnable) {
        this.serverPooledByteBufAllocatorEnable = serverPooledByteBufAllocatorEnable;
    }

    public boolean isUseEpollNativeSelector() {
        return useEpollNativeSelector;
    }

    public void setUseEpollNativeSelector(boolean useEpollNativeSelector) {
        this.useEpollNativeSelector = useEpollNativeSelector;
    }
    @Override
    public Object clone() throws CloneNotSupportedException {
        return (NettyServerConfig) super.clone();
    }
}
