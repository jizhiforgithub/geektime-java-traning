package com.jizhi.geektime.configuration.microprofile;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.Collections;
import java.util.ServiceLoader;

/**
 * 基于microFile的配置文件的实现
 * @author jizhi7
 * @since 1.0
 **/
public class JavaEEConfigProviderResolver extends ConfigProviderResolver {

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        if(loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        ServiceLoader<Config> load = ServiceLoader.load(Config.class, loader);
        return load.iterator().hasNext() ? load.iterator().next() : null;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseConfig(Config config) {
        throw new UnsupportedOperationException();
    }
}
