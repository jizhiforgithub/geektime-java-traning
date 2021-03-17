package com.jizhi.geektime.configuration.microprofile;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 系统配置源
 * @author jizhi7
 * @since 1.0
 **/
public class JavaEESystemConfigSource implements ConfigSource {

    /**
     * Java 系统属性最好通本地变量保存，使用 Map 保存而不是 Properties ，
     * 因为 Properties 的 get 方法是同步的方法，并行的时候效率低，
     * 而运行的时候，系统属性基本是不变的了
     */
    private Map<String, String> properties;
    private static final String CONFIG_SOURCE_NAME = "JavaEESystemConfigSource";

    public JavaEESystemConfigSource() {
        this.properties = new HashMap<>();
        Properties systemProperties = System.getProperties();
        for (String key : systemProperties.stringPropertyNames()) {
            this.properties.put(key, systemProperties.getProperty(key));
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return CONFIG_SOURCE_NAME;
    }
}
