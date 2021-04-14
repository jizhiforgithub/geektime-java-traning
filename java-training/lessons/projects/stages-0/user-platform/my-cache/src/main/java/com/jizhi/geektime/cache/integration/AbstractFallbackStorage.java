package com.jizhi.geektime.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 回调存储接口 {@link FallbackStorage} 的抽象类实现
 * @author jizhi7
 * @since 1.0
 **/
public abstract class AbstractFallbackStorage<K, V> implements FallbackStorage<K, V> {

    /**
     * 优先级
     */
    private final int priority;

    protected AbstractFallbackStorage(int priority) {
        this.priority = priority;
    }

    /**
     * 加载所有的 keys
     * @param keys
     * @return
     * @throws CacheLoaderException
     */
    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) throws CacheLoaderException {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, load(key));
        }
        return map;
    }

    /**
     * 写入所有
     * @param entries
     * @throws CacheWriterException
     */
    @Override
    public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> entries) throws CacheWriterException {
        entries.forEach(this::write);
    }

    /**
     * 删除所有的 keys
     * @param keys
     * @throws CacheWriterException
     */
    @Override
    public void deleteAll(Collection<?> keys) throws CacheWriterException {
        keys.forEach(this::delete);
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
