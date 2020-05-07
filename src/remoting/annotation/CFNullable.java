package remoting.annotation;

import java.lang.annotation.*;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/28 9:32
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface CFNullable {
}
