package com.jizhi.geektime.configuration.microprofile.config.servlet.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * 当每个请求进来的时候，创建线程的时候，
 *  为当前线程，创建一个 threadLocal ，并将 config 数据放进去
 *  请求退出的时候，移除这个 threadLocal
 * @author jizhi7
 * @since 1.0
 **/
public class ConfigServletRequsetListener implements ServletRequestListener {

    private static final ThreadLocal<Config> configThreadLocal = new ThreadLocal<>();

    /**
     * 请求刚进来的时候
     * @param sre
     */
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest servletRequest = sre.getServletRequest();
        ServletContext servletContext = servletRequest.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver instance = ConfigProviderResolver.instance();
        Config config = instance.getConfig(classLoader);
        configThreadLocal.set(config);
    }

    /**
     * 主要是给请求中的其它没有request对象类调用的，获得当前线程的config
     * @return
     */
    public static Config getConfig() {
        return configThreadLocal.get();
    }

    /**
     * 请求即将要出去的时候
     * @param sre
     */
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // 防止 OOM
        configThreadLocal.remove();
    }

}
