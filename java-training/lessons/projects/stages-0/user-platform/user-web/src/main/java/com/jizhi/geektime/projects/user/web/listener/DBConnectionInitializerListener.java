package com.jizhi.geektime.projects.user.web.listener;

import com.jizhi.geektime.projects.user.repository.DatabaseUserRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 2021/3/2
 * jizhi7
 **/
@WebListener
public class DBConnectionInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new DatabaseUserRepository().initDatabase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
