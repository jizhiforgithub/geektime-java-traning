package com.jizhi.geektime.configuration.microprofile.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.*;
import java.util.stream.Stream;

/**
 * 多个配置源
 * @author jizhi7
 * @since 1.0
 **/
public class ConfigSources implements Iterable<ConfigSource> {

    private ClassLoader classLoader;
    /**
     * 配置源集合
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    /**
     * 是否添加了默认配置源
     */
    private boolean addedDefaultConfigSources = false;
    /**
     * 是否添加了
     */
    private boolean addedDiscoveredConfigSources = false;

    public ConfigSources(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 添加默认配置源
     */
    public void addDefaultSources() {
        if(addedDefaultConfigSources) {
            return;
        }
        // 先添加 Java 进程的配置参数
        // 再添加 操作系统的环境变量配置参数源
        // 最后添加 应用程序classPath下的META-INF下的microprofile-config文件
        addConfigSources(JavaSystemPropertiesConfigSource.class,
                OperationSystemEnvironmentVariablesConfigSource.class,
                DefaultResourceConfigSource.class);
        addedDefaultConfigSources = true;
    }

    /**
     * 添加发现的配置源，
     * 是在spi里面配置了的
     */
    public void addDiscoveredSources() {
        if(addedDiscoveredConfigSources) {
            return;
        }
        addConfigSources(ServiceLoader.load(ConfigSource.class, classLoader));
        addedDiscoveredConfigSources = true;
    }

    /**
     * 添加配置源
     * @param configSourceClasses 配置源对应的类可变参数
     */
    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        // 遍历类，实例化在添加
        addConfigSources(Stream.of(configSourceClasses).map(this::newInstance)
                .toArray(ConfigSource[]::new));
    }

    /**
     * 添加配置源
     * @param configSources 配置源可变参数
     */
    public void addConfigSources(ConfigSource... configSources) {
        addConfigSources(Arrays.asList(configSources));
    }

    /**
     * 添加配置源
     * @param configSources 配置源迭代器
     */
    public void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
        // 排序
        Collections.sort(this.configSources, ConfigSourceOrdinalComparator.INSTANCE);
    }

    /**
     * 实例化配置源
     * @param configSourceClass 配置源类
     * @return 实例化对象
     */
    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance = null;
        try {
            instance = configSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    /**
     * 配置源迭代器
     * @return
     */
    @Override
    public Iterator<ConfigSource> iterator() {
        return configSources.iterator();
    }

    /**
     * 是否已经添加了默认数据源
     * @return
     */
    public boolean isAddedDefaultConfigSources() {
        return addedDefaultConfigSources;
    }

    /**
     * 是否已经添加了发现的数据源 spi 的
     * @return
     */
    public boolean isAddedDiscoveredConfigSources() {
        return addedDiscoveredConfigSources;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
