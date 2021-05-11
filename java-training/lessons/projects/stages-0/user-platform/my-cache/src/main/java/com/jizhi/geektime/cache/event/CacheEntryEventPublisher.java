package com.jizhi.geektime.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * 缓存entry事件发布器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class CacheEntryEventPublisher {

    /**
     * 所有的事件监听器
     */
    private List<ConditionalCacheEntryEventListener> listeners = new LinkedList<>();

    /**
     * 发布事件
     *
     * @param event 事件
     * @param <K>
     * @param <V>
     */
    public <K, V> void publish(CacheEntryEvent<? extends K, ? extends V> event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

    /**
     * 注册监听器
     *
     * @param configuration
     */
    public void registerCacheEntryListener(CacheEntryListenerConfiguration configuration) {
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);
        listeners.add(listenerAdapter);
    }

    /**
     * 注销监听器
     *
     * @param configuration
     */
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration configuration) {
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);
        listeners.remove(listenerAdapter);
    }

}
