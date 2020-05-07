package remoting;

import remoting.exception.RemotingCommandException;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:53
 */
public interface CommandCustomHeader {
    /**
     *
     * @throws RemotingCommandException
     */
    void checkField() throws RemotingCommandException;
}
