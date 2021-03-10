package com.jizhi.geektime.projects.user.proxy;

import java.lang.reflect.Method;

/**
 * 2021/3/10
 * jizhi7
 **/
public interface AfterInvoker extends Invoker {

    void after(Object proxyObj, Object targetObj, Method method, Object[] methodArgs, Object methodResult);

}
