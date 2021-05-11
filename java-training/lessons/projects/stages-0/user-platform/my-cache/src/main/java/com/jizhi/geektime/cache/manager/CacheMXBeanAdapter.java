package com.jizhi.geektime.cache.manager;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;
import java.util.Objects;

/**
 * 缓存MXBean的适配器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class CacheMXBeanAdapter implements CacheMXBean {

    /**
     * 缓存的配置
     */
    private final CompleteConfiguration<?, ?> configuration;

    public CacheMXBeanAdapter(CompleteConfiguration<?, ?> configuration) {
        Objects.requireNonNull(configuration, "The argument 'configuration' must not be null!");
        this.configuration = configuration;
    }

    @Override
    public String getKeyType() {
        return configuration.getKeyType().getName();
    }

    @Override
    public String getValueType() {
        return configuration.getValueType().getName();
    }

    @Override
    public boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    @Override
    public boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    @Override
    public boolean isStoreByValue() {
        return configuration.isStoreByValue();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    @Override
    public boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }
}
