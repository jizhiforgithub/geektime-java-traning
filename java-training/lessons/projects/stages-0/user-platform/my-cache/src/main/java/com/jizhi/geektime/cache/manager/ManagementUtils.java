package com.jizhi.geektime.cache.manager;

import javax.cache.Cache;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;
import javax.management.*;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

/**
 * 缓存 JMX 管理的工具类
 *
 * @author jizhi7
 * @since 1.0
 **/
public abstract class ManagementUtils {

    /**
     * 注册 {@link Cache} 缓存的 MXBean
     *
     * @param cache
     * @param <V>
     * @param <K>
     */
    public static <V, K> void registerCacheMXBeanIfRequired(Cache<K, V> cache) {
        CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
        // 配置开启了MXBean管理
        if (configuration.isManagementEnabled()) {
            ObjectName objectName = createObjectName(cache, "CacheConfiguration");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

            try {
                // 没有注册过
                if (!mBeanServer.isRegistered(objectName)) {
                    mBeanServer.registerMBean(createCacheMXBean(configuration), objectName);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static CacheMXBean createCacheMXBean(CompleteConfiguration configuration) {
        return new CacheMXBeanAdapter(configuration);
    }

    /**
     * 创建缓存的 MXBean 的objectName
     *
     * @param cache
     * @param type
     * @param <K>
     * @param <V>
     * @return
     */
    private static <K, V> ObjectName createObjectName(Cache<K, V> cache, String type) {
        // 配置属性
        Hashtable<String, String> props = new Hashtable<>();
        props.put("type", type);
        props.put("name", cache.getName());
        props.put("uri", getUri(cache));

        ObjectName objectName = null;
        try {
            objectName = new ObjectName("javax.cache", props);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        return objectName;
    }

    private static <V, K> String getUri(Cache<K, V> cache) {
        URI uri = cache.getCacheManager().getURI();
        try {
            return URLEncoder.encode(uri.toASCIIString(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
