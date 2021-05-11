package com.jizhi.geektime.projects.user.proxy;

import java.lang.reflect.Method;

/**
 * 后置回调接口
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface AfterInvoker extends Invoker {

    void after(Object proxyObj, Object targetObj, Method method, Object[] methodArgs, Object methodResult);

}
