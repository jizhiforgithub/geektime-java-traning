package com.jizhi.geektime.web.mvc;

import com.jizhi.geektime.web.mvc.controller.Controller;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * {@link Controller} 的处理请求方法的信息类
 * @author jizhi7
 * @since 1.0
 **/
public class HandlerMethodInfo {

    /**
     * 请求路径
     */
    private final String requestPath;

    /**
     * 处理请求的方法
     */
    private final Method handlerMethod;

    /**
     * 该方法支持的HTTP处理的方法
     */
    private final Set<String> supportHttpMethods;

    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportHttpMethods) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportHttpMethods = supportHttpMethods;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportHttpMethods() {
        return supportHttpMethods;
    }
}
