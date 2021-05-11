package com.jizhi.geektime.microprofile.rest;

import java.lang.reflect.Method;

/**
 * 接口的请求方法模板解析器
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface RequestTemplateResolver {

    /**
     * 解析该类的方法的 *Param 相关注解，并创建 {@link RequestTemplate}
     *
     * @param resourceClass
     * @param resourceMethod
     * @return
     */
    RequestTemplate resolve(Class<?> resourceClass, Method resourceMethod);

}
