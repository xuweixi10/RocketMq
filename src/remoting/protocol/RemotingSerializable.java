package remoting.protocol;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 9:22
 * 序列化类型为JSON时
 */
public class RemotingSerializable {
    private final static Charset CHARSET_UTF8=Charset.forName("UTF-8");

    /**
     * 编码成json数据，UTF-8的编码
     * @param object 编码对象
     * @return 字节数组
     */
    public static byte[] encode(final Object object) {
        final String json = toJson(object, false);
        if (null != json) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static <T> T decode(byte[] data,Class<T> classOfT){
        final String json=new String(data,CHARSET_UTF8);
        return fromJson(json,classOfT);
    }

    public byte[] encode(){
        final String json=this.toString();
        if(null!=json){
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static String toJson(final Object object,boolean prettyFormat){
        return JSON.toJSONString(object,prettyFormat);
    }
    public String toJson(){
        return toJson(false);
    }
    public String toJson(final boolean prettyFormat){
        return toJson(this,prettyFormat);
    }

    /**
     * json数据转换成对象
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json,Class<T> classOfT){
        return JSON.parseObject(json,classOfT);
    }
}
