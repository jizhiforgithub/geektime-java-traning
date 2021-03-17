package com.jizhi.geektime.configuration.microprofile;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配置集合
 * @author jizhi7
 * @since 1.0
 **/
public class JavaEEConfig implements Config {

    /**
     * 内部可变集合，不要直接暴露出去
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    private Map<Class<?>, Converter> converterMapping = new HashMap<>();

    public JavaEEConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> load = ServiceLoader.load(ConfigSource.class, classLoader);
        load.forEach(configSources::add);
        // 排序
        configSources.sort((a,b) -> b.getOrdinal()-a.getOrdinal());
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // 类型转换
        Converter<T> converter = getConverter(propertyType).get();
        T result = converter.convert(propertyValue);
        return result;
    }

    private String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if(propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        ConfigValue configValue = null;
        for (ConfigSource configSource : configSources) {
            configValue = new JavaEEConfigValue(propertyName, getPropertyValue(propertyName), configSource);
            if(configValue != null) {
                break;
            }
        }
        return configValue;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T propertyValue = getValue(propertyName, propertyType);
        return Optional.ofNullable(propertyValue);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return Collections.unmodifiableSet(configSources.stream().map(a -> a.getName()).collect(Collectors.toSet()));
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        if(converterMapping == null || converterMapping.size() <= 0) {
            ServiceLoader<Converter> converters = ServiceLoader.load(Converter.class);
            for (Converter converter : converters) {
                // 泛型参数
                ParameterizedType parameterizedType = (ParameterizedType) converter.getClass().getGenericInterfaces()[0];
                Type type = parameterizedType.getActualTypeArguments()[0];
                converterMapping.put((Class<?>) type, converter);
            }
        }
        return Optional.ofNullable(converterMapping.get(forType));
    }

    /**
     * 不支持 profile 的形式获取
     * @param type
     * @param <T>
     * @return
     */
    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException();
    }
}
