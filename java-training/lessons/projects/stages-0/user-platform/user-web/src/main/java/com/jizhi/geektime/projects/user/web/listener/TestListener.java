package com.jizhi.geektime.projects.user.web.listener;

import com.jizhi.geektime.configuration.microprofile.JavaEEConfigProviderResolver;
import com.jizhi.geektime.jmx.mbean.MBeanLoadAgent;
import com.jizhi.geektime.projects.user.ioc.IoCContainer;
import org.eclipse.microprofile.config.ConfigValue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 测试监听器，
 *
 * @author jizhi7
 * @since 1.0
 **/
@WebListener
public class TestListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configurationTest(sce);
        mbeanTest(sce);
    }

    private void mbeanTest(ServletContextEvent sce) {
        IoCContainer container = (IoCContainer) sce.getServletContext().getAttribute(IoCContainer.IoC_NAME);
        MBeanLoadAgent agent = (MBeanLoadAgent) container.getObject("bean/MBeanLoadAgent");
        agent.loadMBean();
    }

    private void configurationTest(ServletContextEvent sce) {
        IoCContainer container = (IoCContainer) sce.getServletContext().getAttribute(IoCContainer.IoC_NAME);
        JavaEEConfigProviderResolver provider = (JavaEEConfigProviderResolver) container.getObject("bean/JavaEEConfigProviderResolver");
        String propertyName = "sun.arch.data.model";
        ConfigValue configValue = provider.getConfig().getConfigValue(propertyName);
        Integer value = provider.getConfig().getValue(propertyName, Integer.class);
        logger.log(Level.WARNING, "参数输出：");
        logger.log(Level.WARNING, "[name : " + propertyName + ",value : " + value + ",type : " + value.getClass() + ",source : " + configValue.getSourceName() + "]");
        logger.log(Level.WARNING, "参数结束。");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
