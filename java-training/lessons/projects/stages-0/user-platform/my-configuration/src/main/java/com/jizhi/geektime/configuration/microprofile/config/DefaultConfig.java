package com.jizhi.geektime.configuration.microprofile.config;

import com.jizhi.geektime.configuration.microprofile.config.converter.Converters;
import com.jizhi.geektime.configuration.microprofile.source.ConfigSources;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.*;
import java.util.stream.StreamSupport;

/**
 * 默认的配置源，默认数据源是多个数据源的集合，
 * 通过数据源的优先级获取配置
 *
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultConfig implements Config {

    /**
     * 数据源集合
     */
    private final ConfigSources configSources;

    /**
     * 转换器集合
     */
    private final Converters converters;

    public DefaultConfig(ConfigSources configSources, Converters converters) {
        this.configSources = configSources;
        this.converters = converters;
    }

    /**
     * 获取转换后的配置
     * 如果对应的转换器找不到 会抛出 {@link UnsupportedOperationException} 异常
     *
     * @param propertyName 配置名
     * @param propertyType 转化的类型
     * @param <T>
     * @return
     */
    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        ConfigValue configValue = getConfigValue(propertyName);
        if (configValue == null) {
            return null;
        }
        String value = configValue.getValue();
        // 获取转换器，进行类型转换
        Converter<T> converter = getConverter(propertyType).get();
        if (converter == null) {
            throw new UnsupportedOperationException("不支持的类型转换");
        }
        return converter.convert(value);
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        String propertyValue = null;
        ConfigSource configSource = null;
        Iterator<ConfigSource> iterator = configSources.iterator();
        while (iterator.hasNext()) {
            configSource = iterator.next();
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        if (propertyValue == null) {
            return null;
        }
        return new DefaultConfigValue(propertyName, transformPropertyValue(propertyValue), propertyValue, configSource);
    }

    /**
     * 转换属性值（如果需要）
     *
     * @param propertyValue
     * @return
     */
    protected String transformPropertyValue(String propertyValue) {
        return propertyValue;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return StreamSupport.stream(configSources.spliterator(), false)
                .map(ConfigSource::getPropertyNames).collect(LinkedHashSet::new, Set::addAll, Set::addAll);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return configSources;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        Converter converter = doGetConverter(forType);
        return converter == null ? Optional.empty() : Optional.of(converter);
    }

    private <T> Converter doGetConverter(Class<T> forType) {
        List<Converter> converters = this.converters.getConverters(forType);
        return converters.isEmpty() ? null : converters.get(0);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
