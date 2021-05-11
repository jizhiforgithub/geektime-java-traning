package com.jizhi.geektime.cache;

import com.jizhi.geektime.cache.configuration.ConfigurationUtils;
import com.jizhi.geektime.cache.event.TestCacheEntryListener;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 缓存测试类
 *
 * @author jizhi7
 * @since 1.0
 **/
public class CachingTest {

    public static void main(String[] args) {
        CachingTest test = new CachingTest();
        //test.testSampleInMemory();
        test.testSampleRedis();
    }

    @Test
    public void testSampleInMemory() {
        // SPI获取的
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("in-memory://localhost/"), null);

        // 配置
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setManagementEnabled(true)
                        .setTypes(String.class, Integer.class);

        // 缓存
        Cache<String, Integer> simpleCache = cacheManager.createCache("simpleCache", config);

        // 添加监听
        simpleCache.registerCacheEntryListener(ConfigurationUtils.cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // 新增
        String key = "key";
        simpleCache.put(key, 1);

        // 更新
        simpleCache.put(key, 2);

        // 获取
        Integer value2 = simpleCache.get(key);
        assertEquals(Integer.valueOf(2), value2);

        // 删除
        simpleCache.remove(key);
        assertNull(simpleCache.get(key));

    }

    @Test
    public void testSampleRedis() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("redis://127.0.0.1:6379/4"), null);
        // 配置
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("redisCache", config);

        // add listener
        cache.registerCacheEntryListener(ConfigurationUtils.cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // cache operations
        String key = "redis-key";
        Integer value1 = 1;
        cache.put(key, value1);

        // update
        value1 = 2;
        cache.put(key, value1);
        cache.put("k3", value1);
        cache.put("u5", value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);

        Iterator<Cache.Entry<String, Integer>> iterator = cache.iterator();
        while (iterator.hasNext()) {
            Cache.Entry<String, Integer> next = iterator.next();
            System.out.println("key: " + next.getKey() + ";value:" + next.getValue());
        }
        //cache.remove(key);
        //assertNull(cache.get(key));
    }

    @Test
    public void testLettuce() {

    }

    @Test
    public void testJedis() {

    }

}
