package com.jizhi.geektime.configuration.microprofile;

import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * 配置项值
 * @author jizhi7
 * @since 1.0
 **/
public class JavaEEConfigValue implements ConfigValue {

    private String name;
    private String value;
    private ConfigSource configSource;

    public JavaEEConfigValue(String name, String value, ConfigSource configSource) {
        this.name = name;
        this.value = value;
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
        return this.value;
    }

    @Override
    public String getSourceName() {
        return configSource.getName();
    }

    @Override
    public int getSourceOrdinal() {
        return configSource.getOrdinal();
    }

}
