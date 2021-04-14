package com.jizhi.geektime.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * 组合了多个 {@link AbstractFallbackStorage} 的回调存储实现类，采用了组合设计模式
 *  通过 Java SPI 获取实现了 {@link AbstractFallbackStorage} 的实现类，来组合
 * @author jizhi7
 * @since 1.0
 **/
public class CompositeFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    /**
     * 类加载器和回调存储列表的映射表
     */
    private static final ConcurrentMap<ClassLoader, List<FallbackStorage>> fallbackStoragesCache =
            new ConcurrentHashMap<>();

    /**
     * 当前类的加载器对应的 所有的回调存储列表
     */
    private final List<FallbackStorage> fallbackStorages;

    public CompositeFallbackStorage() {
        // 默认使用当前线程的类加载器来加载
        this(Thread.currentThread().getContextClassLoader());
    }

    public CompositeFallbackStorage(ClassLoader classLoader) {
        super(Integer.MIN_VALUE);
        // 初始化加载所有SPI配置的回调存储实现类
        this.fallbackStorages = fallbackStoragesCache.computeIfAbsent(classLoader, this::loadFallbackStorages);
    }

    /**
     * 加载所有的回调存储实现，使用SPI方式加载
     * @param classLoader
     * @return
     */
    private List<FallbackStorage> loadFallbackStorages(ClassLoader classLoader) {
        return stream(ServiceLoader.load(FallbackStorage.class, classLoader).spliterator(), false)
                .sorted(PRIORITY_COMPARATOR)
                .collect(toList());
    }

    /**
     * 遍历当前类加载器的所有回调存储列表，调用他们来加载
     * @param key
     * @return
     * @throws CacheLoaderException
     */
    @Override
    public Object load(Object key) throws CacheLoaderException {
        Object value = null;
        for (FallbackStorage fallbackStorage : fallbackStorages) {
            value = fallbackStorage.load(key);
            // 第一个找到了，就返回
            if(value != null) {
                break;
            }
        }
        return value;
    }

    /**
     * 每个都写
     * @param entry
     * @throws CacheWriterException
     */
    @Override
    public void write(Cache.Entry<?, ?> entry) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.write(entry));
    }

    /**
     * 每个都删
     * @param key
     * @throws CacheWriterException
     */
    @Override
    public void delete(Object key) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.delete(key));
    }

    /**
     * 每个都删
     */
    @Override
    public void destroy() {
        fallbackStorages.forEach(FallbackStorage::destroy);
    }

}
