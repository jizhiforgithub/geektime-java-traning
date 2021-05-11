package com.jizhi.geektime.configuration.microprofile.source;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源
 *
 * @author jizhi7
 * @since 1.0
 **/
public class DynamicConfigSource extends MapBasedConfigSource {

    /**
     * 配置源名称
     */
    public static final String CONFIG_NAME = "DynamicConfigSource";

    /**
     * 配资源优先级
     */
    public static final int CONFIG_ORDINAL = 500;

    private Map configData;

    public DynamicConfigSource() {
        super(CONFIG_NAME, CONFIG_ORDINAL);
        configData = new HashMap();
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        this.configData = configData;
    }

    /**
     * 异步更新数据
     *
     * @param data
     */
    public void onUpdate(String data) {

    }
}
