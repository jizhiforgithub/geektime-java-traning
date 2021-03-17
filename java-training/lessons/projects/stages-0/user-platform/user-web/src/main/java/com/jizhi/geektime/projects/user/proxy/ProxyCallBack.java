package com.jizhi.geektime.projects.user.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态代理回调类
 * @author jizhi7
 * @since 1.0
 **/
public class ProxyCallBack implements MethodInterceptor {

    /**
     * 前置方法集合
     */
    private List<BeforeInvoker> beforeInvokers;

    /**
     * 错误方法回调集合
     */
    private List<ThrowableInvoker> throwableInvokers;

    /**
     * 后置方法回调集合
     */
    private List<AfterInvoker> afterInvokers;

    private List<FinallyInvoker> finallyInvokers;

    private Object target;

    public ProxyCallBack(Object target) {
        this.beforeInvokers = new ArrayList<>();
        this.throwableInvokers = new ArrayList<>();
        this.afterInvokers = new ArrayList<>();
        this.finallyInvokers = new ArrayList<>();
        this.target = target;
    }

    @Override
    public Object intercept(Object proxyObj, Method method, Object[] methodArgs, MethodProxy methodProxy) throws Throwable {

        try {
            Object result = null;
            // 调用前置方法
            if(beforeInvokers != null && beforeInvokers.size() > 0) {
                for(BeforeInvoker before : beforeInvokers) {
                    before.before(proxyObj, this.target, method, methodArgs);
                }
            }
            // 调用目标对象的方法
            result = method.invoke(target, methodArgs);
            // 如果没有发生错误，调用后置方法
            if(afterInvokers != null && afterInvokers.size() > 0) {
                for(AfterInvoker after : afterInvokers) {
                    after.after(proxyObj, this.target, method, methodArgs, result);
                }
            }
            return result;
        } catch (Throwable throwable) {
            if(throwableInvokers != null && throwableInvokers.size() > 0) {
                for(ThrowableInvoker th : throwableInvokers) {
                    th.throwable(proxyObj, this.target, method, methodArgs, throwable);
                }
            }
            return null;
        } finally {
            if(finallyInvokers != null && finallyInvokers.size() > 0) {
                for (FinallyInvoker finallyInvoker : finallyInvokers) {
                    finallyInvoker.doFinally();
                }
            }
        }
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

    public void addFinallyInvoker(FinallyInvoker finallyInvoker) {
        this.finallyInvokers.add(finallyInvoker);
    }

    public Object getTarget() {
        return this.target;
    }

}
