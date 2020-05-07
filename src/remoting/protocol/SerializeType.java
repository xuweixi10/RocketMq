package remoting.protocol;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:27
 * 序列化类型，分为JSON和ROCKETMQ两种
 */
public enum SerializeType {
    JSON((byte) 0),
    ROCKETMQ((byte) 1);
    private byte code;
    SerializeType(byte code){
        this.code=code;
    }
    public static SerializeType valusOf(byte code){
        for(SerializeType serializeType:SerializeType.values()){
            if(code==serializeType.getCode()){
                return serializeType;
            }
        }
        return null;
    }


    public byte getCode() {
        return code;
    }
}
