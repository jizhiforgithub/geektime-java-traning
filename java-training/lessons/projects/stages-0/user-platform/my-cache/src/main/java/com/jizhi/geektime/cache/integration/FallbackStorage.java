package com.jizhi.geektime.cache.integration;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.Comparator;

/**
 * 回调存储接口， {@link CacheLoader} 和 {@link CacheWriter} 的共同接口
 *  加载缓存数据，写缓存数据。持久化支持存储
 * @author jizhi7
 * @since 1.0
 **/
public interface FallbackStorage<K, V> extends CacheLoader<K, V> , CacheWriter<K, V> {

    /**
     * 回调存储优先级比较器
     */
    Comparator<FallbackStorage> PRIORITY_COMPARATOR = new PriorityComparator();

    /**
     * 优先级
     * @return
     */
    int getPriority();

    /**
     * 销毁方法
     */
    void destroy();

    /**
     * 回调存储优先级比较器实现类
     */
    class PriorityComparator implements Comparator<FallbackStorage> {

        @Override
        public int compare(FallbackStorage o1, FallbackStorage o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }
}
