package common.test;

public class MqConfiguration {
    private static String hostName;
    private static String listen;
    public MqConfiguration(){

    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        MqConfiguration.hostName = hostName;
    }

    public String getListen() {
        return listen;
    }

    public void setListen(String listen) {
        MqConfiguration.listen = listen;
    }
}
