package store.config;

import common.annotation.ImportantField;
import store.ConsumeQueue;

import java.io.File;

public class MessageStoreConfig {
    //日志保存的根目录
    @ImportantField
    private String storePathRootDir = System.getProperty("user.home") + File.separator + "store";
    //提交日志的目录
    @ImportantField
    private String storePathCommitLog = System.getProperty("user.home") + File.separator + "store" + File.separator + "commitlog";
    //提交日志的大小，默认1g
    private int mapedFileSizeCommitLog = 1024 * 1024 * 1024;
    //消费者队列大小，默认30w个
    private int mapedFileSizeConsumeQueue = 300000 * ConsumeQueue.CQ_STORE_UNIT_SIZE;

    //提交日志持久化的时间间隔 单位ms
    @ImportantField
    private int flushIntervalCommitLog = 500;
    //只在事务存储池开启时使用，提交数据至文件通道
    @ImportantField
    private int ccommitIntervalCommitLog = 200;
    //消息存储到commitlog文件时获取锁类型，如果为true使用ReentrantLock否则使用自旋锁
    private boolean useReentrantLockWhenPutMessage = false;
    //是否刷新提交日志计划，默认为实时
    @ImportantField
    private boolean flushCommitLogTimed = false;
    //ConsumeQueue刷新间隔
    private int flushIntervalConsumeQueue = 1000;
    // 回收（清除）资源间隔
    private int cleanResourceInterval = 10000;
    //CommitLog删除间隔
    private int deleteCommitLogFilesInterval = 100;
    /**
     * 1 删除consumequeue文件时间间隔
     * 2 销毁MappedFile被拒绝的最大存活时间，默认120s。清除过期文件线程在初次销毁mappedfile时，
     * 如果该文件被其他线程引用，引用次数大于0.则设置MappedFile的可用状态为false，
     * 并设置第一次删除时间，下一次清理任务到达时，如果系统时间大于初次删除时间加上本参数
     * 则将ref次数一次减1000，知道引用次数小于0，则释放物理资源
     * 3 重试删除文件间隔，配合destorymapedfileintervalforcibly
     */
    private int deleteConsumeQueueFileInterval = 100;
    private int destroyMapedFileIntervalForcibly = 1000 * 120;
    private int redeleteHangedFileInterval = 1000 * 120;
    /**
     * 1 默认删除时间 4am
     * 2 commitlog目录所在分区的最大使用比例，如果commitlog目录所在的分区使用比例大于该值，
     * 则触发过期文件删除
     */
    @ImportantField
    private String deleteWhen = "04";
    private int diskMaxUsedSpaceRatio = 75;
    //删除一个文件前至少保存的时间 默认72小时
    @ImportantField
    private int fileReservedTime = 72;
    //ConsumeQueue的流控制
    private int putMsgIndexHightWater = 600000;
    //单个消息最大容量 默认4MB
    private int maxMessageSize = 1024 * 1024 * 4;
    //恢复时是否CRC效验
    private boolean checkCRCOnRecover = true;
    /**
     * 1 写入commitLog需要多少页
     * 2 将数据提交到文件时要提交多少页
     * 3 磁盘在预热状态时至少写入的页数 默认4096
     */
    private int flushCommitLogLeastPages = 4;
    private int commitCommitLogLeastPages = 4;
    private int flushLeastPagesWhenWarmMapedFile = 1024 / 4 * 16;
    /**
     * 1 ConsumeQueue写入至少需要页的数量,默认2页,
     * 2 commitlog两次写入磁盘的最大间隔,如果超过该间隔,将fushCommitLogLeastPages要求直接写入磁盘操作
     * 3 Commitlog两次提交的最大间隔,如果超过该间隔,将直接提交
     * 4 Consume两次写入的最大间隔,如果超过该间隔,将忽略
     */
    // How many pages are to be flushed when flush ConsumeQueue
    private int flushConsumeQueueLeastPages = 2;
    private int flushCommitLogThoroughInterval = 1000 * 10;
    private int commitCommitLogThoroughInterval = 200;
    private int flushConsumeQueueThoroughInterval = 1000 * 60;
    //一次服务消息端消息拉取,消息在内存中传输允许的最大传输字节数默认256kb
    @ImportantField
    private int maxTransferBytesOnMessageInMemory = 1024 * 256;
    //一次服务消息拉取,消息在内存中传输运行的最大消息条数,默认为32条
    @ImportantField
    private int maxTransferCountOnMessageInMemory = 32;
    @ImportantField
    private int maxTransferBytesOnMessageInDisk = 1024 * 64;
    @ImportantField
    private int maxTransferCountOnMessageInDisk = 8;
    //访问消息在内存中比率,默认为40
    @ImportantField
    private int accessMessageInMemoryMaxRadio = 40;
    /**
     * 1 是否支持消息索引
     * 2 单个索引文件hash槽的个数,默认为五百万
     * 3 单个索引文件索引条目的个数,默认为两千万
     * 4 一次查询消息最大返回消息条数,默认64条
     */
    @ImportantField
    private boolean messageIndexEnable = true;
    private int maxHashSlotNum = 5000000;
    private int maxIndexNum = 5000000 * 4;
    private int maxMsgsNumBatch = 64;
    /**
     * 1 消息索引是否安全,默认为 false,文件恢复时选择文件检测点（commitlog.consumeque）的最小的与文件最后更新对比
     * 如果为true，文件恢复时选择文件检测点保存的索引更新时间作为对比
     * 2 Master监听端口,从服务器连接该端口,默认为10912
     * 3 Master与Slave心跳包发送间隔
     * 4 Master与save长连接空闲时间,超过该时间将关闭连接
     * 5 一次HA主从同步传输的最大字节长度,默认为32K
     */
    @ImportantField
    private boolean messageIndexSafe = false;
    private int haListenPort = 10912;
    private int haSendHeartbeatInterval = 1000 * 5;
    private int haHousekeepingInterval = 1000 * 20;
    private int haTransferBatchSize = 1024 * 32;
    /**
     * 1 Master服务器IP地址与端口号
     * 2 允许从服务器落户的最大偏移字节数,默认为256M。超过该值则表示该Slave不可用
     */
    @ImportantField
    private String haMasterAddress = null;
    private int haSlaveFallbehindMax = 1024 * 1024 * 256;
    @ImportantField
    private BrokerRole brokerRole = BrokerRole.ASYNC_MASTER;
    /**
     * 1 刷盘方式,默认为 ASYNC_FLUSH(异步刷盘),可选值SYNC_FLUSH(同步刷盘)
     * 2 同步刷盘超时时间
     * 3 延迟队列等级（s=秒，m=分，h=小时）
     * 4 延迟队列拉取进度刷盘间隔。默认10s
     */
    @ImportantField
    private FlushDiskType flushDiskType = FlushDiskType.ASYNC_FLUSH;
    private int syncFlushTimeout = 1000 * 5;
    private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
    private long flushDelayOffsetInterval = 1000 * 10;
    /**
     * 1 是否支持强行删除过期文件
     * 2 是否温和(?)地使用 MappedFile如果为true,将不强制将内存映射文件锁定在内存中
     * 3 从服务器是否坚持 offset检测
     * 4 是否支持 PutMessage Lock锁打印信息
     * 5 是否允许重复复制,默认为 false
     * 6 是否统计磁盘的使用情况,默认为true
     */
    @ImportantField
    private boolean cleanFileForciblyEnable = true;
    private boolean warmMapedFileEnable = false;
    private boolean offsetCheckInSlave = false;
    private boolean debugLockEnable = false;
    private boolean duplicationEnable = false;
    private boolean diskFallRecorded = true;
    /**
     * 1 putMessage锁占用超过该时间,表示 PageCache忙
     * 2 查询消息默认返回条数,默认为32
     */
    private long osPageCacheBusyTimeOutMills = 1000;
    private int defaultQueryMaxNum = 32;
    /**
     * 1 Commitlog是否开启 transientStorePool机制,默认为 false
     * 2 transientStorePool中缓存 ByteBuffer个数,默认5个
     * 3 从 transientStorepool中获取 ByteBuffer是否支持fail-fast
     */
    @ImportantField
    private boolean transientStorePoolEnable = false;
    private int transientStorePoolSize = 5;
    private boolean fastFailIfNoBufferInStorePool = false;

    public String getStorePathRootDir() {
        return storePathRootDir;
    }

    public void setStorePathRootDir(String storePathRootDir) {
        this.storePathRootDir = storePathRootDir;
    }

    public String getStorePathCommitLog() {
        return storePathCommitLog;
    }

    public void setStorePathCommitLog(String storePathCommitLog) {
        this.storePathCommitLog = storePathCommitLog;
    }

    public int getMapedFileSizeCommitLog() {
        return mapedFileSizeCommitLog;
    }

    public void setMapedFileSizeCommitLog(int mapedFileSizeCommitLog) {
        this.mapedFileSizeCommitLog = mapedFileSizeCommitLog;
    }

    public int getMapedFileSizeConsumeQueue() {
        return mapedFileSizeConsumeQueue;
    }

    public void setMapedFileSizeConsumeQueue(int mapedFileSizeConsumeQueue) {
        this.mapedFileSizeConsumeQueue = mapedFileSizeConsumeQueue;
    }

    public int getFlushIntervalCommitLog() {
        return flushIntervalCommitLog;
    }

    public void setFlushIntervalCommitLog(int flushIntervalCommitLog) {
        this.flushIntervalCommitLog = flushIntervalCommitLog;
    }

    public int getCcommitIntervalCommitLog() {
        return ccommitIntervalCommitLog;
    }

    public void setCcommitIntervalCommitLog(int ccommitIntervalCommitLog) {
        this.ccommitIntervalCommitLog = ccommitIntervalCommitLog;
    }

    public boolean isUseReentrantLockWhenPutMessage() {
        return useReentrantLockWhenPutMessage;
    }

    public void setUseReentrantLockWhenPutMessage(boolean useReentrantLockWhenPutMessage) {
        this.useReentrantLockWhenPutMessage = useReentrantLockWhenPutMessage;
    }

    public boolean isFlushCommitLogTimed() {
        return flushCommitLogTimed;
    }

    public void setFlushCommitLogTimed(boolean flushCommitLogTimed) {
        this.flushCommitLogTimed = flushCommitLogTimed;
    }

    public int getFlushIntervalConsumeQueue() {
        return flushIntervalConsumeQueue;
    }

    public void setFlushIntervalConsumeQueue(int flushIntervalConsumeQueue) {
        this.flushIntervalConsumeQueue = flushIntervalConsumeQueue;
    }

    public int getCleanResourceInterval() {
        return cleanResourceInterval;
    }

    public void setCleanResourceInterval(int cleanResourceInterval) {
        this.cleanResourceInterval = cleanResourceInterval;
    }

    public int getDeleteCommitLogFilesInterval() {
        return deleteCommitLogFilesInterval;
    }

    public void setDeleteCommitLogFilesInterval(int deleteCommitLogFilesInterval) {
        this.deleteCommitLogFilesInterval = deleteCommitLogFilesInterval;
    }

    public int getDeleteConsumeQueueFileInterval() {
        return deleteConsumeQueueFileInterval;
    }

    public void setDeleteConsumeQueueFileInterval(int deleteConsumeQueueFileInterval) {
        this.deleteConsumeQueueFileInterval = deleteConsumeQueueFileInterval;
    }

    public int getDestroyMapedFileIntervalForcibly() {
        return destroyMapedFileIntervalForcibly;
    }

    public void setDestroyMapedFileIntervalForcibly(int destroyMapedFileIntervalForcibly) {
        this.destroyMapedFileIntervalForcibly = destroyMapedFileIntervalForcibly;
    }

    public int getRedeleteHangedFileInterval() {
        return redeleteHangedFileInterval;
    }

    public void setRedeleteHangedFileInterval(int redeleteHangedFileInterval) {
        this.redeleteHangedFileInterval = redeleteHangedFileInterval;
    }

    public String getDeleteWhen() {
        return deleteWhen;
    }

    public void setDeleteWhen(String deleteWhen) {
        this.deleteWhen = deleteWhen;
    }

    public int getDiskMaxUsedSpaceRatio() {
        return diskMaxUsedSpaceRatio;
    }

    public void setDiskMaxUsedSpaceRatio(int diskMaxUsedSpaceRatio) {
        this.diskMaxUsedSpaceRatio = diskMaxUsedSpaceRatio;
    }

    public int getFileReservedTime() {
        return fileReservedTime;
    }

    public void setFileReservedTime(int fileReservedTime) {
        this.fileReservedTime = fileReservedTime;
    }

    public int getPutMsgIndexHightWater() {
        return putMsgIndexHightWater;
    }

    public void setPutMsgIndexHightWater(int putMsgIndexHightWater) {
        this.putMsgIndexHightWater = putMsgIndexHightWater;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public boolean isCheckCRCOnRecover() {
        return checkCRCOnRecover;
    }

    public void setCheckCRCOnRecover(boolean checkCRCOnRecover) {
        this.checkCRCOnRecover = checkCRCOnRecover;
    }

    public int getFlushCommitLogLeastPages() {
        return flushCommitLogLeastPages;
    }

    public void setFlushCommitLogLeastPages(int flushCommitLogLeastPages) {
        this.flushCommitLogLeastPages = flushCommitLogLeastPages;
    }

    public int getCommitCommitLogLeastPages() {
        return commitCommitLogLeastPages;
    }

    public void setCommitCommitLogLeastPages(int commitCommitLogLeastPages) {
        this.commitCommitLogLeastPages = commitCommitLogLeastPages;
    }

    public int getFlushLeastPagesWhenWarmMapedFile() {
        return flushLeastPagesWhenWarmMapedFile;
    }

    public void setFlushLeastPagesWhenWarmMapedFile(int flushLeastPagesWhenWarmMapedFile) {
        this.flushLeastPagesWhenWarmMapedFile = flushLeastPagesWhenWarmMapedFile;
    }

    public int getFlushConsumeQueueLeastPages() {
        return flushConsumeQueueLeastPages;
    }

    public void setFlushConsumeQueueLeastPages(int flushConsumeQueueLeastPages) {
        this.flushConsumeQueueLeastPages = flushConsumeQueueLeastPages;
    }

    public int getFlushCommitLogThoroughInterval() {
        return flushCommitLogThoroughInterval;
    }

    public void setFlushCommitLogThoroughInterval(int flushCommitLogThoroughInterval) {
        this.flushCommitLogThoroughInterval = flushCommitLogThoroughInterval;
    }

    public int getCommitCommitLogThoroughInterval() {
        return commitCommitLogThoroughInterval;
    }

    public void setCommitCommitLogThoroughInterval(int commitCommitLogThoroughInterval) {
        this.commitCommitLogThoroughInterval = commitCommitLogThoroughInterval;
    }

    public int getFlushConsumeQueueThoroughInterval() {
        return flushConsumeQueueThoroughInterval;
    }

    public void setFlushConsumeQueueThoroughInterval(int flushConsumeQueueThoroughInterval) {
        this.flushConsumeQueueThoroughInterval = flushConsumeQueueThoroughInterval;
    }

    public int getMaxTransferBytesOnMessageInMemory() {
        return maxTransferBytesOnMessageInMemory;
    }

    public void setMaxTransferBytesOnMessageInMemory(int maxTransferBytesOnMessageInMemory) {
        this.maxTransferBytesOnMessageInMemory = maxTransferBytesOnMessageInMemory;
    }

    public int getMaxTransferCountOnMessageInMemory() {
        return maxTransferCountOnMessageInMemory;
    }

    public void setMaxTransferCountOnMessageInMemory(int maxTransferCountOnMessageInMemory) {
        this.maxTransferCountOnMessageInMemory = maxTransferCountOnMessageInMemory;
    }

    public int getMaxTransferBytesOnMessageInDisk() {
        return maxTransferBytesOnMessageInDisk;
    }

    public void setMaxTransferBytesOnMessageInDisk(int maxTransferBytesOnMessageInDisk) {
        this.maxTransferBytesOnMessageInDisk = maxTransferBytesOnMessageInDisk;
    }

    public int getMaxTransferCountOnMessageInDisk() {
        return maxTransferCountOnMessageInDisk;
    }

    public void setMaxTransferCountOnMessageInDisk(int maxTransferCountOnMessageInDisk) {
        this.maxTransferCountOnMessageInDisk = maxTransferCountOnMessageInDisk;
    }

    public int getAccessMessageInMemoryMaxRadio() {
        return accessMessageInMemoryMaxRadio;
    }

    public void setAccessMessageInMemoryMaxRadio(int accessMessageInMemoryMaxRadio) {
        this.accessMessageInMemoryMaxRadio = accessMessageInMemoryMaxRadio;
    }

    public boolean isMessageIndexEnable() {
        return messageIndexEnable;
    }

    public void setMessageIndexEnable(boolean messageIndexEnable) {
        this.messageIndexEnable = messageIndexEnable;
    }

    public int getMaxHashSlotNum() {
        return maxHashSlotNum;
    }

    public void setMaxHashSlotNum(int maxHashSlotNum) {
        this.maxHashSlotNum = maxHashSlotNum;
    }

    public int getMaxIndexNum() {
        return maxIndexNum;
    }

    public void setMaxIndexNum(int maxIndexNum) {
        this.maxIndexNum = maxIndexNum;
    }

    public int getMaxMsgsNumBatch() {
        return maxMsgsNumBatch;
    }

    public void setMaxMsgsNumBatch(int maxMsgsNumBatch) {
        this.maxMsgsNumBatch = maxMsgsNumBatch;
    }

    public boolean isMessageIndexSafe() {
        return messageIndexSafe;
    }

    public void setMessageIndexSafe(boolean messageIndexSafe) {
        this.messageIndexSafe = messageIndexSafe;
    }

    public int getHaListenPort() {
        return haListenPort;
    }

    public void setHaListenPort(int haListenPort) {
        this.haListenPort = haListenPort;
    }

    public int getHaSendHeartbeatInterval() {
        return haSendHeartbeatInterval;
    }

    public void setHaSendHeartbeatInterval(int haSendHeartbeatInterval) {
        this.haSendHeartbeatInterval = haSendHeartbeatInterval;
    }

    public int getHaHousekeepingInterval() {
        return haHousekeepingInterval;
    }

    public void setHaHousekeepingInterval(int haHousekeepingInterval) {
        this.haHousekeepingInterval = haHousekeepingInterval;
    }

    public int getHaTransferBatchSize() {
        return haTransferBatchSize;
    }

    public void setHaTransferBatchSize(int haTransferBatchSize) {
        this.haTransferBatchSize = haTransferBatchSize;
    }

    public String getHaMasterAddress() {
        return haMasterAddress;
    }

    public void setHaMasterAddress(String haMasterAddress) {
        this.haMasterAddress = haMasterAddress;
    }

    public int getHaSlaveFallbehindMax() {
        return haSlaveFallbehindMax;
    }

    public void setHaSlaveFallbehindMax(int haSlaveFallbehindMax) {
        this.haSlaveFallbehindMax = haSlaveFallbehindMax;
    }

    public BrokerRole getBrokerRole() {
        return brokerRole;
    }

    public void setBrokerRole(BrokerRole brokerRole) {
        this.brokerRole = brokerRole;
    }

    public FlushDiskType getFlushDiskType() {
        return flushDiskType;
    }

    public void setFlushDiskType(FlushDiskType flushDiskType) {
        this.flushDiskType = flushDiskType;
    }

    public int getSyncFlushTimeout() {
        return syncFlushTimeout;
    }

    public void setSyncFlushTimeout(int syncFlushTimeout) {
        this.syncFlushTimeout = syncFlushTimeout;
    }

    public String getMessageDelayLevel() {
        return messageDelayLevel;
    }

    public void setMessageDelayLevel(String messageDelayLevel) {
        this.messageDelayLevel = messageDelayLevel;
    }

    public long getFlushDelayOffsetInterval() {
        return flushDelayOffsetInterval;
    }

    public void setFlushDelayOffsetInterval(long flushDelayOffsetInterval) {
        this.flushDelayOffsetInterval = flushDelayOffsetInterval;
    }

    public boolean isCleanFileForciblyEnable() {
        return cleanFileForciblyEnable;
    }

    public void setCleanFileForciblyEnable(boolean cleanFileForciblyEnable) {
        this.cleanFileForciblyEnable = cleanFileForciblyEnable;
    }

    public boolean isWarmMapedFileEnable() {
        return warmMapedFileEnable;
    }

    public void setWarmMapedFileEnable(boolean warmMapedFileEnable) {
        this.warmMapedFileEnable = warmMapedFileEnable;
    }

    public boolean isOffsetCheckInSlave() {
        return offsetCheckInSlave;
    }

    public void setOffsetCheckInSlave(boolean offsetCheckInSlave) {
        this.offsetCheckInSlave = offsetCheckInSlave;
    }

    public boolean isDebugLockEnable() {
        return debugLockEnable;
    }

    public void setDebugLockEnable(boolean debugLockEnable) {
        this.debugLockEnable = debugLockEnable;
    }

    public boolean isDuplicationEnable() {
        return duplicationEnable;
    }

    public void setDuplicationEnable(boolean duplicationEnable) {
        this.duplicationEnable = duplicationEnable;
    }

    public boolean isDiskFallRecorded() {
        return diskFallRecorded;
    }

    public void setDiskFallRecorded(boolean diskFallRecorded) {
        this.diskFallRecorded = diskFallRecorded;
    }

    public long getOsPageCacheBusyTimeOutMills() {
        return osPageCacheBusyTimeOutMills;
    }

    public void setOsPageCacheBusyTimeOutMills(long osPageCacheBusyTimeOutMills) {
        this.osPageCacheBusyTimeOutMills = osPageCacheBusyTimeOutMills;
    }

    public int getDefaultQueryMaxNum() {
        return defaultQueryMaxNum;
    }

    public void setDefaultQueryMaxNum(int defaultQueryMaxNum) {
        this.defaultQueryMaxNum = defaultQueryMaxNum;
    }

    public boolean isTransientStorePoolEnable() {
        return transientStorePoolEnable;
    }

    public void setTransientStorePoolEnable(boolean transientStorePoolEnable) {
        this.transientStorePoolEnable = transientStorePoolEnable;
    }

    public int getTransientStorePoolSize() {
        return transientStorePoolSize;
    }

    public void setTransientStorePoolSize(int transientStorePoolSize) {
        this.transientStorePoolSize = transientStorePoolSize;
    }

    public boolean isFastFailIfNoBufferInStorePool() {
        return fastFailIfNoBufferInStorePool;
    }

    public void setFastFailIfNoBufferInStorePool(boolean fastFailIfNoBufferInStorePool) {
        this.fastFailIfNoBufferInStorePool = fastFailIfNoBufferInStorePool;
    }
}
