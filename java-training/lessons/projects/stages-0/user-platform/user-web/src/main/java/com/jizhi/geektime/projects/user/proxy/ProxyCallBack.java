package com.jizhi.geektime.projects.user.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 2021/3/10
 * jizhi7
 **/
public class ProxyCallBack implements MethodInterceptor {

    private List<BeforeInvoker> beforeInvokers;
    private List<ThrowableInvoker> throwableInvokers;
    private List<AfterInvoker> afterInvokers;
    private Object target;

    public ProxyCallBack(Object target) {
        this.beforeInvokers = new ArrayList<>();
        this.throwableInvokers = new ArrayList<>();
        this.afterInvokers = new ArrayList<>();
        this.target = target;
    }

    @Override
    public Object intercept(Object proxyObj, Method method, Object[] methodArgs, MethodProxy methodProxy) throws Throwable {
        if(beforeInvokers != null && beforeInvokers.size() != 0) {
            for(BeforeInvoker before : beforeInvokers) {
                before.before(proxyObj, this.target, method, methodArgs);
            }
        }
        Object result = null;
        boolean hasException = false;
        try {
            result = method.invoke(target, methodArgs);
        } catch (Throwable throwable) {
            hasException = true;
            if(throwableInvokers != null && throwableInvokers.size() != 0) {
                for(ThrowableInvoker th : throwableInvokers) {
                    th.throwable(proxyObj, this.target, method, methodArgs, throwable);
                }
            }
        }
        if(!hasException && afterInvokers != null && afterInvokers.size() != 0) {
            for(AfterInvoker after : afterInvokers) {
                after.after(proxyObj, this.target, method, methodArgs, result);
            }
        }
        return result;
    }

    public void addBeforeInvoker(BeforeInvoker beforeInvoker) {
        this.beforeInvokers.add(beforeInvoker);
    }

    public void addThrowableInvoker(ThrowableInvoker throwableInvoker) {
        this.throwableInvokers.add(throwableInvoker);
    }

    public void addAfterInvoker(AfterInvoker afterInvoker) {
        this.afterInvokers.add(afterInvoker);
    }


}
