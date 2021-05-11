package com.jizhi.geektime.redis;

import com.jizhi.geektime.cache.AbstractCacheManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.resource.ClientResources;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;

/**
 * redis实现的缓存管理器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class LettuceRedisCacheManager extends AbstractCacheManager {

    private final RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;


    public LettuceRedisCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        ArrayList<RedisURI> list = new ArrayList<>();
        list.add(RedisURI.create(uri.toString()));
        //this.redisClient = RedisClusterClient.create(list);
        //this.connection = redisClient.connect();
        redisClient = RedisClient.create(uri.toString());
        connection = redisClient.connect();
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        RedisCommands<String, String> sync = connection.sync();
        return new LettuceRedisCache(this, cacheName, configuration, sync);
    }

    @Override
    protected void doClose() {
        connection.close();
        redisClient.shutdown();
    }
}
