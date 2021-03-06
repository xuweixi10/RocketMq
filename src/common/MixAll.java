package common;

import common.annotation.ImportantField;
import common.help.FAQURL;
import org.slf4j.Logger;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MixAll {
    public static final String ROCKETMQ_HOME_ENV = "ROCKETMQ_HOME";
    public static final String ROCKETMQ_HOME_PROPERTY = "rocketmq.home.dir";
    public static final String NAMESRV_ADDR_ENV = "NAMESRV_ADDR";
    public static final String NAMESRV_ADDR_PROPERTY = "rocketmq.namesrv.addr";
    public static final String MESSAGE_COMPRESS_LEVEL = "rocketmq.message.compressLevel";
    public static final String DEFAULT_NAMESRV_ADDR_LOOKUP = "jmenv.tbsite.net";
    public static final String WS_DOMAIN_NAME = System.getProperty("rocketmq.namesrv.domain", DEFAULT_NAMESRV_ADDR_LOOKUP);
    public static final String WS_DOMAIN_SUBGROUP = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
    // http://jmenv.tbsite.net:8080/rocketmq/nsaddr
    public static final String WS_ADDR = "http://" + WS_DOMAIN_NAME + ":8080/rocketmq/" + WS_DOMAIN_SUBGROUP;
    public static final String DEFAULT_TOPIC = "TBW102";
    public static final String BENCHMARK_TOPIC = "BenchmarkTest";
    public static final String DEFAULT_PRODUCER_GROUP = "DEFAULT_PRODUCER";
    public static final String DEFAULT_CONSUMER_GROUP = "DEFAULT_CONSUMER";
    public static final String TOOLS_CONSUMER_GROUP = "TOOLS_CONSUMER";
    public static final String FILTERSRV_CONSUMER_GROUP = "FILTERSRV_CONSUMER";
    public static final String MONITOR_CONSUMER_GROUP = "__MONITOR_CONSUMER";
    public static final String CLIENT_INNER_PRODUCER_GROUP = "CLIENT_INNER_PRODUCER";
    public static final String SELF_TEST_PRODUCER_GROUP = "SELF_TEST_P_GROUP";
    public static final String SELF_TEST_CONSUMER_GROUP = "SELF_TEST_C_GROUP";
    public static final String SELF_TEST_TOPIC = "SELF_TEST_TOPIC";
    public static final String OFFSET_MOVED_EVENT = "OFFSET_MOVED_EVENT";
    public static final String ONS_HTTP_PROXY_GROUP = "CID_ONS-HTTP-PROXY";
    public static final String CID_ONSAPI_PERMISSION_GROUP = "CID_ONSAPI_PERMISSION";
    public static final String CID_ONSAPI_OWNER_GROUP = "CID_ONSAPI_OWNER";
    public static final String CID_ONSAPI_PULL_GROUP = "CID_ONSAPI_PULL";
    public static final String CID_RMQ_SYS_PREFIX = "CID_RMQ_SYS_";

    //public static final List<String> LOCAL_INET_ADDRESS = getLocalInetAddress();
    public static final String LOCALHOST = localhost();
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final long MASTER_ID = 0L;
    public static final long CURRENT_JVM_PID = getPID();

    public static final String RETRY_GROUP_TOPIC_PREFIX = "%RETRY%";

    public static final String DLQ_GROUP_TOPIC_PREFIX = "%DLQ%";
    public static final String SYSTEM_TOPIC_PREFIX = "rmq_sys_";
    public static final String UNIQUE_MSG_QUERY_FLAG = "_UNIQUE_KEY_QUERY";
    public static final String DEFAULT_TRACE_REGION_ID = "DefaultRegion";
    public static final String CONSUME_CONTEXT_TYPE = "ConsumeContextType";

    /**获取重试的Topic
     *
     * @param consumerGroup
     * @return
     */
    public static String getRetryTopic(final String consumerGroup){
        return RETRY_GROUP_TOPIC_PREFIX+consumerGroup;
    }
    //判断是否为系统消费组
    public static boolean isSysConsumerGroup(final String consumerGroup){
        return consumerGroup.startsWith(CID_RMQ_SYS_PREFIX);
    }
    //判断是否为系统订阅节点
    public static boolean isSystemTopic(final String topic) {
        return topic.startsWith(SYSTEM_TOPIC_PREFIX);
    }

    //获取死信消息
    public static String getDLQTopic(final String consumerGroup) {
        return DLQ_GROUP_TOPIC_PREFIX + consumerGroup;
    }
    //获取broker 的VIP通道 如果改变了就把端口数减2
    public static String brokerVIPChannel(final boolean isChange,final String brokerAddr){
        if(isChange){
            String[] ipAndPort=brokerAddr.split(":");
            String brokerAddrNew = ipAndPort[0] + ":" + (Integer.parseInt(ipAndPort[1]) - 2);
            return brokerAddrNew;
        }
        else{
            return brokerAddr;
        }
    }
    //获取进程ID
    public static long getPID(){
        //结构为 进程id@+机器名
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

    /**
     * 创建BrokerId
     * @param ip broker所在的ip或域名
     * @param port 对应的端口
     * @return brokerId 一个64位的地址
     */
    public static long createBrokerId(final String ip,final int port){
        InetSocketAddress inetSocketAddress=new InetSocketAddress(ip,port);
        byte[] ipArray=inetSocketAddress.getAddress().getAddress();
        //从堆空间中分配一个容量大小为8的HeapByteBuffer数组作为缓冲区的byte数据存储器
        ByteBuffer byteBuffer=ByteBuffer.allocate(8);
        //四个字节加int四个字节刚好是8
        byteBuffer.put(ipArray);
        byteBuffer.putInt(port);
        long value = byteBuffer.getLong(0);
        return Math.abs(value);
    }

    /**
     * 把字符串转换为文字
     * @param str 字符串
     * @param fileName 文件名
     * @throws IOException
     */
    public static void string2File(final String str,final String fileName)throws IOException {
        //创建临时文件.tmp
        String tmpFile=fileName+".tmp";
        string2FileNotSafe(str,tmpFile);
        //写入备份文件
        String bakFile=fileName+".bak";
        String prevContent=file2String(fileName);
        if(prevContent!=null){
            string2FileNotSafe(prevContent,bakFile);
        }
        //删除上次临时文件
        File file=new File(fileName);
        file.delete();
        //修改新的临时文件 fileName.tmp to fileName
        file=new File(tmpFile);
        file.renameTo(new File(fileName));

    }

    /**
     * 字符串转文字不安全的实现
     * @param str 字符串
     * @param fileName 文件名
     * @throws IOException
     */
    public static void string2FileNotSafe(final String str,final String fileName) throws IOException{
        File file=new File(fileName);
        /**
         * 判断输入的文件名是否包含路径，如果是个文件名则会在直接在项目根目录下创建一个文件,且会覆盖存在的相同文件
         * 如果路径包含路径，则会先创建前面的路径
         * Example dir1/test first create directory dir1 in program root directory(relative directory)
         * if the path is absolute directory will create directory form root(like E:/test/test.txt)
         */

        File fileParent=file.getParentFile();
        if(fileParent!=null){
            boolean mkdir = fileParent.mkdir();
        }
        FileWriter fileWriter=null;
        try {
            fileWriter=new FileWriter(file);
            fileWriter.write(str);
        }catch (IOException e){
            throw e;
        }finally {
            if(fileWriter!=null){
                fileWriter.close();
            }
        }
    }

    /**
     * 读取文件内容的输出为字符串
     * @param fileName 文件名
     * @return
     */
    public static String file2String(final String fileName){
        File file=new File(fileName);
        return file2String(file);
    }
    public static String file2String(final File file){
        if(file.exists()){
            //获取文件内容长度
            char[] data=new char[(int)file.length()];
            boolean result=false;
            FileReader fileReader=null;
            try {
                fileReader=new FileReader(file);
                int len=fileReader.read(data);
                //判断是否完全读取
                result = len == data.length;
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                //关闭文件阅读器
                if(fileReader!=null){
                    try {
                        fileReader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            if(result){
                //装换为字符串输出
                return new String(data);
            }
        }
        return null;
    }

    /**
     * 获取网络地址，远程资源传输
     * @param url 资源地址
     * @return
     */
    public static String file2String(final URL url){
        InputStream in=null;
        try {
            //打开连接，且不使用缓存
            URLConnection urlConnection=url.openConnection();
            urlConnection.setUseCaches(false);
            //数据注入到inputStream
            in=urlConnection.getInputStream();
            int len=in.available();
            byte[] data=new byte[len];
            //数据读入data,返回UTF-8编码的字符串
            in.read(data,0,len);
            return new String(data,"UTF-8");
        }catch (IOException ignored){
        }finally {
            if(null != in){
                try {
                    in.close();
                }catch (IOException ignored){
                }
            }
        }
        return null;
    }

    /**
     * 获取一个类的路径
     * @param c 一个类 example.class
     * @return
     */
    public static String findClassPath(Class<?> c){
        URL url=c.getProtectionDomain().getCodeSource().getLocation();
        return url.getPath();
    }

    public static void printObjectProperties(final Logger logger,final Object object){
        printObjectProperties(logger,object,false);
    }

    /**
     * log打印对象配置 1 getDeclaredFields 获取Field(public private protected)
     * @param log 日志对象
     * @param object 对象
     * @param onlyImportantField 是否为 ImportantField
     */
    public static void printObjectProperties(final Logger log,final Object object,final boolean onlyImportantField){
        Field[] fields=object.getClass().getDeclaredFields();
        for(Field field:fields){
            //判断是否为静态变量
            if(Modifier.isStatic(field.getModifiers())){
                String name=field.getName();
                //不允许this开头的静态字段
                if(!name.startsWith("this")){
                    Object value=null;
                    try {
                        //设置为可见
                        field.setAccessible(true);
                        value=field.get(object);
                        if(null==value){
                            value="";
                        }
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }



                    if(log!=null){
                        log.info(name + "=" + value);
                    }
                }
            }
        }
    }

    /**
     * 配置文件转换为字符串
     * <K,V> => K=V
     * @param properties 所有属性
     * @return
     */
    public static String properties2String(final Properties properties){
        StringBuffer sb=new StringBuffer();
        for(Map.Entry<Object,Object> entry:properties.entrySet()){
            if(null!=entry.getValue()){
                sb.append(entry.getKey().toString()+"="+entry.getValue().toString()+"\n");
            }
        }
        return sb.toString();
    }

    /**
     * 把字符串转换为可配置的属性
     * @param str 字符串
     * @return 转换后的配置属性
     */
    public static Properties string2Properties(final String str){
        Properties properties=new Properties();
        try {

            InputStream in=new ByteArrayInputStream(str.getBytes(DEFAULT_CHARSET));
            properties.load(in);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        return properties;
    }

    public static Properties object2Properties(final Object object){
        Properties properties=new Properties();
        Field[] fields=object.getClass().getDeclaredFields();
        for(Field field:fields){
            if(Modifier.isStatic(field.getModifiers())){
                String name=field.getName();
                if(!name.startsWith("this")){
                    Object value=null;
                    try {
                        field.setAccessible(true);
                        value=field.get(object);
                    }catch (IllegalArgumentException | IllegalAccessException e){
                        e.printStackTrace();
                    }

                    if(value!=null){
                        properties.setProperty(name,value.toString());
                    }
                }
            }
        }
        return properties;
    }

    /**
     * 将参数注入到类的set方法中，完成参数的初始化
     * @param properties
     * @param object
     */
    public static void properties2Object(final Properties properties,final Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String mn = method.getName();
            if (mn.startsWith("set")) {
                try {
                    //判断是否以set开头
                    String tmp = mn.substring(4);
                    String first = mn.substring(3, 4);
                    //setArgs转换为args
                    String key = first.toLowerCase() + tmp;

                    String property = properties.getProperty(key);
                    if (property != null) {
                        //获取方法的参数列表
                        Class<?>[] pt = method.getParameterTypes();
                        if (pt != null && pt.length > 0) {
                            //第一个参数，
                            String cn = pt[0].getSimpleName();
                            Object arg = null;
                            if (cn.equals("int") || cn.equals("Integer")) {
                                arg = Integer.parseInt(property);
                            } else if (cn.equals("long") || cn.equals("Long")) {
                                arg = Long.parseLong(property);
                            } else if (cn.equals("double") || cn.equals("Double")) {
                                arg = Double.parseDouble(property);
                            } else if (cn.equals("boolean") || cn.equals("Boolean")) {
                                arg = Boolean.parseBoolean(property);
                            } else if (cn.equals("float") || cn.equals("Float")) {
                                arg = Float.parseFloat(property);
                            } else if (cn.equals("String")) {
                                arg = property;
                            } else {
                                continue;
                            }
                            method.invoke(object, arg);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * 判断两个属性对象是否相等
     * @param p1 属性1
     * @param p2 属性2
     * @return 是否相等
     */
    public static boolean isPropertiesEqual(final Properties p1, final Properties p2) {
        return p1.equals(p2);
    }

    /**
     * 获取本机的网络地址
     * @return 本机网络地址
     */
    public static List<String> getLocalInetAddress(){
        List<String> inetAddressList=new ArrayList<String>();
        try {
            //获取本机所有的网络接口
            Enumeration<NetworkInterface> enumeration=NetworkInterface.getNetworkInterfaces();
            while(enumeration.hasMoreElements()){
                NetworkInterface networkInterface=enumeration.nextElement();
                Enumeration<InetAddress> addres=networkInterface.getInetAddresses();
                while (addres.hasMoreElements()){
                    //主机名传入
                    inetAddressList.add(addres.nextElement().getHostName());
                }
            }
        }catch (SocketException e){
            throw new RuntimeException("get local inet Address fail",e);
        }
        return inetAddressList;
    }

    /**
     * 判断是否为本机地址
     * @param address 网络地址
     * @return
    public static boolean isLocalAddr(String address){
        for(String addr:LOCAL_INET_ADDRESS){
            if(address.contains(addr)){
                return true;
            }
        }
        return false;
    }
    */

    /**
     * 确保target只能增长
     * @param target 目标
     * @param value 值
     * @return 是否增长
     */
    public static boolean compareAndIncreaseOnly(final AtomicLong target,final long value){
        long prev=target.get();
        while (value > prev){
            boolean update=target.compareAndSet(prev,value);
            if(update){
                return true;
            }
            prev=target.get();
        }
        return false;
    }

    /**
     * 获取本机地址
     * @return 地址
     */
    public static String localhost(){
        try {
            return InetAddress.getLocalHost().getHostName();
        }catch (UnknownHostException e){
            throw new RuntimeException("InetAddress java.net.InetAddress.getLocalHost() throws UnknownHostException"
                    + FAQURL.suggestTodo(FAQURL.UNKNOWN_HOST_EXCEPTION),
                    e);
        }
    }

    /**
     * 把字节数装换为可以读懂的单位，
     * @param bytes 字节数
     * @param si 单位为1000还是1024
     * @return
     */
    public static String humanReadableByteCount(long bytes,boolean si){
        int unit=si?1000:1024;
        if(bytes<unit){
            return bytes+"B";
        }
        int exp=(int)(Math.log(bytes)/Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * list装换为set
     * @param values
     * @return
     */
    public Set<String> list2Set(List<String> values) {
        Set<String> result = new HashSet<String>();
        for (String v : values) {
            result.add(v);
        }
        return result;
    }

    /**
     * set装换为list
     * @param values
     * @return
     */
    public List<String> set2List(Set<String> values) {
        List<String> result = new ArrayList<String>();
        for (String v : values) {
            result.add(v);
        }
        return result;
    }

}
