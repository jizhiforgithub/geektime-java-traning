package com.jizhi.geektime.configuration.microprofile.source.servlet;

import com.jizhi.geektime.configuration.microprofile.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

/**
 * 基于 servlet 的配置源
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ServletContextConfigSource extends MapBasedConfigSource {

    public static final String CONFIG_NAME = "ServletContext Init Parameters";
    public static final int CONFIG_ORDINAL = 500;

    private ServletContext servletContext;

    public ServletContextConfigSource(ServletContext servletContext) {
        super(CONFIG_NAME, CONFIG_ORDINAL);
        this.servletContext = servletContext;
    }

    /**
     * 从 servletContext 中获取 初始化参数
     *
     * @param configData
     * @throws Throwable
     */
    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        Enumeration<String> parameterNames = servletContext.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            configData.put(name, servletContext.getInitParameter(name));
        }
    }

}
