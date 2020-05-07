package common.stats;

import common.UtilAll;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/28 10:35
 */
public class MomentStatsItem {
    private final AtomicLong value=new AtomicLong(0);

    private final String statsName;
    private final String statsKey;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Logger log;

    public MomentStatsItem(String statsName, String statsKey, ScheduledExecutorService scheduledExecutorService, Logger log) {
        this.statsName = statsName;
        this.statsKey = statsKey;
        this.scheduledExecutorService = scheduledExecutorService;
        this.log = log;
    }

    public void init(){
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    printAtMinutes();

                    MomentStatsItem.this.value.set(0);
                }catch (Throwable e){

                }
            }
            //一小时后开始，每五分钟打印一次状态
        },Math.abs(UtilAll.computNextHourTimeMillis()-System.currentTimeMillis()),1000*60*5, TimeUnit.MILLISECONDS);
    }

    public void printAtMinutes() {
        log.info(String.format("[%s] [%s] Stats Every 5 Minutes, Value: %d",
                this.statsName,
                this.statsKey,
                this.value.get()));
    }
    public AtomicLong getValue() {
        return value;
    }

    public String getStatsKey() {
        return statsKey;
    }

    public String getStatsName() {
        return statsName;
    }
}
