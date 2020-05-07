package broker.client;

import common.constant.LoggerName;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.common.RemotingHelper;
import remoting.common.RemotingUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/7 21:06
 */
public class ProducerManager {
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    private static final long LOCK_TIMEOUT_MILLIS=3000;
    private static final long CHANNEL_EXPIRED_TIMEOUT=1000*120;
    private final Lock groupChannelLock=new ReentrantLock();
    //group-data
    private final HashMap<String,HashMap<Channel,ClientChannelInfo>> groupChannelTable=new HashMap<>();

    public ProducerManager() {

    }
    public HashMap<String,HashMap<Channel,ClientChannelInfo>> getGroupChannelTable() {
        HashMap<String, HashMap<Channel, ClientChannelInfo>> newGroupChannelTable = new HashMap<>();
        try {
            if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) ;
            try {
                newGroupChannelTable.putAll(this.groupChannelTable);
            } finally {
                groupChannelLock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newGroupChannelTable;
    }
    public void scanNotActiveChannel() {
        try {
            if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    for (Entry<String, HashMap<Channel, ClientChannelInfo>> entry : this.groupChannelTable.entrySet()) {
                        String group = entry.getKey();
                        final HashMap<Channel, ClientChannelInfo> chlMap = entry.getValue();
                        Iterator<Entry<Channel, ClientChannelInfo>> it = chlMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Entry<Channel, ClientChannelInfo> item = it.next();
                            final ClientChannelInfo clientChannelInfo = item.getValue();
                            long diff = System.currentTimeMillis() - clientChannelInfo.getLastUpdateTimestamp();
                            if (diff > CHANNEL_EXPIRED_TIMEOUT) {
                                it.remove();
                                log.warn("SCAN: remove expired channel[{}] from ProducerManager groupChannelTable, producer group name: {}",
                                        RemotingHelper.parseChannelRemoteAddr(clientChannelInfo.getChannel()), group);
                                RemotingUtil.closeChannel(clientChannelInfo.getChannel());
                            }
                        }
                    }
                } finally {
                    groupChannelLock.unlock();
                }
            } else {
                log.warn("ProducerManager scanNotActiveChannel lock timeout");
            }
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }
    public void doChannelCloseEvent(final String remoteAddr,final Channel channel) {
        if (channel != null) {
            try {
                if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                    try {
                        for (final Entry<String, HashMap<Channel, ClientChannelInfo>> entry : this.groupChannelTable.entrySet()) {
                            final String group = entry.getKey();
                            final HashMap<Channel, ClientChannelInfo> clientChannelInfoTable = entry.getValue();
                            ClientChannelInfo clientChannelInfo = clientChannelInfoTable.remove(channel);
                            if (null != clientChannelInfo) {
                                log.info(
                                        "NETTY EVENT: remove channel[{}][{}] from ProducerManager groupChannelTable, producer group: {}",
                                        clientChannelInfo.toString(), remoteAddr, group);
                            }
                        }
                    } finally {
                        this.groupChannelLock.unlock();
                    }

                } else {
                    log.warn("ProducerManager doChannelCloseEvent lock timeout");
                }
            } catch (Exception e) {
                log.warn("", e);
            }
        }
    }

    /**
     * 注册消费者
     * @param group 组名
     * @param clientChannelInfo 客户端通道信息
     */
    public void registerProducer(final String group,final ClientChannelInfo clientChannelInfo) {
        try {
            ClientChannelInfo clientChannelInfoFound = null;
            if (this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    //判断是否有对应的组
                    HashMap<Channel, ClientChannelInfo> clientTable = this.groupChannelTable.get(group);
                    if (null == clientTable) {
                        clientTable = new HashMap<>();
                        this.groupChannelTable.put(group, clientTable);
                    }
                    //判断组里是否有对应的客户端通道信息
                    clientChannelInfoFound = clientTable.get(clientChannelInfo.getChannel());
                    if (null == clientChannelInfoFound) {
                        clientTable.put(clientChannelInfo.getChannel(), clientChannelInfo);
                        log.info("new producer connected, group: {} channel: {}", group,
                                clientChannelInfo.toString());
                    }
                } finally {
                    this.groupChannelLock.unlock();
                }
                //原先消费者已存在，更新其上一次信息
                if (null != clientChannelInfoFound) {
                    clientChannelInfoFound.setLastUpdateTimestamp(System.currentTimeMillis());
                }
            } else {
                log.warn("ProducerManager registerProducer lock timeout");
            }
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }
    public void unregisterProducer(final String group,final ClientChannelInfo clientChannelInfo){
        try {
            if(this.groupChannelLock.tryLock(LOCK_TIMEOUT_MILLIS,TimeUnit.MILLISECONDS)){
                try {
                    HashMap<Channel,ClientChannelInfo> channelTable=this.groupChannelTable.get(group);
                    if(null!=channelTable&&!channelTable.isEmpty()){
                        ClientChannelInfo old=channelTable.remove(clientChannelInfo.getChannel());
                        if(old!=null){
                            log.info("unregister a producer[{}] from groupChannelTable {}", group,
                                    clientChannelInfo.toString());
                        }
                        //组里只有一个用户，移除后把对应的组也移除
                        if(channelTable.isEmpty()){
                            this.groupChannelTable.remove(group);
                            log.info("unregister a producer group[{}] from groupChannelTable", group);
                        }
                    }
                }finally {
                    this.groupChannelLock.unlock();
                }

            }
            else {
                log.warn("ProducerManager unregisterProducer lock timeout");
            }
        }catch (InterruptedException e){
            log.warn("",e);
        }
    }
}
