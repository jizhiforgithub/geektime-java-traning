package com.jizhi.geektime.configuration.microprofile.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 基于 Map 形式的配置元基类
 *
 * @author jizhi7
 * @since 1.0
 **/
public abstract class MapBasedConfigSource implements ConfigSource {

    /**
     * 配置源名称
     */
    private final String name;

    /**
     * 配置源的优先级
     */
    private final int ordinal;

    /**
     * 配置源的数据
     */
    private Map<String, String> source;

    public MapBasedConfigSource(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
        //this.source = getProperties();
    }

    /**
     * 获取配置数据，方法定义为 final 的不允许子类重写，
     * 子类只需要实现 {@link #prepareConfigData} 方法提供配置源数据
     *
     * @return 不可修改的 Map 类型的配置数据
     */
    @Override
    public final Map<String, String> getProperties() {
        Map<String, String> configData = new HashMap<>();
        try {
            prepareConfigData(configData);
        } catch (Throwable cause) {
            throw new IllegalStateException("准备配置数据发生错误", cause);
        }
        return Collections.unmodifiableMap(configData);
    }

    /**
     * 准备数据，子类需要实现这个方法，提供具体的配置数据
     *
     * @param configData
     */
    protected abstract void prepareConfigData(Map configData) throws Throwable;


    @Override
    public Set<String> getPropertyNames() {
        if (source == null) {
            this.source = getProperties();
        }
        return source.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        if (source == null) {
            this.source = getProperties();
        }
        return source.get(propertyName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }
}
