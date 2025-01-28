package com.atguigu.tingshu.common.login;

import java.lang.annotation.*;

/**
 * @author admin
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface GuiGuLogin {
    boolean required() default true;
}
