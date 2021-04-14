package com.jizhi.geektime.memory;

import com.jizhi.geektime.cache.AbstractCacheManager;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * 内存类型实现的缓存管理器
 * @author jizhi7
 * @since 1.0
 **/
public class InMemoryCacheManager extends AbstractCacheManager {

    public InMemoryCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new InMemoryCache<K, V>(this, cacheName, configuration);
    }

}
