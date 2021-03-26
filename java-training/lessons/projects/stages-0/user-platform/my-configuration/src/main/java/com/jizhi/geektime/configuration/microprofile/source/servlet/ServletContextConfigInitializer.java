package com.jizhi.geektime.configuration.microprofile.source.servlet;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContext;
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
        // 创建 servletContextConfigSource
        ServletContext servletContext = servletContextEvent.getServletContext();
        ServletContextConfigSource servletContextConfigSource = new ServletContextConfigSource(servletContext);

       // ClassLoader classLoader = servletContext.getClassLoader();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ConfigProviderResolver configProvider = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProvider.getBuilder();
        // 关联 classLoader
        configBuilder.forClassLoader(classLoader);
        // 添加默认的源
        configBuilder.addDefaultSources();
        // 通过发现配置源（动态的）
        configBuilder.addDiscoveredSources();
        // 添加扩展源
        configBuilder.withSources(servletContextConfigSource);
        // 获取 Config
        Config config = configBuilder.build();
        // 注册 Config 关联到当前 ClassLoader
        configProvider.registerConfig(config, classLoader);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
