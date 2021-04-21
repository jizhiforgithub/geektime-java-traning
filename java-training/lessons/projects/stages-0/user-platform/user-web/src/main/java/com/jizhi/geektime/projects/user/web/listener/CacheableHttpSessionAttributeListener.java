package com.jizhi.geektime.projects.user.web.listener;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Http Session属性监听器
 * @author jizhi7
 * @since 1.0
 **/
public class CacheableHttpSessionAttributeListener implements HttpSessionAttributeListener {

    /**
     * 添加
     * @param event
     */
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {

    }

    /**
     * 移除
     * @param event
     */
    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {

    }

    /**
     * 替换
     * @param event
     */
    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {

    }
}
