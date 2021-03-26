package com.jizhi.geektime.config.web;

import com.jizhi.geektime.context.ClassicComponentContext;
import com.jizhi.geektime.context.ComponentContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * servlet 监听器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ServletContextConfigInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 创建 Component
        ComponentContext context = new ClassicComponentContext();
        ((ClassicComponentContext) context).init(servletContextEvent.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
