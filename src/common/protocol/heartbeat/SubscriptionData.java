package common.protocol.heartbeat;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/2 22:54
 */
public class SubscriptionData implements Comparable<SubscriptionData> {
    public final static String SUB_ALL="*";
    //是否启用消息过滤机制
    private boolean classFilterMode=false;
    //订阅节点
    private String topic;
    //具体节点
    private String subString;
    //标签
    private Set<String> tagsSet = new HashSet<String>();
    //编码
    private Set<Integer> codeSet = new HashSet<Integer>();
    //版本控制
    private long subVersion = System.currentTimeMillis();

    @JSONField(serialize = false)
    private String filterClassSource;

    public SubscriptionData(){

    }
    public SubscriptionData(String topic,String subString){
        super();
        this.topic=topic;
        this.subString=subString;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (classFilterMode ? 1231 : 1237);
        result = prime * result + ((codeSet == null) ? 0 : codeSet.hashCode());
        result = prime * result + ((subString == null) ? 0 : subString.hashCode());
        result = prime * result + ((tagsSet == null) ? 0 : tagsSet.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj){
        if(null==obj){
            return false;
        }
        if(getClass()!=obj.getClass()){
            return false;
        }
        if(this==obj){
            return true;
        }
        SubscriptionData other=(SubscriptionData)obj;
        if (classFilterMode != other.classFilterMode){
            return false;
        }
        if (codeSet == null) {
            if (other.codeSet != null){
                return false;}
        } else if (!codeSet.equals(other.codeSet)){
            return false;}
        if (subString == null) {
            if (other.subString != null){
                return false;}
        } else if (!subString.equals(other.subString)){
            return false;}
        if (subVersion != other.subVersion){
            return false;}
        if (tagsSet == null) {
            if (other.tagsSet != null){
                return false;}
        } else if (!tagsSet.equals(other.tagsSet)){
            return false;}
        if (topic == null) {
            if (other.topic != null){
                return false;}
        } else if (!topic.equals(other.topic)){
            return false;
        }

        return true;
    }
    @Override
    public String toString() {
        return "SubscriptionData [classFilterMode=" + classFilterMode + ", topic=" + topic + ", subString="
                + subString + ", tagsSet=" + tagsSet + ", codeSet=" + codeSet + ", subVersion=" + subVersion
                + "]";
    }

    @Override
    public int compareTo(SubscriptionData other) {
        String thisValue = this.topic + "@" + this.subString;
        String otherValue = this.topic + "@" + this.subString;
        return thisValue.compareTo(otherValue);
    }

    public boolean isClassFilterMode() {
        return classFilterMode;
    }

    public void setClassFilterMode(boolean classFilterMode) {
        this.classFilterMode = classFilterMode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubString() {
        return subString;
    }

    public void setSubString(String subString) {
        this.subString = subString;
    }

    public Set<String> getTagsSet() {
        return tagsSet;
    }

    public void setTagsSet(Set<String> tagsSet) {
        this.tagsSet = tagsSet;
    }

    public Set<Integer> getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(Set<Integer> codeSet) {
        this.codeSet = codeSet;
    }

    public long getSubVersion() {
        return subVersion;
    }

    public void setSubVersion(long subVersion) {
        this.subVersion = subVersion;
    }

    public String getFilterClassSource() {
        return filterClassSource;
    }

    public void setFilterClassSource(String filterClassSource) {
        this.filterClassSource = filterClassSource;
    }
}
