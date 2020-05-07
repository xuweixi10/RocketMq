package common.stats;

import common.UtilAll;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/28 10:54
 */
public class MomentStatsItemSet {
    //key-data
    private final ConcurrentHashMap<String,MomentStatsItem> statsItemTable=new ConcurrentHashMap<>(128);
    private final String statName;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Logger log;

    public MomentStatsItemSet(String statName, ScheduledExecutorService scheduledExecutorService, Logger log) {
        this.statName = statName;
        this.scheduledExecutorService = scheduledExecutorService;
        this.log = log;
        this.init();
    }

    /**
     * 初始化，打印所有statsItem状态
     */
    private void init() {

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    printAtMinutes();
                } catch (Throwable ignored) {
                }
            }
        }, Math.abs(UtilAll.computNextMinutesTimeMillis() - System.currentTimeMillis()), 1000 * 60 * 5, TimeUnit.MILLISECONDS);
    }

    private void printAtMinutes() {
        Iterator<Map.Entry<String, MomentStatsItem>> it = this.statsItemTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MomentStatsItem> next = it.next();
            next.getValue().printAtMinutes();
        }
    }
    public void setValue(final String statsKey,final int value){
        MomentStatsItem item=getAndCreateStatsItem(statsKey);
        item.getValue().set(value);
    }

    public MomentStatsItem getAndCreateStatsItem(final String statsKey){
        MomentStatsItem item=this.statsItemTable.get(statsKey);
        if(null==item){
            item=new MomentStatsItem(this.statName,statsKey,this.scheduledExecutorService,log);
            MomentStatsItem prev=this.statsItemTable.put(statsKey,item);
            if(null==prev){

            }
        }
        return item;
    }
}
