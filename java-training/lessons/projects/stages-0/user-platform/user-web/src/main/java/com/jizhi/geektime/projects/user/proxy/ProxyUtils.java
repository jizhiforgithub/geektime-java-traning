package com.jizhi.geektime.projects.user.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * 事务代理类创建
 * @author jizhi7
 * @since 1.0
 **/
public class ProxyUtils {

    public static Object createProxy(Class<?> target, Callback callback) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target);
        enhancer.setCallback(callback);
        return enhancer.create();
    }

}
