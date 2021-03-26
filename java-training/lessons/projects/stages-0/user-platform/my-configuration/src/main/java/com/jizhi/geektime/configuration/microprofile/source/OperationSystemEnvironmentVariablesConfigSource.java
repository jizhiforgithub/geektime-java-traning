package com.jizhi.geektime.configuration.microprofile.source;

import java.util.Map;

/**
 * 操作系统环境变量配资源
 * @author jizhi7
 * @since 1.0
 **/
public class OperationSystemEnvironmentVariablesConfigSource extends MapBasedConfigSource {

    /**
     * 配置源名称
     */
    public static final String CONFIG_NAME = "Operation System Environment Variables";

    /**
     * 配资源优先级
     */
    public static final int CONFIG_ORDINAL = 300;

    public OperationSystemEnvironmentVariablesConfigSource() {
        super(CONFIG_NAME, CONFIG_ORDINAL);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        configData.putAll(System.getenv());
    }
}
