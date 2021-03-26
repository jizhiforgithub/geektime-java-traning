package com.jizhi.geektime.config.web;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * servlet 应用启动的时候，添加监听器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ServletComponentContextInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        // 添加监听器
        ctx.addListener(ServletContextConfigInitializer.class);
    }

}
