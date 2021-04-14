package com.jizhi.geektime.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;

/**
 * 缓存实体事件监听器适配器，
 * 将 {@link CacheEntryListenerConfiguration} 的缓存实体监听配置适配成
 * 标准的 {@link ConditionalCacheEntryEventListener} 条件缓存实体事件监听器
 *
 * @author jizhi7
 * @since 1.0
 */
public class CacheEntryEventListenerAdapter<K, V> implements ConditionalCacheEntryEventListener<K, V> {

    /**
     * 事件类型和处理方法名称映射
     */
    private static List<Object> eventTypesAndHandleMethodNames = asList(
            EventType.CREATED, "onCreated",
            EventType.UPDATED, "onUpdated",
            EventType.EXPIRED, "onExpired",
            EventType.REMOVED, "onRemoved"
    );

    /**
     * 缓存实体监听配置
     */
    private final CacheEntryListenerConfiguration<K, V> configuration;

    /**
     * 缓存实体事件过滤器
     */
    private final CacheEntryEventFilter<? super K, ? super V> cacheEntryEventFilter;

    /**
     * 缓存实体监听器
     */
    private final CacheEntryListener<? super K, ? super V> cacheEntryListener;

    /**
     * 支持的缓存事件类型和处理方法的映射表
     */
    private final Map<EventType, Method> eventTypeMethods;

    /**
     * 事件执行器
     */
    private final Executor executor;

    public CacheEntryEventListenerAdapter(CacheEntryListenerConfiguration<K, V> configuration) {
        this.configuration = configuration;
        this.cacheEntryEventFilter = getCacheEntryEventFilter(configuration);
        this.cacheEntryListener = configuration.getCacheEntryListenerFactory().create();
        this.eventTypeMethods = determineEventTypeMethods(cacheEntryListener);
        this.executor = getExecutor(configuration);
    }

    /**
     * 获取执行器，
     * 如果事件监听配置配置了同步，就直接返回同步执行
     *  否则就返回 ForkJoinPool 的线程池异步执行
     * @param configuration
     * @return
     */
    private Executor getExecutor(CacheEntryListenerConfiguration<K,V> configuration) {
        Executor executor = null;
        // 同步执行
        if (configuration.isSynchronous()) {
            executor = Runnable::run;
        } else {
            executor = ForkJoinPool.commonPool();
        }
        return executor;
    }

    /**
     * 初始化事件类型和事件处理方法的映射表
     * @param cacheEntryListener 事件监听器
     * @return
     */
    private Map<EventType,Method> determineEventTypeMethods(CacheEntryListener<? super K,? super V> cacheEntryListener) {
        Map<EventType, Method> eventTypeMethods = new HashMap<>(EventType.values().length);
        // 监听器类
        Class<?> cacheEntryListenerClass = cacheEntryListener.getClass();
        // 遍历事件操作和方法映射关系
        for (int i = 0; i < eventTypesAndHandleMethodNames.size(); ) {
            // 事件类型
            EventType eventType = (EventType) eventTypesAndHandleMethodNames.get(i++);
            // 方法名称
            String handleMethodName = (String) eventTypesAndHandleMethodNames.get(i++);
            try {
                // 在监听器类中找到该方法
                Method handleMethod = cacheEntryListenerClass.getMethod(handleMethodName, Iterable.class);
                if (handleMethod != null) {
                    eventTypeMethods.put(eventType, handleMethod);
                }
            } catch (NoSuchMethodException ignored) {
            }

        }
        return unmodifiableMap(eventTypeMethods);
    }

    /**
     * 获取缓存实体事件过滤器
     * @param configuration 缓存实体监听配置
     * @return
     */
    private CacheEntryEventFilter<? super K, ? super V> getCacheEntryEventFilter(CacheEntryListenerConfiguration<K, V> configuration) {
        Factory<CacheEntryEventFilter<? super K, ? super V>> factory = configuration.getCacheEntryEventFilterFactory();
        CacheEntryEventFilter<? super K, ? super V> filter = null;

        if (factory != null) {
            filter = factory.create();
        }
        // 如果没有配置过滤器，默认全是全部事件都通知
        if (filter == null) {
            filter = e -> true;
        }
        return filter;
    }

    /**
     * 是否支持该事件，通过获取支持事件类型表和过滤器来判断
     * @param event
     * @return
     * @throws CacheEntryListenerException
     */
    @Override
    public boolean supports(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException {
        return supportsEventType(event) && cacheEntryEventFilter.evaluate(event);
    }

    /**
     * 查询事件支持表，判断该事件是否支持
     * @param event
     * @return
     */
    private boolean supportsEventType(CacheEntryEvent<? extends K, ? extends V> event) {
        return getSupportedEventTypes().contains(event.getEventType());
    }

    /**
     * 有事件发生的时候
     * @param event
     */
    @Override
    public void onEvent(CacheEntryEvent<? extends K, ? extends V> event) {
        // 如果不是支持的事件
        if (!supports(event)) {
            return;
        }

        // 事件类型
        EventType eventType = event.getEventType();
        // 监听器的处理方法
        Method handleMethod = eventTypeMethods.get(eventType);

        // 执行器执行，有可能异步，也可能同步。看config
        executor.execute(() -> {
            try {
                // 反射调用监听器的方法
                handleMethod.invoke(cacheEntryListener, singleton(event));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new CacheEntryListenerException(e);
            }
        });
    }

    @Override
    public Set<EventType> getSupportedEventTypes() {
        return eventTypeMethods.keySet();
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public int hashCode() {
        return configuration.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CacheEntryEventListenerAdapter)) {
            return false;
        }
        CacheEntryEventListenerAdapter another = (CacheEntryEventListenerAdapter) object;
        return this.configuration.equals(another.configuration);
    }

}
