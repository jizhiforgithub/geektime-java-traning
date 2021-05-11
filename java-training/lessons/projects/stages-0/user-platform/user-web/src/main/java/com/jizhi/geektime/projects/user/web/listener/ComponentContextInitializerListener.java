package com.jizhi.geektime.projects.user.web.listener;

import com.jizhi.geektime.context.ClassicComponentContext;
import com.jizhi.geektime.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * {@link ClassicComponentContext} 初始化器
 **/
@WebListener
public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    /**
     * 初始化
     *
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ClassicComponentContext context = new ClassicComponentContext();
        context.init(servletContext);
    }

    /**
     * 销毁
     *
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = ClassicComponentContext.getInstance();
        context.destroy();
    }
}
