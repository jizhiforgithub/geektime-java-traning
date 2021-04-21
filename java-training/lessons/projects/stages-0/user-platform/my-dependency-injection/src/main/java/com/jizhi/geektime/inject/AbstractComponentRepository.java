package com.jizhi.geektime.inject;

import com.jizhi.geektime.function.ThrowableFunction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 组件仓库的抽象实现
 *
 * @author jizhi7
 * @since 1.0
 **/
public abstract class AbstractComponentRepository implements ComponentRepository {

    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * 本地组件缓存
     */
    private Map<String, Object> componentsCache = new LinkedHashMap<>();

    @Override
    public <C> C getComponent(String name) {
        // 如果没有的话，就调用子类的获取，并放进去
        return (C) componentsCache.putIfAbsent(name, doGetComponent(name));
    }

    @Override
    public Set<String> getComponentNames() {
        return componentsCache.isEmpty() ? listComponentNames() : componentsCache.keySet();
    }

    /**
     * 通过指定 ThrowableFunction 返回计算结果
     *
     * @param argument         Function's argument
     * @param function         ThrowableFunction
     * @param ignoredException 是否忽略异常
     * @param <R>              返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <T, R> R executeInContext(T argument, ThrowableFunction<T, R> function, boolean ignoredException) {
        R result = null;
        try {
            result = function.apply(argument);
        } catch (Throwable throwable) {
            if (ignoredException) {
                logger.warning(throwable.getMessage());
            } else {
                throw new RuntimeException(throwable);
            }
        }
        return result;
    }

    protected abstract Set<String> listComponentNames();

    protected abstract Object doGetComponent(String name);
}
