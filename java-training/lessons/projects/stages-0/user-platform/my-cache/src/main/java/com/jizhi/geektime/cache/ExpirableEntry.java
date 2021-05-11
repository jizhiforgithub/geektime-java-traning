package com.jizhi.geektime.cache;

import javax.cache.Cache;
import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * 可过期的缓存实体 是 {@link javax.cache.Cache.Entry} 的实现
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ExpirableEntry<K, V> implements Cache.Entry<K, V>, Serializable {

    /**
     * 缓存实体的 key
     */
    private final K key;

    /**
     * 缓存的value
     */
    private V value;

    /**
     * 时间戳，判定是否过期
     */
    private long timestamp;

    public ExpirableEntry(K key, V value) {
        // 判定key不为空
        requireKeyNotNull(key);
        this.key = key;
        this.setValue(value);
        this.timestamp = Long.MAX_VALUE;
    }

    /**
     * key不为空
     *
     * @param key
     * @param <K>
     */
    public static <K> void requireKeyNotNull(K key) {
        requireNonNull(key, "The key must not be null.");
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    /**
     * 设置valu值
     *
     * @param value
     */
    public void setValue(V value) {
        requireValueNotNull(value);
        this.value = value;
    }

    public static <V> void requireValueNotNull(V value) {
        requireNonNull(value, "The value must not be null.");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 判定时候已经过期
     *
     * @return
     */
    public boolean isExpired() {
        // 如果系统当前时间 大于 时间戳就是过期
        return System.currentTimeMillis() >= timestamp;
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

    @Override
    public String toString() {
        return "ExpirableEntry{" +
                "key=" + key +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }

    public static <K, V> ExpirableEntry<K, V> of(Map.Entry<K, V> entry) {
        return new ExpirableEntry(entry.getKey(), entry.getValue());
    }

    public static <K, V> ExpirableEntry<K, V> of(K key, V value) {
        return new ExpirableEntry(key, value);
    }

}
