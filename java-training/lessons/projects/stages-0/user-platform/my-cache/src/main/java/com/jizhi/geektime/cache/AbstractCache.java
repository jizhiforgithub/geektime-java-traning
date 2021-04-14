package com.jizhi.geektime.cache;

import com.jizhi.geektime.cache.configuration.ConfigurationUtils;
import com.jizhi.geektime.cache.event.CacheEntryEventPublisher;
import com.jizhi.geektime.cache.event.GenericCacheEntryEvent;
import com.jizhi.geektime.cache.integration.CompositeFallbackStorage;
import com.jizhi.geektime.cache.integration.FallbackStorage;
import com.jizhi.geektime.cache.manager.ManagementUtils;
import com.jizhi.geektime.cache.processor.MutableEntryAdapter;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.*;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * 缓存项抽象基类，实现了 Cache 接口 {@link javax.cache.Cache}
 *
 * @author jizhi7
 * @since 1.0
 **/
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    /**
     * 日志
     */
    protected final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * 缓存管理器
     */
    private final CacheManager cacheManager;

    /**
     * 缓存对象名称
     */
    private final String cacheName;

    /**
     * 配置
     */
    private final MutableConfiguration<K, V> configuration;

    /**
     * 缓存过期策略
     */
    private final ExpiryPolicy expiryPolicy;

    /**
     * 缓存加载器
     */
    private final CacheLoader<K, V> cacheLoader;

    /**
     * 缓存写
     */
    private final CacheWriter<K, V> cacheWriter;

    /**
     * 回调存储，默认实现是个组合实现
     */
    private final FallbackStorage defaultFallbackStorage;

    /**
     * 缓存实体事件发布器
     */
    private final CacheEntryEventPublisher entryEventPublisher;

    /**
     * 事件发布执行器
     */
    private final Executor executor;

    private volatile boolean closed = false;

    protected AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.configuration = ConfigurationUtils.mutableConfiguration(configuration);
        this.expiryPolicy = resolveExpiryPolicy(getConfiguration());
        this.cacheLoader = resolveCacheLoader(getConfiguration(), getClassLoader());
        this.cacheWriter = resolveCacheWriter(getConfiguration(), getClassLoader());
        // 默认是组合回调存储
        this.defaultFallbackStorage = new CompositeFallbackStorage(getClassLoader());
        this.entryEventPublisher = new CacheEntryEventPublisher();
        this.executor = ForkJoinPool.commonPool();
        // 注册监听器
        registerCacheEntryListenersFromConfiguration();
        // 注册MXBean
        ManagementUtils.registerCacheMXBeanIfRequired(this);
    }

    /**
     * 从配置内容中拿到监听器，注册缓存实体的监听器
     */
    private void registerCacheEntryListenersFromConfiguration() {
        this.configuration.getCacheEntryListenerConfigurations().forEach(this::registerCacheEntryListener);
    }

    /**
     * 根据配置内容 解析 缓存写入
     *
     * @param configuration 配置
     * @param classLoader
     * @return
     */
    private CacheWriter<K, V> resolveCacheWriter(MutableConfiguration<K, V> configuration, ClassLoader classLoader) {
        Factory<CacheWriter<? super K, ? super V>> cacheWriterFactory = configuration.getCacheWriterFactory();
        CacheWriter<K, V> cacheWriter = null;
        if (cacheWriterFactory != null) {
            cacheWriter = (CacheWriter<K, V>) cacheWriterFactory.create();
        }

        // 默认是基于文件的回调写入
        if (cacheWriter == null) {
            cacheWriter = this.defaultFallbackStorage;
        }
        return cacheWriter;
    }

    /**
     * 根据配置 解析 缓存加载器
     *
     * @param configuration 配置
     * @param classLoader   类加载器
     * @return
     */
    private CacheLoader<K, V> resolveCacheLoader(MutableConfiguration<K, V> configuration, ClassLoader classLoader) {
        Factory<CacheLoader<K, V>> cacheLoaderFactory = configuration.getCacheLoaderFactory();
        CacheLoader<K, V> cacheLoader = null;
        if (cacheLoaderFactory != null) {
            cacheLoader = cacheLoaderFactory.create();
        }
        // 如果没有定义缓存加载器，默认就是基于文件的缓存回调加载
        if (cacheLoader == null) {
            cacheLoader = this.defaultFallbackStorage;
        }
        return cacheLoader;
    }

    /**
     * 解析获取过期策略
     *
     * @param configuration
     * @return
     */
    private ExpiryPolicy resolveExpiryPolicy(MutableConfiguration<K, V> configuration) {
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        if (expiryPolicyFactory == null) {
            // 默认是永恒的过期策略，永不过期
            expiryPolicyFactory = EternalExpiryPolicy::new;
        }
        return expiryPolicyFactory.create();
    }


    public MutableConfiguration<K, V> getConfiguration() {
        return configuration;
    }

    protected ClassLoader getClassLoader() {
        return getCacheManager().getClassLoader();
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    @Override
    public V get(K key) {
        assertNotClosed();
        ExpirableEntry.requireKeyNotNull(key);
        ExpirableEntry<K, V> entry = null;
        try {
            entry = getEntry(key);
            // 访问时检查过期策略
            if (handleExpiryPolicyForAccess(entry)) {
                return null;
            }
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }

        // 如果没有
        if (entry == null && isReadThrough()) {
            return loadValue(key, true);
        }
        return getValue(entry);
    }

    /**
     * 加载 Value
     *
     * @param key
     * @return
     */
    private V loadValue(K key) {
        return getCacheLoader().load(key);
    }

    private V loadValue(K key, boolean storedEntry) {
        V value = loadValue(key);
        // 存一下
        if (storedEntry && value != null) {
            put(key, value);
        }
        return value;
    }

    /**
     * 从entry 中获取value
     *
     * @param entry
     * @param <K>
     * @param <V>
     * @return
     */
    private static <K, V> V getValue(Entry<K, V> entry) {
        return entry == null ? null : entry.getValue();
    }

    protected final boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    protected CacheLoader<K, V> getCacheLoader() {
        return this.cacheLoader;
    }

    /**
     * 子类实现的获取缓存实体
     *
     * @param key
     * @return
     */
    protected abstract ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException;

    /**
     * 获取所有的key的value
     *
     * @param keys
     * @return
     */
    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        return containsEntry(key);
    }

    /**
     * 根据key判断是否包含缓存实体
     *
     * @param key
     * @return
     */
    protected abstract boolean containsEntry(K key) throws CacheException, ClassCastException;

    /**
     * 加载所有的key
     *
     * @param keys                  key
     * @param replaceExistingValues 是否替换已存在的值
     * @param completionListener    监听器
     */
    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        assertNotClosed();
        if (!configuration.isReadThrough()) {
            completionListener.onCompletion();
            return;
        }

        // 执行
        CompletableFuture.runAsync(() -> {
            for (K key : keys) {
                // 加载key
                V value = loadValue(key, false);
                if (replaceExistingValues) {
                    // 替换值
                    replace(key, value);
                } else {
                    put(key, value);
                }
            }
        }, executor).whenComplete((v, e) -> {
            // 加载完成之后
            if (completionListener != null) {
                // 有错误
                if (e instanceof Exception && e.getCause() instanceof Exception) {
                    completionListener.onException((Exception) e.getCause());
                }
                // 没错误
                else {
                    completionListener.onCompletion();
                }
            }
        });
    }

    /**
     * 放入缓存
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        assertNotClosed();
        Entry<K, V> entry = null;
        try {
            // 之前没有key，床技安一个新的
            if (!containsKey(key)) {
                entry = createAndPutEntry(key, value);
            }
            // 更新
            else {
                entry = updateEntry(key, value);
            }
        } finally {
            // 写入
            writeEntryIfWriteThrough(entry);
        }
    }

    /**
     * 写入实体
     *
     * @param entry
     */
    private void writeEntryIfWriteThrough(Entry<K, V> entry) {
        if (entry != null && isWriteThrough()) {
            getCacheWriter().write(entry);
        }
    }

    protected final boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    protected CacheWriter<K, V> getCacheWriter() {
        return this.cacheWriter;
    }

    /**
     * 新创建一个 cache entry，并且放入
     *
     * @param key
     * @param value
     * @return
     */
    private Entry<K, V> createAndPutEntry(K key, V value) {
        // 创建
        ExpirableEntry<K, V> newEntry = createEntry(key, value);
        // 过期了
        if (handleExpiryPolicyForCreation(newEntry)) {
            return null;
        }
        // 放入
        putEntry(newEntry);
        // 发布放入事件
        publishCreatedEvent(key, value);
        return newEntry;
    }

    /**
     * 放入缓存对象中
     *
     * @param newEntry
     */
    protected abstract void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException;

    /**
     * 创建缓存实体
     *
     * @param key
     * @param value
     * @return
     */
    private ExpirableEntry<K, V> createEntry(K key, V value) {
        return ExpirableEntry.of(key, value);
    }

    /**
     * 更新实体
     *
     * @param key
     * @param value
     * @return
     */
    private Entry<K, V> updateEntry(K key, V value) {
        // 获取旧数据
        ExpirableEntry<K, V> oldEntry = getEntry(key);

        V oldValue = oldEntry.getValue();
        // 设置新数据
        oldEntry.setValue(value);
        // 放入新数据
        putEntry(oldEntry);
        // 发布更新事件
        publishUpdatedEvent(key, oldValue, value);
        // 过期了
        if (handleExpiryPolicyForUpdate(oldEntry)) {
            return null;
        }
        return oldEntry;
    }

    /**
     * 获取并放入
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public V getAndPut(K key, V value) {
        // 获取旧值
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        // 放入新值
        put(key, value);
        return oldValue;
    }

    /**
     * 放入所有的
     *
     * @param map
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 如果缺失的化，就放入
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 移除
     *
     * @param key
     * @return
     */
    @Override
    public boolean remove(K key) {
        assertNotClosed();
        ExpirableEntry.requireKeyNotNull(key);
        boolean removed = false;
        try {
            ExpirableEntry<K, V> oldEntry = removeEntry(key);
            removed = oldEntry != null;
            if (removed) {
                // 发布移出事件
                publishRemovedEvent(key, oldEntry.getValue());
            }
        } finally {
            // 删除写入的数据
            deleteIfWriteThrough(key);
        }
        return removed;
    }

    /**
     * 删除写入
     *
     * @param key
     */
    private void deleteIfWriteThrough(K key) {
        if (isWriteThrough()) {
            getCacheWriter().delete(key);
        }
    }

    /**
     * 删除对应的key val
     *
     * @param key
     * @param oldValue
     * @return
     */
    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取并移除
     *
     * @param key
     * @return
     */
    @Override
    public V getAndRemove(K key) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        remove(key);
        return oldValue;
    }

    /**
     * 替换
     *
     * @param key      key
     * @param oldValue 旧值
     * @param newValue 新值
     * @return
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        ExpirableEntry.requireValueNotNull(oldValue);
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            put(key, newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean replace(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取并替换
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public V getAndReplace(K key, V value) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        if (oldValue != null) {
            put(key, value);
        }
        return oldValue;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        for (K key : keys) {
            remove(key);
        }
    }

    /**
     * 移除所有
     */
    @Override
    public void removeAll() {
        removeAll(keySet());
    }

    /**
     * 所有的key集合
     *
     * @return
     */
    protected abstract Set<? extends K> keySet();

    /**
     * 清除
     */
    @Override
    public void clear() {
        assertNotClosed();
        // 清除所有实体
        clearEntries();
        // 清除写入
        defaultFallbackStorage.destroy();
    }

    /**
     * 清除所有实体
     */
    protected abstract void clearEntries() throws CacheException;

    /**
     * 获取配置
     *
     * @param clazz
     * @param <C>
     * @return
     */
    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        if (!Configuration.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The class must be inherited of " + Configuration.class.getName());
        }
        return (C) ConfigurationUtils.immutableConfiguration(getConfiguration());
    }

    /**
     * 调用 EntryProcessor 的方法
     *
     * @param key
     * @param entryProcessor
     * @param arguments
     * @param <T>
     * @return
     * @throws EntryProcessorException
     */
    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        // 创建一个多实体适配器
        MutableEntry<K, V> mutableEntry = MutableEntryAdapter.of(key, this);
        // 执行
        return entryProcessor.process(mutableEntry, arguments);
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        Map<K, EntryProcessorResult<T>> resultMap = new LinkedHashMap<>();
        for (K key : keys) {
            resultMap.put(key, () -> invoke(key, entryProcessor, arguments));
        }
        return resultMap;
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * 关闭
     */
    @Override
    public void close() {
        if (isClosed()) {
            return;
        }
        doClose();
        closed = true;
    }

    /**
     * 子类可以实现的关闭钩子方法
     */
    protected void doClose() {

    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return getCacheManager().unwrap(clazz);
    }

    /**
     * 注册监听器
     *
     * @param cacheEntryListenerConfiguration
     */
    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    /**
     * 注销监听器
     *
     * @param cacheEntryListenerConfiguration
     */
    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.deregisterCacheEntryListener(cacheEntryListenerConfiguration);
    }

    /**
     * 缓存实体迭代器
     *
     * @return
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        List<Entry<K, V>> entries = new LinkedList<>();
        for (K key : keySet()) {
            V value = get(key);
            entries.add(ExpirableEntry.of(key, value));
        }
        return entries.iterator();
    }

    /**
     * 判定缓存没有关闭
     */
    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }

    /**
     * 创建时，检查过期策略
     *
     * @param newEntry
     * @return
     */
    private boolean handleExpiryPolicyForCreation(ExpirableEntry<K, V> newEntry) {
        return handleExpiryPolicy(newEntry, getExpiryForCreation(), false);
    }

    /**
     * 访问时检查过期策略。如果过期了就移除
     *
     * @param entry
     * @return
     */
    private boolean handleExpiryPolicyForAccess(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForAccess(), true);
    }

    /**
     * 更新时，检查过期策略。如果过期了就移除
     *
     * @param oldEntry
     * @return
     */
    private boolean handleExpiryPolicyForUpdate(ExpirableEntry<K, V> oldEntry) {
        return handleExpiryPolicy(oldEntry, getExpiryForUpdate(), true);
    }

    /**
     * 获取创建时的过期时长
     *
     * @return
     */
    protected final Duration getExpiryForCreation() {
        return getDuration(expiryPolicy::getExpiryForCreation);
    }

    /**
     * 获取访问时的过期时长
     *
     * @return
     */
    protected final Duration getExpiryForAccess() {
        return getDuration(expiryPolicy::getExpiryForAccess);
    }

    /**
     * 获取更新时的过期时长
     *
     * @return
     */
    protected final Duration getExpiryForUpdate() {
        return getDuration(expiryPolicy::getExpiryForUpdate);
    }

    /**
     * 获取时长
     *
     * @param durationSupplier
     * @return
     */
    private Duration getDuration(Supplier<Duration> durationSupplier) {
        Duration duration = null;
        try {
            duration = durationSupplier.get();
        } catch (Throwable ignored) {
            // Default
            duration = Duration.ETERNAL;
        }
        return duration;
    }

    /**
     * 从配置中解析过期策略，默认永不过期策略
     *
     * @param configuration 缓存配置
     * @return
     */
    private ExpiryPolicy resolveExpiryPolicy(CompleteConfiguration<?, ?> configuration) {
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        if (expiryPolicyFactory == null) {
            // 默认永不过期策略
            expiryPolicyFactory = EternalExpiryPolicy::new;
        }
        return expiryPolicyFactory.create();
    }

    /**
     * 处理过期策略
     *
     * @param entry               缓存实体
     * @param duration            过期时长
     * @param removedExpiredEntry 是否移除已过期的实体
     * @return
     */
    private boolean handleExpiryPolicy(ExpirableEntry<K, V> entry, Duration duration, boolean removedExpiredEntry) {
        if (entry == null) {
            return false;
        }
        // 是否过期
        boolean expired = false;
        if (entry.isExpired()) {
            expired = true;
        } else if (duration != null) {
            // 过期时间是0，过期
            if (duration.isZero()) {
                expired = true;
            } else {
                // 获取当前时间和过期时间的和
                long timestamp = duration.getAdjustedTime(System.currentTimeMillis());
                // 更新实体的过期时间
                entry.setTimestamp(timestamp);
            }
        }

        // 如果要移除，且过期了
        if (removedExpiredEntry && expired) {
            // Remove Cache.Entry
            K key = entry.getKey();
            V value = entry.getValue();
            // 移除
            removeEntry(key);
            // 发布过期事件
            publishExpiredEvent(key, value);
        }
        return expired;
    }

    /**
     * 发布创建事件
     *
     * @param key
     * @param value
     */
    private void publishCreatedEvent(K key, V value) {
        entryEventPublisher.publish(GenericCacheEntryEvent.createdEvent(this, key, value));
    }

    /**
     * 发布更新事件
     *
     * @param key
     * @param oldValue
     * @param value
     */
    private void publishUpdatedEvent(K key, V oldValue, V value) {
        entryEventPublisher.publish(GenericCacheEntryEvent.updatedEvent(this, key, oldValue, value));
    }

    /**
     * 发布过期事件
     *
     * @param key
     * @param oldValue
     */
    private void publishExpiredEvent(K key, V oldValue) {
        entryEventPublisher.publish(GenericCacheEntryEvent.expiredEvent(this, key, oldValue));
    }

    /**
     * 发布移出事件
     *
     * @param key
     * @param oldValue
     */
    private void publishRemovedEvent(K key, V oldValue) {
        entryEventPublisher.publish(GenericCacheEntryEvent.removedEvent(this, key, oldValue));
    }

    /**
     * 子类要实现的移除实体
     *
     * @param key
     */
    protected abstract ExpirableEntry<K, V> removeEntry(K key);
}
