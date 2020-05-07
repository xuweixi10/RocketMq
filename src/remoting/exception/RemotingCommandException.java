package remoting.exception;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:55
 */
public class RemotingCommandException extends RemotingException {
    private static final long serialVersionUID = -6061365915274953096L;
    public RemotingCommandException(String message){
        super(message);
    }
    public RemotingCommandException(String message,Throwable throwable){
        super(message,throwable);
    }

}
