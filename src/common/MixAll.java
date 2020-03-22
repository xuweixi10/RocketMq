package common;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

public class MixAll {
    public static final String ROCKETMQ_HOME_ENV = "ROCKETMQ_HOME";
    public static final String ROCKETMQ_HOME_PROPERTY = "rocketmq.home.dir";
    public static final String NAMESRV_ADDR_ENV = "NAMESRV_ADDR";
    public static final String NAMESRV_ADDR_PROPERTY = "rocketmq.namesrv.addr";
    public static final String MESSAGE_COMPRESS_LEVEL = "rocketmq.message.compressLevel";
    public static final String DEFAULT_NAMESRV_ADDR_LOOKUP = "jmenv.tbsite.net";
    public static final String WS_DOMAIN_NAME = System.getProperty("rocketmq.namesrv.domain", DEFAULT_NAMESRV_ADDR_LOOKUP);
    public static final String WS_DOMAIN_SUBGROUP = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
    // http://jmenv.tbsite.net:8080/rocketmq/nsaddr
    public static final String WS_ADDR = "http://" + WS_DOMAIN_NAME + ":8080/rocketmq/" + WS_DOMAIN_SUBGROUP;
    public static final String DEFAULT_TOPIC = "TBW102";
    public static final String BENCHMARK_TOPIC = "BenchmarkTest";
    public static final String DEFAULT_PRODUCER_GROUP = "DEFAULT_PRODUCER";
    public static final String DEFAULT_CONSUMER_GROUP = "DEFAULT_CONSUMER";
    public static final String TOOLS_CONSUMER_GROUP = "TOOLS_CONSUMER";
    public static final String FILTERSRV_CONSUMER_GROUP = "FILTERSRV_CONSUMER";
    public static final String MONITOR_CONSUMER_GROUP = "__MONITOR_CONSUMER";
    public static final String CLIENT_INNER_PRODUCER_GROUP = "CLIENT_INNER_PRODUCER";
    public static final String SELF_TEST_PRODUCER_GROUP = "SELF_TEST_P_GROUP";
    public static final String SELF_TEST_CONSUMER_GROUP = "SELF_TEST_C_GROUP";
    public static final String SELF_TEST_TOPIC = "SELF_TEST_TOPIC";
    public static final String OFFSET_MOVED_EVENT = "OFFSET_MOVED_EVENT";
    public static final String ONS_HTTP_PROXY_GROUP = "CID_ONS-HTTP-PROXY";
    public static final String CID_ONSAPI_PERMISSION_GROUP = "CID_ONSAPI_PERMISSION";
    public static final String CID_ONSAPI_OWNER_GROUP = "CID_ONSAPI_OWNER";
    public static final String CID_ONSAPI_PULL_GROUP = "CID_ONSAPI_PULL";
    public static final String CID_RMQ_SYS_PREFIX = "CID_RMQ_SYS_";

    public static final List<String> LOCAL_INET_ADDRESS = getLocalInetAddress();
    public static final String LOCALHOST = localhost();
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final long MASTER_ID = 0L;
    public static final long CURRENT_JVM_PID = getPID();

    public static final String RETRY_GROUP_TOPIC_PREFIX = "%RETRY%";

    public static final String DLQ_GROUP_TOPIC_PREFIX = "%DLQ%";
    public static final String SYSTEM_TOPIC_PREFIX = "rmq_sys_";
    public static final String UNIQUE_MSG_QUERY_FLAG = "_UNIQUE_KEY_QUERY";
    public static final String DEFAULT_TRACE_REGION_ID = "DefaultRegion";
    public static final String CONSUME_CONTEXT_TYPE = "ConsumeContextType";
    //获取重试的Topic
    public static String getRetryTopic(final String consumerGroup){
        return RETRY_GROUP_TOPIC_PREFIX+consumerGroup;
    }
    //判断是否为系统消费组
    public static boolean isSysConsumerGroup(final String consumerGroup){
        return consumerGroup.startsWith(CID_RMQ_SYS_PREFIX);
    }
    //判断是否为系统订阅节点
    public static boolean isSystemTopic(final String topic) {
        return topic.startsWith(SYSTEM_TOPIC_PREFIX);
    }

    //获取死信消息
    public static String getDLQTopic(final String consumerGroup) {
        return DLQ_GROUP_TOPIC_PREFIX + consumerGroup;
    }
    //获取broker 的VIP通道 如果改变了就把端口数减2
    public static String brokerVIPChannel(final boolean isChange,final String brokerAddr){
        if(isChange){
            String[] ipAndPort=brokerAddr.split(":");
            String brokerAddrNew = ipAndPort[0] + ":" + (Integer.parseInt(ipAndPort[1]) - 2);
            return brokerAddrNew;
        }
        else{
            return brokerAddr;
        }
    }
    //获取进程ID
    public static long getPID(){
        //结构为 进程id@+机器名
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

    /**
     * 创建BrokerId
     * @param ip broker所在的ip或域名
     * @param port 对应的端口
     * @return brokerId 一个64位的地址
     */
    public static long createBrokerId(final String ip,final int port){
        InetSocketAddress inetSocketAddress=new InetSocketAddress(ip,port);
        byte[] ipArray=inetSocketAddress.getAddress().getAddress();
        //从堆空间中分配一个容量大小为8的HeapByteBuffer数组作为缓冲区的byte数据存储器
        ByteBuffer byteBuffer=ByteBuffer.allocate(8);
        //四个字节加int四个字节刚好是8
        byteBuffer.put(ipArray);
        byteBuffer.putInt(port);
        long value = byteBuffer.getLong(0);
        return Math.abs(value);
    }
}
