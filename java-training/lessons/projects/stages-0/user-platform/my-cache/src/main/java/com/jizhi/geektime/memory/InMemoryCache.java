package com.jizhi.geektime.memory;

import com.jizhi.geektime.cache.AbstractCache;
import com.jizhi.geektime.cache.ExpirableEntry;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 基于内存类型的缓存项
 * @author jizhi7
 * @since 1.0
 **/
public class InMemoryCache<K, V> extends AbstractCache<K, V> {

    /**
     * 缓存的数据
     */
    private final Map<K, ExpirableEntry<K, V>> cache;

    protected InMemoryCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        cache = new HashMap<>();
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        return cache.get(key);
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return cache.containsKey(key);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException {
        K key = newEntry.getKey();
        cache.put(key, newEntry);
    }

    @Override
    protected Set<? extends K> keySet() {
        return cache.keySet();
    }

    @Override
    protected void clearEntries() throws CacheException {
        cache.clear();
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) {
        return cache.remove(key);
    }
}
