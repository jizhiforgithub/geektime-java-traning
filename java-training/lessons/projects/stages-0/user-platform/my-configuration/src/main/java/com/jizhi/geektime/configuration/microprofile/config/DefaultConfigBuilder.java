package com.jizhi.geektime.configuration.microprofile.config;

import com.jizhi.geektime.configuration.microprofile.config.converter.Converters;
import com.jizhi.geektime.configuration.microprofile.source.ConfigSources;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * 默认配置建造者
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultConfigBuilder implements ConfigBuilder {

    /**
     * 配置源集合
     */
    private final ConfigSources configSources;

    /**
     * 转换器集合
     */
    private final Converters converters;

    public DefaultConfigBuilder(ClassLoader classLoader) {
        this.configSources = new ConfigSources(classLoader);
        this.converters = new Converters(classLoader);
        converters.addDiscoveredConverters();
    }

    @Override
    public ConfigBuilder addDefaultSources() {
        configSources.addDefaultSources();
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredSources() {
        configSources.addDiscoveredSources();
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredConverters() {
        converters.addDiscoveredConverters();
        return this;
    }

    @Override
    public ConfigBuilder forClassLoader(ClassLoader loader) {
        configSources.setClassLoader(loader);
        converters.setClassLoader(loader);
        return this;
    }

    @Override
    public ConfigBuilder withSources(ConfigSource... sources) {
        configSources.addConfigSources(sources);
        return this;
    }

    @Override
    public ConfigBuilder withConverters(Converter<?>... converters) {
        this.converters.addConverters(converters);
        return this;
    }

    @Override
    public <T> ConfigBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
        this.converters.addConverter(converter, priority, type);
        return this;
    }

    @Override
    public Config build() {
        Config config = new DefaultConfig(configSources, converters);
        return config;
    }
}
