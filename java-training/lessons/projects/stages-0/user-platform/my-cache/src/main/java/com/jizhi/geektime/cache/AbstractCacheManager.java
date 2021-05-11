package com.jizhi.geektime.cache;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * 缓存管理器的基类
 *
 * @author jizhi7
 * @since 1.0
 */
public abstract class AbstractCacheManager implements CacheManager {

    /**
     * 缓存注册容器
     */
    private ConcurrentMap<String, Map<KeyValueTypePair, Cache>> cacheRepository = new ConcurrentHashMap<>();

    /**
     * 缓存清除
     */
    private static final Consumer<Cache> CLEAR_CACHE_OPERATION = Cache::clear;

    /**
     * 缓存关闭
     */
    private static final Consumer<Cache> CLOSE_CACHE_OPERATION = Cache::close;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final CachingProvider cachingProvider;
    private final URI uri;
    private final Properties properties;
    private final ClassLoader classLoader;
    private volatile boolean closed;

    public AbstractCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        this.cachingProvider = cachingProvider;
        this.uri = uri == null ? cachingProvider.getDefaultURI() : uri;
        this.properties = properties == null ? cachingProvider.getDefaultProperties() : properties;
        this.classLoader = classLoader == null ? cachingProvider.getDefaultClassLoader() : classLoader;
    }

    @Override
    public final CachingProvider getCachingProvider() {
        return this.cachingProvider;
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * 创建缓存
     *
     * @param cacheName     缓存名称
     * @param configuration 配置
     * @param <K>           key
     * @param <V>           val
     * @param <C>           配置
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        // 如果注册容器里面已经存在了，说明之前已经创建过了，不能再创建了
        if (cacheRepository.containsKey(cacheName)) {
            throw new CacheException(format("The Cache whose name is '%s' is already existed, " +
                    "please try another name to create a new Cache.", cacheName));
        }
        return getOrCreateCache(cacheName, configuration, true);
    }

    /**
     * 获取值，没有就拿默认值
     *
     * @param cacheName
     * @param configuration
     * @param created
     * @param <V>
     * @param <K>
     * @param <C>
     * @return
     */
    protected <V, K, C extends Configuration<K, V>> Cache<K, V> getOrCreateCache(String cacheName, C configuration, boolean created) {
        // 判断没有关闭
        assertNotClosed();
        // 如果没有就，创建一个CacheMap
        Map<KeyValueTypePair, Cache> cacheMap = cacheRepository.computeIfAbsent(cacheName, n -> new ConcurrentHashMap<>());

        // 如果没有创建，就创建放进
        return cacheMap.computeIfAbsent(new KeyValueTypePair(configuration.getKeyType(), configuration.getValueType()),
                key -> created ? doCreateCache(cacheName, configuration) : null);
    }

    /**
     * 子类实现的创建一个缓存
     *
     * @param cacheName
     * @param configuration
     * @param <K>
     * @param <V>
     * @param <C>
     * @return
     */
    protected abstract <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration);

    private void assertNotClosed() {
        if (idClose()) {
            throw new IllegalStateException("The CacheManager has been closed, current operation should not be invoked!");
        }
    }

    private boolean idClose() {
        return this.closed;
    }

    /**
     * 获取缓存对象
     *
     * @param cacheName 缓存名称
     * @param keyType   缓存的K类型
     * @param valueType 缓存的V类型
     * @param <K>
     * @param <V>
     * @return
     */
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        MutableConfiguration<K, V> configuration = new MutableConfiguration<K, V>()
                .setTypes(keyType, valueType);
        return getOrCreateCache(cacheName, configuration, false);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return getCache(cacheName, (Class<K>) Object.class, (Class<V>) Object.class);
    }

    /**
     * 获取所有的缓存对象名称
     *
     * @return
     */
    @Override
    public Iterable<String> getCacheNames() {
        assertNotClosed();
        return cacheRepository.keySet();
    }

    /**
     * 销毁缓存
     *
     * @param cacheName 缓存名称
     */
    @Override
    public void destroyCache(String cacheName) {
        // 判定cacheName不为空
        Objects.requireNonNull(cacheName, "The 'cacheName' argument must not be null.");
        assertNotClosed();
        // 移除
        Map<KeyValueTypePair, Cache> remove = cacheRepository.remove(cacheName);
        if (remove != null) {
            // 迭代移除缓存的值，执行，缓存清除，缓存关闭
            iterateCaches(remove.values(), CLEAR_CACHE_OPERATION, CLOSE_CACHE_OPERATION);
        }
    }

    /**
     * 迭代缓存项，执行一系列操作
     *
     * @param cacheItems     缓存项迭代
     * @param cacheOperation 一系列操作，可变参数
     */
    protected final void iterateCaches(Iterable<Cache> cacheItems, Consumer<Cache>... cacheOperation) {
        for (Cache cache : cacheItems) {
            for (Consumer<Cache> cacheConsumer : cacheOperation) {
                try {
                    cacheConsumer.accept(cache);
                } catch (Throwable e) {
                    logger.finest(e.getMessage());
                }
            }
        }
    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {
        assertNotClosed();
        // TODO
        throw new UnsupportedOperationException("TO support in the future!");
    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {
        assertNotClosed();
        // TODO
        throw new UnsupportedOperationException("TO support in the future!");
    }

    @Override
    public void close() {
        if (isClosed()) {
            logger.warning("The CacheManager has been closed, current close operation will be ignored!");
            return;
        }
        // 将所有的缓存对象都清除
        for (Map<KeyValueTypePair, Cache> cacheMap : cacheRepository.values()) {
            iterateCaches(cacheMap.values(), CLOSE_CACHE_OPERATION);
        }
        doClose();
        this.closed = true;
    }

    /**
     * 关闭时，子类可以实现的钩子方法
     */
    protected void doClose() {
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}
