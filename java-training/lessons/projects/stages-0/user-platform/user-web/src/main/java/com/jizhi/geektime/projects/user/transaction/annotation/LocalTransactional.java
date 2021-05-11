package com.jizhi.geektime.projects.user.transaction.annotation;

import java.lang.annotation.*;

/**
 * 事务注解
 *
 * @author jizhi7
 * @since 1.0
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalTransactional {

    /**
     * 事务传播级别
     *
     * @return
     */
    int propagation() default LocalPropagation.REQUIRED;

    /**
     * 事务隔离级别
     *
     * @return
     */
    int isolation() default LocalIsolation.DEFAULT;


    Class<? extends Throwable>[] rollbackFor() default {};

}
