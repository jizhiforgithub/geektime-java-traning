package com.jizhi.geektime.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jizhi.geektime.cache.AbstractCache;
import com.jizhi.geektime.cache.ExpirableEntry;
import com.jizhi.geektime.serialize.SerializeProvider;
import io.lettuce.core.api.sync.RedisCommands;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于redis的缓存项
 *
 * @author jizhi7
 * @since 1.0
 **/
public class LettuceRedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<Object, V> {

    private final RedisCommands<String, String> redis;

    protected LettuceRedisCache(CacheManager cacheManager, String cacheName,
                                Configuration<Object, V> configuration,
                                RedisCommands<String, String> redis) {
        super(cacheManager, cacheName, configuration);
        this.redis = redis;
    }

    @Override
    protected ExpirableEntry<Object, V> getEntry(Object key) throws CacheException, ClassCastException {
        String serializeKey = serialize(key);
        return getEntry(serializeKey);
    }

    protected ExpirableEntry<Object, V> getEntry(String serializeKey) throws CacheException, ClassCastException {
        String value = redis.get(serializeKey);
        return ExpirableEntry.of(deserialize(serializeKey, getConfiguration().getKeyType()), deserialize(value, getConfiguration().getValueType()));
    }

    /**
     * 反序列化
     *
     * @param str
     * @return
     */
    private <T> T deserialize(String str, Class clazz) {
        /*if (str == null) {
            return null;
        }
        T obj = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            obj = (T) objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return obj;*/
        return (T) SerializeProvider.getSerialize().deserialize(str, clazz);
    }

    /**
     * 序列化
     *
     * @param value
     * @return
     */
    private String serialize(Object value) {
        /*ObjectMapper objectMapper = new ObjectMapper();
        try {
            String str = objectMapper.writeValueAsString(value);
            return str;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
       return SerializeProvider.getSerialize().serialize(value, String.class);
    }

    @Override
    protected boolean containsEntry(Object key) throws CacheException, ClassCastException {
        String keyBytes = serialize(key);
        return redis.get(keyBytes) == null ? false : true;
    }

    @Override
    protected void putEntry(ExpirableEntry<Object, V> entry) throws CacheException, ClassCastException {
        String keyBytes = serialize(entry.getKey());
        String valueBytes = serialize(entry.getValue());
        redis.set(keyBytes, valueBytes);
    }

    @Override
    protected Set<?> keySet() {
        List<String> keys = redis.keys("*");
        return keys.stream().map(k -> deserialize(k, getConfiguration().getKeyType())).collect(Collectors.toSet());
    }

    @Override
    protected void clearEntries() throws CacheException {
        keySet().forEach(this::removeEntry);
    }

    @Override
    protected ExpirableEntry<Object, V> removeEntry(Object key) {
        String keyBytes = serialize(key);
        ExpirableEntry<Object, V> oldEntry = getEntry(keyBytes);
        redis.del(keyBytes);
        return oldEntry;
    }
}
