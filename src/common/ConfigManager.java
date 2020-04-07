package common;

import common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/2 21:38
 */
public abstract class ConfigManager {
    private static final Logger PLOG= LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    public abstract String encode();

    /**
     * 配置文件路径
     * @return 配置文件路径
     */
    public abstract String configFilePath();
    public boolean load(){
        String fileName = null;
        try {
            fileName = this.configFilePath();
            String jsonString = MixAll.file2String(fileName);

            if (null == jsonString || jsonString.length() == 0) {
                return this.loadBak();
            } else {
                this.decode(jsonString);
                PLOG.info("load {} OK", fileName);
                return true;
            }
        } catch (Exception e) {
            PLOG.error("load " + fileName + " Failed, and try to load backup file", e);
            return this.loadBak();
        }
    }

    private boolean loadBak(){
        String fileName = null;
        try {
            fileName = this.configFilePath();
            String jsonString = MixAll.file2String(fileName + ".bak");
            if (jsonString != null && jsonString.length() > 0) {
                this.decode(jsonString);
                PLOG.info("load " + fileName + " OK");
                return true;
            }
        } catch (Exception e) {
            PLOG.error("load " + fileName + " Failed", e);
            return false;
        }

        return true;
    }

    /**
     * 解码
     */
    public abstract void decode(final String jsonString);

    public synchronized void persist() {
        String jsonString = this.encode(true);
        if (jsonString != null) {
            String fileName = this.configFilePath();
            try {
                MixAll.string2File(jsonString, fileName);
            } catch (IOException e) {
                PLOG.error("persist file Exception, " + fileName, e);
            }
        }
    }
    public abstract String encode(final boolean prettyFormat);
}
