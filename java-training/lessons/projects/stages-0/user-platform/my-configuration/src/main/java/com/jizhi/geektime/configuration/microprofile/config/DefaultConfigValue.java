package com.jizhi.geektime.configuration.microprofile.config;

import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * 默认的 ConfigValue 实现类
 *
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultConfigValue implements ConfigValue {

    /**
     * 配置项的名称
     */
    private final String name;

    /**
     * 配置项的值
     */
    private final String value;

    /**
     * 配置项的原生值
     */
    private final String rawValue;

    /**
     * 配置项所在的配置源
     */
    private final ConfigSource configSource;


    public DefaultConfigValue(String name, String value, String rawValue, ConfigSource configSource) {
        this.name = name;
        this.value = value;
        this.rawValue = rawValue;
        this.configSource = configSource;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getRawValue() {
        return this.rawValue;
    }

    @Override
    public String getSourceName() {
        return this.configSource.getName();
    }

    @Override
    public int getSourceOrdinal() {
        return this.configSource.getOrdinal();
    }
}
