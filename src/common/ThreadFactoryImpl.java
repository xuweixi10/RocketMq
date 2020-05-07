package common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/24 9:29
 */
public class ThreadFactoryImpl implements ThreadFactory {
    private final AtomicInteger threadIndex=new AtomicInteger(0);
    private final String threadNamePrefix;
    public ThreadFactoryImpl(final String threadNamePrefix){
        this.threadNamePrefix=threadNamePrefix;
    }
    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable,threadNamePrefix+this.threadIndex.incrementAndGet());
    }
}
