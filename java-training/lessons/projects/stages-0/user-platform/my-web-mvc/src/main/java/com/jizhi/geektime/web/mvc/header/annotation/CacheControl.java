package com.jizhi.geektime.web.mvc.header.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheControl {

    String[] value() default {};

}
