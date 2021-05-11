package com.jizhi.geektime.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * 优先性的转换器，可以进行排序
 *
 * @author jizhi7
 * @since 1.0
 **/
public class PrioritizedConverter<T> implements Converter<T>, Comparable<PrioritizedConverter<T>> {

    public Converter<T> getConverter() {
        return converter;
    }

    /**
     * 转换器
     */
    private final Converter<T> converter;

    /**
     * 优先级
     */
    private final int priority;

    public PrioritizedConverter(Converter<T> converter, int priority) {
        this.converter = converter;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * priority 属性越大，优先级越高
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(PrioritizedConverter<T> other) {
        return Integer.compare(other.getPriority(), this.priority);
    }

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        return converter.convert(value);
    }
}
