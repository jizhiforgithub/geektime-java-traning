package com.jizhi.geektime.redis;

import com.jizhi.geektime.cache.AbstractCache;
import com.jizhi.geektime.cache.ExpirableEntry;
import com.jizhi.geektime.serialize.CacheSerialize;
import com.jizhi.geektime.serialize.SerializeProvider;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于redis的缓存项
 *
 * @author jizhi7
 * @since 1.0
 **/
public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<Object, V> {

    private final Jedis jedis;

    protected JedisCache(CacheManager cacheManager, String cacheName, Configuration<Object, V> configuration, Jedis jedis) {
        super(cacheManager, cacheName, configuration);
        this.jedis = jedis;
    }

    @Override
    protected ExpirableEntry<Object, V> getEntry(Object key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        return getEntry(keyBytes);
    }

    protected ExpirableEntry<Object, V> getEntry(byte[] keyBytes) throws CacheException, ClassCastException {
        byte[] valueBytes = jedis.get(keyBytes);
        return ExpirableEntry.of(deserialize(keyBytes), deserialize(valueBytes));
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    private <T> T deserialize(byte[] bytes) {
        /*if (bytes == null) {
            return null;
        }
        T obj = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            obj = (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException(e);
        }
        return obj;*/
        return (T) CacheSerialize.getProvider().getSerialize().deserialize(bytes, getConfiguration().getValueType());
    }

    /**
     * 序列化
     *
     * @param value
     * @return
     */
    private byte[] serialize(Object value) {
       /* byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(value);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;*/
        return CacheSerialize.getProvider().getSerialize().serialize(value, byte[].class);
    }

    @Override
    protected boolean containsEntry(Object key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        return jedis.exists(keyBytes);
    }

    @Override
    protected void putEntry(ExpirableEntry<Object, V> entry) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(entry.getKey());
        byte[] valueBytes = serialize(entry.getValue());
        jedis.set(keyBytes, valueBytes);
    }

    @Override
    protected Set<?> keySet() {
        Set<byte[]> keys = jedis.keys("*".getBytes());
        return keys.stream().map(this::deserialize).collect(Collectors.toSet());
    }

    @Override
    protected void clearEntries() throws CacheException {
        keySet().forEach(this::removeEntry);
    }

    @Override
    protected ExpirableEntry<Object, V> removeEntry(Object key) {
        byte[] keyBytes = serialize(key);
        ExpirableEntry<Object, V> oldEntry = getEntry(keyBytes);
        jedis.del(keyBytes);
        return oldEntry;
    }
}
