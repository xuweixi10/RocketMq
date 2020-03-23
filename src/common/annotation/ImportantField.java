package common.annotation;

import javax.xml.bind.Element;
import java.lang.annotation.*;

/**
 *
 * @author xuxiaoxi10
 * 1 @Documented将会使注解信息包含在生成的javadoc上
 * 2 保留时间长短 分别有
 *      RUNTIME 运行时保留，真正会进入java虚拟机的
 *      CLASS 编译时被保留,在class文件中存在,但JVM将会忽略
 *      SOURCE 源代码级别保留,编译时就会被忽略
 * 3 可以用于注释的目标
 * ElementType. FIELD 字段上, METHOD 方法上, PARAMETER 参数上,PARAMETER 局部变量
 *              ANNOTATION_TYPE	可用于注解类型上（被interface修饰的类型）
 *              PACKAGE	用于记录java文件的package信息
 *              TYPE 类或接口上
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
public @interface ImportantField {
}
