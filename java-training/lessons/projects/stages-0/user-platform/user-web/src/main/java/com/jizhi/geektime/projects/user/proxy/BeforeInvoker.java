package com.jizhi.geektime.projects.user.proxy;

import java.lang.reflect.Method;

/**
 * 前置回调接口
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface BeforeInvoker extends Invoker {

    void before(Object proxyObj, Object targetObj, Method method, Object[] methodArgs);

}
