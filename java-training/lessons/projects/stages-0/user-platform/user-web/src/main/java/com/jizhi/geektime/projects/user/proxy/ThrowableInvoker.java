package com.jizhi.geektime.projects.user.proxy;

import java.lang.reflect.Method;

/**
 * 2021/3/10
 * jizhi7
 **/
public interface ThrowableInvoker extends Invoker {

    void throwable(Object proxyObj, Object targetObj, Method method, Object[] methodArgs, Throwable throwable);

}
