package com.jizhi.geektime.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认配置提供者
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultConfigProviderResolver extends ConfigProviderResolver {

    /**
     * 配置集合，和 classLoader 关联
     */
    private Map<ClassLoader, Config> configRepository = new ConcurrentHashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        if(loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        return configRepository.computeIfAbsent(loader, this::newConfig);
    }

    protected Config newConfig(ClassLoader classLoader) {
        return newConfigBuilder(classLoader).build();
    }

    /**
     * 获取数据源 builder
     * @return
     */
    @Override
    public ConfigBuilder getBuilder() {
        return newConfigBuilder(null);
    }

    protected ConfigBuilder newConfigBuilder(ClassLoader classLoader) {
        return new DefaultConfigBuilder(resolveClassLoader(classLoader));
    }

    /**
     * 获取 classLoader
     * @param classLoader
     * @return
     */
    private ClassLoader resolveClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.getClass().getClassLoader() : classLoader;
    }

    /**
     * 注册配置源
     * @param config
     * @param classLoader
     */
    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        configRepository.put(classLoader, config);
    }

    /**
     * 删除对应的配置源
     * @param config
     */
    @Override
    public void releaseConfig(Config config) {
        List<ClassLoader> targetKeys = new LinkedList<>();
        for (Map.Entry<ClassLoader, Config> entry : configRepository.entrySet()) {
            if (Objects.equals(config, entry.getValue())) {
                targetKeys.add(entry.getKey());
            }
        }
        targetKeys.forEach(configRepository::remove);
    }
}
