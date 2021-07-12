package org.example.mirai.plugin.annotation;

import java.lang.annotation.*;

/**
 * @author Mu Yuchen
 * @date 2021/7/8 14:59
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiraiCommand {
    String value() default "";
}
