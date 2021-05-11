package com.jizhi.geektime.web.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 页面跳转控制接口，负责服务端的页面渲染和跳转
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface PageController extends Controller {

    /**
     * 页面跳转方法
     *
     * @param request  请求
     * @param response 响应
     * @return 返回对应的跳转地址的url，可能是视图路径，也可能是其他的服务路径
     * @throws Throwable 发生的异常
     */
    String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable;

}
