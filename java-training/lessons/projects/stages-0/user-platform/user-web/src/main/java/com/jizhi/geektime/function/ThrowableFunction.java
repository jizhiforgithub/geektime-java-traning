package com.jizhi.geektime.function;

/**
 * 能抛出异常的函数接口
 * @author jizhi7
 * @since 1.0
 **/

@FunctionalInterface
public interface ThrowableFunction<T, R> {

    /**
     *  该函数，消费参数，返回数据
     * @param t 进入函数的参数
     * @return 函数返回的数据
     * @throws Throwable 函数抛出的异常
     */
    R apply(T t) throws Throwable;

}
