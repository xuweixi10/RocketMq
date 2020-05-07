package common.stats;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/28 11:23
 * 状态快照，包括状态统计，系统吞吐量，
 */
public class StatsSnapshot {
    private long sum;
    private double tps;
    private double avgpt;

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public double getAvgpt() {
        return avgpt;
    }

    public void setAvgpt(double avgpt) {
        this.avgpt = avgpt;
    }
}
