package com.jizhi.geektime.configuration.microprofile.source.servlet;

import com.jizhi.geektime.configuration.microprofile.source.MapBasedConfigSource;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

import static java.lang.String.format;

/**
 * 基于 servletConfig 的配置源
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ServletConfigSource extends MapBasedConfigSource {

    public static final int CONFIG_ORDINAL = 600;

    private ServletConfig servletConfig;

    public ServletConfigSource(ServletConfig servletConfig) {
        super(format("Servlet[name:%s] Init Parameters", servletConfig.getServletName()), CONFIG_ORDINAL);

        this.servletConfig = servletConfig;
    }

    /**
     * 从 servletConfig 中获取参数
     * @param configData
     * @throws Throwable
     */
    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        Enumeration<String> parameterNames = servletConfig.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            configData.put(name, servletConfig.getInitParameter(name));
        }
    }

}
