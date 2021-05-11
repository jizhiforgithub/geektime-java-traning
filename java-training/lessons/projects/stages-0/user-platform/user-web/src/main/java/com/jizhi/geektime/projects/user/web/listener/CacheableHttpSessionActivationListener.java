package com.jizhi.geektime.projects.user.web.listener;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

/**
 * http session 缓存监听器
 * jizhi7
 **/
public class CacheableHttpSessionActivationListener implements HttpSessionActivationListener {

    /**
     * 缓存钝化
     *
     * @param se
     */
    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {

    }

    /**
     * @param se
     */
    @Override
    public void sessionDidActivate(HttpSessionEvent se) {

    }
}
