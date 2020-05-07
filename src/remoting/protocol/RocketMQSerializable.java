package remoting.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  ----------------------------------------------------------
 * |-----code(16)-------|-language(8)-|-----version(16)-----|
 * |-----------------opaque(32)------------------|
 * |-----------------flag(32)--------------------|
 * |--------------remarkLength(32)---------------|
 * |-----------------remark-----------------.....
 * ..............................................|
 * |-------------extFieldsLength(32)-------------|
 * |-----------------extField------------........
 * ..............................................
 * ----------------------------------------------|
 * ------------------------------------------------------------
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 9:19
 */
public class RocketMQSerializable {
    private final static Charset CHARSET_UTF8=Charset.forName("UTF-8");


    /**
     * MQ协议编码
     * @param cmd 远程命令
     * @return 字节流
     */
    public static byte[] rocketMQProtocolEncode(RemotingCommand cmd){
        byte[] remarkBytes = null;
        int remarkLen=0;
        if(cmd.getRemark()!=null&&cmd.getRemark().length()>0){
            remarkBytes=cmd.getRemark().getBytes(CHARSET_UTF8);
            remarkLen=remarkBytes.length;
        }
        byte[] extFieldsBytes=null;
        int extFieldLength=0;
        if(cmd.getExtFields()!=null&&!cmd.getExtFields().isEmpty()){
            extFieldsBytes=mapSerialize(cmd.getExtFields());
            extFieldLength=extFieldsBytes.length;
        }
        int totalLen=calTotalLen(remarkLen,extFieldLength);
        ByteBuffer byteBuffer=ByteBuffer.allocate(totalLen);
        byteBuffer.putShort((short)cmd.getCode());
        byteBuffer.put(cmd.getLanguage().getCode());
        byteBuffer.putShort((short)cmd.getVersion());
        byteBuffer.putInt(cmd.getOpaque());
        byteBuffer.putInt(cmd.getFlag());

        if(remarkBytes!=null){
            byteBuffer.putInt(remarkLen);
            byteBuffer.put(remarkBytes);
        }else {
            byteBuffer.putInt(0);
        }
        if(extFieldsBytes!=null){
            byteBuffer.putInt(extFieldLength);
            byteBuffer.put(extFieldsBytes);
        }else {
            byteBuffer.putInt(0);
        }

        return byteBuffer.array();
    }

    /**
     * MQ协议解码
     * @param headData 头部数据
     * @return RemotingCommand
     */
    public static RemotingCommand rocketMQProtocolDecode(byte[] headData){
        RemotingCommand cmd=new RemotingCommand();
        ByteBuffer byteBuffer=ByteBuffer.wrap(headData);
        cmd.setCode(byteBuffer.getShort());
        cmd.setLanguage(LanguageCode.valueOf(byteBuffer.get()));
        cmd.setVersion(byteBuffer.getShort());
        cmd.setOpaque(byteBuffer.getInt());
        cmd.setFlag(byteBuffer.getInt());
        int remarkLength=byteBuffer.getInt();
        if(remarkLength>0){
            byte[] remark=new byte[remarkLength];
            byteBuffer.get(remark);
            cmd.setRemark(new String(remark,CHARSET_UTF8));
        }

        int extFieldsLength=byteBuffer.getInt();
        if(extFieldsLength>0){
            byte[] extField=new byte[extFieldsLength];
            byteBuffer.get(extField);
            cmd.setExtFields(mapDeserialize(extField));
        }
        return cmd;
    }

    /**
     * calculate total length
     * @param remark remark length
     * @param ext extra length
     * @return total length
     */
    private static int calTotalLen(int remark, int ext) {
        // int code(~32767)
        int length = 2
                // LanguageCode language
                + 1
                // int version(~32767)
                + 2
                // int opaque
                + 4
                // int flag
                + 4
                // String remark
                + 4 + remark
                // HashMap<String, String> extFields
                + 4 + ext;

        return length;
    }

    /**
     * hashMap序列化
     * |------keySize(16)-------|----key-----..........|
     * |------------valueSize(32)----------------|-----------........|
     * @param map
     * @return
     */
    private static byte[] mapSerialize(HashMap<String,String> map){
        if (map == null||map.isEmpty()) {
            return null;
        }
        int totalLength=0;
        int kvLength;
        Iterator<Map.Entry<String,String>> it=map.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,String> next=it.next();
            kvLength=2+next.getKey().getBytes(CHARSET_UTF8).length
                    +4+next.getValue().getBytes(CHARSET_UTF8).length;
            totalLength+=kvLength;
        }
        ByteBuffer content=ByteBuffer.allocate(totalLength);
        it=map.entrySet().iterator();
        byte[] key;
        byte[] value;
        while (it.hasNext()){
            Map.Entry<String,String> next=it.next();
            key=next.getKey().getBytes(CHARSET_UTF8);
            value=next.getValue().getBytes(CHARSET_UTF8);
            content.putShort((short)key.length);
            content.put(key);

            content.putInt(value.length);
            content.put(value);

        }
        return content.array();
    }

    /**
     * hashMap反序列化
     * @param data 字节流
     * @return HashMap
     */
    private static HashMap<String,String> mapDeserialize(byte[] data){
        if(data==null||data.length<=0){
            return null;
        }
        ByteBuffer buffer=ByteBuffer.wrap(data);

        HashMap<String,String> extdata=new HashMap<>();
        short keySize;
        byte[] key;
        int valueSzie;
        byte[] value;
        while (buffer.hasRemaining()){
            keySize=buffer.getShort();
            key=new byte[keySize];
            buffer.get(key);
            valueSzie=buffer.getInt();
            value=new byte[valueSzie];
            buffer.get(value);

            extdata.put(new String(key,CHARSET_UTF8),new String(value,CHARSET_UTF8));
        }
        return extdata;
    }
}
