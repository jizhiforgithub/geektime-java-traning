package com.jizhi.geektime.projects.user.proxy;

import java.lang.reflect.Method;

/**
 * 错误接口回调
 * @author jizhi7
 * @since 1.0
 **/
public interface ThrowableInvoker extends Invoker {

    void throwable(Object proxyObj, Object targetObj, Method method, Object[] methodArgs, Throwable throwable);

}
