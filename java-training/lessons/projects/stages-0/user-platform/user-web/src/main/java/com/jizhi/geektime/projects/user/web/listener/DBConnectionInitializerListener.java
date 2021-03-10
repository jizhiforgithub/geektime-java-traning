package com.jizhi.geektime.projects.user.web.listener;

import com.jizhi.geektime.projects.user.ioc.IoCContainer;
import com.jizhi.geektime.projects.user.repository.DatabaseUserRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * wen监听器，初始化IoC容器，
 * @author jizhi7
 * @since 1.0
 **/
@WebListener
public class DBConnectionInitializerListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        IoCContainer container = new IoCContainer();
        sce.getServletContext().setAttribute(IoCContainer.IoC_NAME, container);
        IoCContainer.addServletContext(getClass().getClassLoader(), sce.getServletContext());
        container.init();
        new DatabaseUserRepository().initDatabase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
