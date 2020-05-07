package remoting.exception;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:37
 */
public class RemotingException extends Exception {
    private static final long serialVersionUID=-5690687334570505110L;

    public RemotingException(String message){
        super(message);
    }
    public RemotingException(String message,Throwable throwable){
        super(message,throwable);
    }
}
