package com.jizhi.geektime.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.*;
import java.util.EventListener;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 缓存实体事件条件监听器，可以根据不同的事件分类监听
 * 继承至 java 的 {@link EventListener} 事件监听器
 *
 * @author jizhi7
 * @see CacheEntryListener java的缓存实体事件监听器
 * @see CacheEntryEventFilter java的缓存实体事件过滤器
 * @see CacheEntryListenerConfiguration java的缓存实体监听配置
 * @since 1.0
 **/
public interface ConditionalCacheEntryEventListener<K, V> extends CacheEntryListener {

    /**
     * 判定是否支持该事件类型的监听
     *
     * @param event
     * @return
     * @throws CacheEntryListenerException
     */
    boolean supports(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException;

    /**
     * 当事件到达的时候，会调用
     *
     * @param event
     */
    void onEvent(CacheEntryEvent<? extends K, ? extends V> event);

    /**
     * 多个时间到达的时候，调用
     *
     * @param events
     */
    default void onEvents(Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        events.forEach(this::onEvent);
    }

    /**
     * 获取支持的事件类型
     *
     * @return
     */
    Set<EventType> getSupportedEventTypes();

    /**
     * 获取执行器，可能是多线程的执行器，也可能是单线程的同步执行
     *
     * @return
     */
    Executor getExecutor();

    @Override
    int hashCode();

    @Override
    boolean equals(Object object);

}
