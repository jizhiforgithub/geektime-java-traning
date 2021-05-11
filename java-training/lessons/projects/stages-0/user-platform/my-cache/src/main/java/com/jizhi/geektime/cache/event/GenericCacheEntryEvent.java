package com.jizhi.geektime.cache.event;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;

import static java.util.Objects.requireNonNull;

/**
 * 通用的缓存实体事件，继承至java cache 的缓存事件
 *
 * @author jizhi7
 * @since 1.0
 **/
public class GenericCacheEntryEvent<K, V> extends CacheEntryEvent<K, V> {

    /**
     * 缓存的Key
     */
    private final K key;

    /**
     * 缓存的旧值
     */
    private final V oldValue;

    /**
     * 缓存的新值
     */
    private final V value;


    public GenericCacheEntryEvent(Cache source, EventType eventType, K key, V oldValue, V value) {
        super(source, eventType);
        requireNonNull(key, "The key must not be null!");
        requireNonNull(value, "The value must not be null!");
        this.key = key;
        this.oldValue = oldValue;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V getOldValue() {
        return oldValue;
    }

    @Override
    public boolean isOldValueAvailable() {
        return oldValue != null;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return getSource().getCacheManager().unwrap(clazz);
    }

    @Override
    public String toString() {
        return "GenericCacheEntryEvent{" +
                "key=" + getKey() +
                ", oldValue=" + getOldValue() +
                ", value=" + getValue() +
                ", evenType=" + getEventType() +
                ", source=" + getSource().getName() +
                '}';
    }

    ///事件相关操作////////////////

    /**
     * 创建事件
     *
     * @param source 源缓存
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryEvent<K, V> createdEvent(Cache source, K key, V value) {
        return of(source, EventType.CREATED, key, null, value);
    }

    /**
     * 更新
     *
     * @param source
     * @param key
     * @param oldValue
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryEvent<K, V> updatedEvent(Cache source, K key, V oldValue, V value) {
        return of(source, EventType.UPDATED, key, oldValue, value);
    }

    /**
     * 缓存过期
     *
     * @param source
     * @param key
     * @param oldValue
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryEvent<K, V> expiredEvent(Cache source, K key, V oldValue) {
        return of(source, EventType.EXPIRED, key, oldValue, oldValue);
    }

    /**
     * 缓存移除
     *
     * @param source
     * @param key
     * @param oldValue
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> CacheEntryEvent<K, V> removedEvent(Cache source, K key, V oldValue) {
        return of(source, EventType.REMOVED, key, oldValue, oldValue);
    }

    public static <K, V> CacheEntryEvent<K, V> of(Cache source, EventType eventType, K key, V oldValue, V value) {
        return new GenericCacheEntryEvent<>(source, eventType, key, oldValue, value);
    }

}
