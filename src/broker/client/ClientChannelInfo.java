package broker.client;

import io.netty.channel.Channel;
import remoting.protocol.LanguageCode;

import java.util.Objects;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/4 22:42
 */
public class ClientChannelInfo {
    private final Channel channel;
    private final String clientId;
    private final LanguageCode language;
    private final int version;
    private volatile long lastUpdateTimestamp = System.currentTimeMillis();

    public ClientChannelInfo(Channel channel){
        this(channel,null,null,0);
    }
    public ClientChannelInfo(Channel channel,String clientId,LanguageCode language,int version){
        this.channel=channel;
        this.clientId=clientId;
        this.language=language;
        this.version=version;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public LanguageCode getLanguage() {
        return language;
    }

    public int getVersion() {
        return version;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientChannelInfo that = (ClientChannelInfo) o;
        return version == that.version &&
                lastUpdateTimestamp == that.lastUpdateTimestamp &&
                channel.equals(that.channel) &&
                clientId.equals(that.clientId) &&
                language == that.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, clientId, language, version, lastUpdateTimestamp);
    }

    @Override
    public String toString() {
        return "ClientChannelInfo [channel=" + channel + ", clientId=" + clientId + ", language=" + language
                + ", version=" + version + ", lastUpdateTimestamp=" + lastUpdateTimestamp + "]";
    }
}
