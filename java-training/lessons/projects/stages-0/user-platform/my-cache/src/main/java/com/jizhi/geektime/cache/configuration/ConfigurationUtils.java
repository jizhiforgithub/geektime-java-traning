package com.jizhi.geektime.cache.configuration;

import javax.cache.configuration.*;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

/**
 * cache 配置工具类
 */
public abstract class ConfigurationUtils {

    /**
     * 将 {@link Configuration} 配置转换为 一个多配置的配置
     *
     * @param configuration
     * @param <V>
     * @param <K>
     * @return
     */
    public static <K, V> MutableConfiguration<K, V> mutableConfiguration(Configuration<K, V> configuration) {
        MutableConfiguration mutableConfiguration = null;
        // 可变的配置
        if (configuration instanceof MutableConfiguration) {
            mutableConfiguration = (MutableConfiguration) configuration;
        }
        // 只读的配置
        else if (configuration instanceof CompleteConfiguration) {
            CompleteConfiguration config = (CompleteConfiguration) configuration;
            mutableConfiguration = new MutableConfiguration<>(config);
        } else {
            mutableConfiguration = new MutableConfiguration<K, V>()
                    .setTypes(configuration.getKeyType(), configuration.getValueType())
                    .setStoreByValue(configuration.isStoreByValue());
        }
        return mutableConfiguration;
    }

    /**
     * 获取不变的配置
     *
     * @param configuration
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CompleteConfiguration<K, V> immutableConfiguration(MutableConfiguration<K, V> configuration) {
        return new ImmutableCompleteConfiguration(configuration);
    }

    /**
     * 缓存实体监听配置
     *
     * @param listener
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener) {
        return cacheEntryListenerConfiguration(listener, null);
    }

    /**
     * 缓存实体监听和过滤配置
     *
     * @param listener
     * @param filter
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter) {
        return cacheEntryListenerConfiguration(listener, filter, true);
    }

    /**
     * 缓存实体监听和过滤配置，是否旧数据，同步的
     *
     * @param listener
     * @param filter
     * @param isOldValueRequired
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired) {
        return cacheEntryListenerConfiguration(listener, filter, isOldValueRequired, true);
    }

    /**
     * 缓存实体监听和过滤配置，是否旧数据，是否同步
     *
     * @param listener
     * @param filter
     * @param isOldValueRequired
     * @param isSynchronous
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired,
                                                                                               boolean isSynchronous) {
        return new MutableCacheEntryListenerConfiguration<>(() -> listener, () -> filter, isOldValueRequired, isSynchronous);
    }
}
