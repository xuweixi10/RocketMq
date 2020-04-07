package broker.offset;

import broker.BrokerController;
import common.ConfigManager;
import common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/2 21:58
 */
public class ConsumerOffsetManager extends ConfigManager {
    private static final Logger log= LoggerFactory.getLogger(LoggerName.BROKER_LOGGER_NAME);
    private static final String TOPIC_GROUP_SEPARATOR="@";
    private ConcurrentHashMap<String,ConcurrentHashMap<Integer,Long>> offsetTable=
            new ConcurrentHashMap<String, ConcurrentHashMap<Integer, Long>>(512);

    private transient BrokerController brokerController;

    public ConsumerOffsetManager(){

    }
    public ConsumerOffsetManager(BrokerController brokerController){
        this.brokerController=brokerController;
    }
    public void scanUnsubscribedTopic(){
        Iterator<Entry<String,ConcurrentHashMap<Integer,Long>>> it=this.offsetTable.entrySet().iterator();
        while (it.hasNext()){
            Entry<String,ConcurrentHashMap<Integer,Long>> next=it.next();
            String topicAtGroup = next.getKey();
            String[] arrays = topicAtGroup.split(TOPIC_GROUP_SEPARATOR);
            if(arrays.length==2){
                String topic = arrays[0];
                String group = arrays[1];
            }
        }
    }
    @Override
    public String encode() {
        return null;
    }

    @Override
    public String configFilePath() {
        return null;
    }

    @Override
    public void decode(String jsonString) {

    }

    @Override
    public String encode(boolean prettyFormat) {
        return null;
    }
}
