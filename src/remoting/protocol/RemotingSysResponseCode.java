package remoting.protocol;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:59
 *
 * 统一远程调用系统返回码
 */
public class RemotingSysResponseCode {

    public static int SUCCESS=0;
    //系统错误
    public static int SYSTEM_ERROR=1;
    //系统忙
    public static int SYSTEM_BUSY=2;
    //不支持的请求码
    public static int REQUEST_CODE_NOT_SUPPORTED=3;
    //连接失败
    public static final int TRANSACTION_FAILED = 4;
}
