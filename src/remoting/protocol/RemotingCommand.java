package remoting.protocol;

import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remoting.CommandCustomHeader;
import remoting.annotation.CFNotNull;
import remoting.common.RemotingHelper;
import remoting.exception.RemotingCommandException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/25 8:09
 */
public class RemotingCommand {
    public static final String SERIALIZE_TYPE_PROPERTY = "rocketmq.serialize.type";
    public static final String SERIALIZE_TYPE_ENV = "ROCKETMQ_SERIALIZE_TYPE";
    public static final String REMOTING_VERSION_KEY = "rocketmq.remoting.version";
    private static final Logger log = LoggerFactory.getLogger(RemotingHelper.ROCKETMQ_REMOTING);
    private static final int RPC_TYPE = 0; // 0, REQUEST_COMMAND
    private static final int RPC_ONEWAY = 1; // 0, RPC
    //类名和其所有的字段
    private static final Map<Class<? extends CommandCustomHeader>, Field[]> CLASS_HASH_MAP =
            new HashMap<Class<? extends CommandCustomHeader>, Field[]>();
    private static final Map<Class, String> CANONICAL_NAME_CACHE = new HashMap<Class, String>();
    // 1, Oneway
    // 1, RESPONSE_COMMAND
    private static final Map<Field, Annotation> NOT_NULL_ANNOTATION_CACHE = new HashMap<Field, Annotation>();
    private static final String STRING_CANONICAL_NAME = String.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_1 = Double.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_2 = double.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_1 = Integer.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_2 = int.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_1 = Long.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_2 = long.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_1 = Boolean.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_2 = boolean.class.getCanonicalName();
    private static volatile int configVersion = -1;
    private static AtomicInteger requestId = new AtomicInteger(0);
    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;

    //初始化序列化协议，这里有json序列化和自定义ROCKETMQ序列化方式
    static{
        final String protocol=System.getProperty(SERIALIZE_TYPE_PROPERTY,System.getenv(SERIALIZE_TYPE_ENV));
        if(!isBlank(protocol)){
            try {
                serializeTypeConfigInThisServer=SerializeType.valueOf(protocol);
            }catch (IllegalArgumentException e){
                throw new RuntimeException("parser specified protocol error. protocol="+protocol,e);
            }
        }
    }

    /**
     * 对象属性
     * 1 状态码
     */
    private int code;
    private LanguageCode language = LanguageCode.JAVA;
    private int version = 0;
    private int opaque = requestId.getAndIncrement();
    private int flag = 0;
    private String remark;
    private HashMap<String, String> extFields;
    private transient CommandCustomHeader customHeader;

    private SerializeType serializeTypeCurrentRPC = serializeTypeConfigInThisServer;

    private transient byte[] body;
    protected RemotingCommand(){}
    /**
     * 对象方法
     *
     */

    /**
     * Command编码组成
     * |----------------------dataSize(32)---------------------------------|
     * |-----protocolType(8)------|---------headSize(24) 0XFFFFFF----------|
     * |-----------------------headerData----------------------------------|
     * |-----------------------bodyData------------------------------------|
     */
    /**
     * command 编码
     * @return 字节流
     */
    public ByteBuffer encodeHeader() {
        return encodeHeader(this.body != null ? this.body.length : 0);
    }
    public ByteBuffer encodeHeader(final int bodyLength) {
        //header data
        int length = 4;

        byte[] headerData = this.headerEncode();
        length += headerData.length;
        length += body.length;

        ByteBuffer result = ByteBuffer.allocate(length);
        result.putInt(length);
        result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));
        result.put(headerData);
        result.flip();
        return result;
    }

    /**
     * 头部数据编码,由对象属性决定采用哪种编码方式
     * @return 字节数组
     */
    private byte[] headerEncode() {
        makeCustomHeaderToNet();
        if (SerializeType.ROCKETMQ == serializeTypeCurrentRPC) {
            return RocketMQSerializable.rocketMQProtocolEncode(this);
        } else {
            return RemotingSerializable.encode(this);
        }
    }

    public ByteBuffer encode() {
        int length = 4;

        //|---type----|---------length--------|
        byte[] headerData = this.headerEncode();
        length += headerData.length;

        // 3> body data length
        if (this.body != null) {
            length += body.length;
        }

        ByteBuffer result = ByteBuffer.allocate(4 + length);

        // length
        result.putInt(length);

        // header length
        result.put(markProtocolType(headerData.length, serializeTypeCurrentRPC));

        // header data
        result.put(headerData);

        // body data;
        if (this.body != null) {
            result.put(this.body);
        }

        result.flip();

        return result;
    }

    /**
     * 解码对象头部数据
     * @param classHeader
     * @return
     * @throws RemotingCommandException
     */
    public CommandCustomHeader decodeCommandCustomHeader(Class<? extends CommandCustomHeader> classHeader)
            throws RemotingCommandException{
        CommandCustomHeader header;
        try {
            header=classHeader.newInstance();
        }catch (InstantiationException e){
            return null;
        }catch (IllegalAccessException e){
            return null;
        }
        if(this.extFields!=null){
            Field[] fields=getClazzFields(classHeader);
            for(Field field:fields){
                if(!Modifier.isStatic(field.getModifiers())){
                    String name=field.getName();
                    if(!name.startsWith("this")){
                        try {
                            String value=this.extFields.get(name);
                            if(null==value){
                                Annotation annotation=getNotNullAnnotation(field);
                                if(null!=annotation){
                                    throw new RemotingCommandException("the custom field <" + name + "> is null");
                                }
                                continue;
                            }
                            field.setAccessible(true);
                            String type=getCanonicalName(field.getType());
                            Object valueParsed;
                            //只支持基本类型
                            if (type.equals(STRING_CANONICAL_NAME)) {
                                valueParsed = value;
                            } else if (type.equals(INTEGER_CANONICAL_NAME_1) || type.equals(INTEGER_CANONICAL_NAME_2)) {
                                valueParsed = Integer.parseInt(value);
                            } else if (type.equals(LONG_CANONICAL_NAME_1) || type.equals(LONG_CANONICAL_NAME_2)) {
                                valueParsed = Long.parseLong(value);
                            } else if (type.equals(BOOLEAN_CANONICAL_NAME_1) || type.equals(BOOLEAN_CANONICAL_NAME_2)) {
                                valueParsed = Boolean.parseBoolean(value);
                            } else if (type.equals(DOUBLE_CANONICAL_NAME_1) || type.equals(DOUBLE_CANONICAL_NAME_2)) {
                                valueParsed = Double.parseDouble(value);
                            } else {
                                throw new RemotingCommandException("the custom field <" + name + "> type is not supported");
                            }
                            field.set(header, valueParsed);
                        }catch (Throwable e){

                        }
                    }
                }
            }
            header.checkField();
        }
        return header;
    }

    /**
     * 把请求头的数据写入extFields用于传输
     */
    public void makeCustomHeaderToNet() {
        if (this.customHeader != null) {
            Field[] fields = getClazzFields(this.customHeader.getClass());
            if (null == this.extFields) {
                extFields = new HashMap<>();
            }
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!name.startsWith("this")) {
                        Object value = null;
                        try {
                            field.setAccessible(true);
                            value = field.get(this.customHeader);
                        } catch (IllegalArgumentException e) {

                        } catch (IllegalAccessException e) {
                        }
                        if (value != null) {
                            this.extFields.put(name, value.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取请求头的字段数据
     * @param classHeader
     * @return
     */
    private Field[] getClazzFields(Class<? extends CommandCustomHeader> classHeader){
        Field[] fields=CLASS_HASH_MAP.get(classHeader);
        if(null==fields){
            fields=classHeader.getDeclaredFields();
            synchronized (CLASS_HASH_MAP){
                CLASS_HASH_MAP.put(classHeader,fields);
            }

        }
        return fields;
    }

    /**
     * 获取具体的类名字 java.lang.String
     * @param clazz
     * @return
     */
    private String getCanonicalName(Class clazz) {
        String name = CANONICAL_NAME_CACHE.get(clazz);

        if (name == null) {
            name = clazz.getCanonicalName();
            synchronized (CANONICAL_NAME_CACHE) {
                CANONICAL_NAME_CACHE.put(clazz, name);
            }
        }
        return name;
    }

    /**
     * 获取字段有CFNotNull注解的注释
     * 并且进行cache
     * @param field 字段
     * @return
     */
    private Annotation getNotNullAnnotation(Field field) {
        Annotation annotation = NOT_NULL_ANNOTATION_CACHE.get(field);

        if (annotation == null) {
            annotation = field.getAnnotation(CFNotNull.class);
            synchronized (NOT_NULL_ANNOTATION_CACHE) {
                NOT_NULL_ANNOTATION_CACHE.put(field, annotation);
            }
        }
        return annotation;
    }

    /**
     * 对外提供的静态方法
     */

    /**
     * 创建请求命令
     * @param code 状态码
     * @param header 命令头
     * @return
     */
    public static RemotingCommand createRequestCommand(int code,CommandCustomHeader header){
        RemotingCommand cmd=new RemotingCommand();
        cmd.setCode(code);
        cmd.customHeader=header;
        setCmdVersion(cmd);
        return cmd;
    }

    public static RemotingCommand createResponseCommand(Class<? extends CommandCustomHeader> classHeader){
        return createResponseCommand(RemotingSysResponseCode.SYSTEM_ERROR,"not set any response code",classHeader);
    }

    /**
     * 创建返回指令
     * @param code 状态码
     * @param remark 标记
     * @param header 头部类
     * @return RemotingCommand
     */
    public static RemotingCommand createResponseCommand(int code,String remark,Class<? extends CommandCustomHeader> header){
        RemotingCommand cmd=new RemotingCommand();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setRemark(remark);
        setCmdVersion(cmd);
        if(null!=header){
            try {
                /**
                 * newInstance不建议使用，它会让你无法捕获类中所抛出的异常
                 * Note that this method propagates any exception thrown by the nullary constructor,
                 * including a checked exception. Use of this method effectively bypasses the
                 * compile-time exception checking that would otherwise be performed by the compiler.
                 * The Constructor.newInstance method avoids this problem by wrapping any exception
                 * thrown by the constructor in a (checked) InvocationTargetException.
                 */
                CommandCustomHeader objectHeader=header.newInstance();
                cmd.customHeader=objectHeader;
            }catch (InstantiationException e){
                log.error("error is happened when initialize CommandCustomHeader");
                return null;
            }catch (IllegalAccessException e){
                log.error("don't have Authority to the class constructor");
                return null;
            }
        }
        return cmd;
    }

    public static RemotingCommand createResponseCommand(int code,String mark){
        return  createResponseCommand(code,mark,null);
    }

    public static RemotingCommand decode(final byte[] array) {
        ByteBuffer data = ByteBuffer.wrap(array);
        return decode(data);
    }

    /**
     * 解码成RemotingCommand
     * @param byteBuffer 字节流
     * @return 解码后的请求
     */
    public static RemotingCommand decode(final ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();
        //前四个字节表示为数据包的长度
        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);
        RemotingCommand cmd = headerDecode(headerData, getProtocolType(oriHeaderLen));
        //数据长度为总长度减去前4位表示总数据长度和头部长度
        int bodyLength = length - 4 - headerLength;

        byte[] body = null;
        if (bodyLength > 0) {
            body = new byte[bodyLength];
            byteBuffer.get(body);
        }
        cmd.setBody(body);
        return cmd;
    }

    /**
     * 标记为返回类型
     */
    public void markResponseType() {
        int bits = 1 << RPC_TYPE;
        this.flag |= bits;
    }

    /**
     * 内部对象初始化工具
     */
    private static void setCmdVersion(RemotingCommand cmd) {
        if (configVersion > 0) {
            cmd.setVersion(configVersion);
        } else {
            String version = System.getProperty(REMOTING_VERSION_KEY);
            if (null != version) {
                int value = Integer.parseInt(version);
                cmd.setVersion(value);
                configVersion = value;
            } else {
                log.error("initialize REMOTING_VERSION error,please check the system variable");
            }
        }
    }
    /**
     * 内部工具方法
     */
    /**
     * 判断是否为空
     * @param str 对应的字符串
     * @return result
     */
    private static boolean isBlank(String str) {
        int strLen;
        if (null == str || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    private static RemotingCommand headerDecode(byte[] headerData,SerializeType type) {
        switch (type) {
            case JSON:
                RemotingCommand resultJson = RemotingSerializable.decode(headerData, RemotingCommand.class);
                resultJson.setSerializeTypeCurrentRPC(type);
                return resultJson;
            case ROCKETMQ:
                RemotingCommand result = RocketMQSerializable.rocketMQProtocolDecode(headerData);
                result.setSerializeTypeCurrentRPC(type);
        }
        return null;
    }
    /**
     * 获取协议类型 JSON或ROCKETMQ
     * @param source 数据包的前四个字节为源数据，第一个字节为协议类型
     * @return
     */
    private static SerializeType getProtocolType(int source) {
        return SerializeType.valusOf((byte) ((source >> 24) & 0XFF));
    }
    /**
     * 获取头部长度，注意这里最大长度为2^24,也就是后三个字节代表头部数据的长度
     * @param length 长度
     * @return 转换后的长度
     */
    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    /**
     * 标记协议类型以及头部长度
     * @param source header data size
     * @param type JSON 0 ROCKET 1
     * @return |------type(8)------|--------------------------headerSize(24)------------------------------|
     */
    public static byte[] markProtocolType(int source, SerializeType type) {
        byte[] result = new byte[4];

        result[0] = type.getCode();
        result[1] = (byte) ((source >> 16) & 0xFF);
        result[2] = (byte) ((source >> 8) & 0xFF);
        result[3] = (byte) (source & 0xFF);
        return result;
    }
    /**
     * getter and setter method
     */

    public CommandCustomHeader readCustomHeader() {
        return customHeader;
    }

    public void writeCustomHeader(CommandCustomHeader customHeader) {
        this.customHeader = customHeader;
    }
    /**
     * 标记为oneWay RPC调用，该调用不需要回应
     * bits=0001->0010
     * flag=??1?
     * 第二位为0则表示为oneWay调用
     */
    public void markOnewayRPC() {
        int bits = 1 << RPC_ONEWAY;
        this.flag |= bits;
    }

    /**
     * 是否为onway RPC调用
     * bits = 0010
     * 判断第二位是否为1
     * @return
     */
    @JSONField(serialize = false)
    public boolean isOnewayRPC() {
        int bits = 1 << RPC_ONEWAY;
        return (this.flag & bits) == bits;
    }

    @JSONField(serialize = false)
    public RemotingCommandType getType() {
        if (this.isResponseType()) {
            return RemotingCommandType.RESPONSE_COMMAND;
        }

        return RemotingCommandType.REQUEST_COMMAND;
    }

    /**
     * 判断是否为响应类型
     * bits=0001,第一位为1则是返回类型
     * @return
     */
    @JSONField(serialize = false)
    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LanguageCode getLanguage() {
        return language;
    }

    public void setLanguage(LanguageCode language) {
        this.language = language;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public HashMap<String, String> getExtFields() {
        return extFields;
    }

    public void setExtFields(HashMap<String, String> extFields) {
        this.extFields = extFields;
    }

    public void addExtField(String key, String value) {
        if (null == extFields) {
            extFields = new HashMap<String, String>();
        }
        extFields.put(key, value);
    }

    @Override
    public String toString() {
        return "RemotingCommand [code=" + code + ", language=" + language + ", version=" + version + ", opaque=" + opaque + ", flag(B)="
                + Integer.toBinaryString(flag) + ", remark=" + remark + ", extFields=" + extFields + ", serializeTypeCurrentRPC="
                + serializeTypeCurrentRPC + "]";
    }

    public SerializeType getSerializeTypeCurrentRPC() {
        return serializeTypeCurrentRPC;
    }

    public void setSerializeTypeCurrentRPC(SerializeType serializeTypeCurrentRPC) {
        this.serializeTypeCurrentRPC = serializeTypeCurrentRPC;
    }
}
