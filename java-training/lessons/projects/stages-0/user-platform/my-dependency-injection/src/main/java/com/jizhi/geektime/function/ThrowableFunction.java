package com.jizhi.geektime.function;

import javax.naming.Context;

/**
 * 能抛出异常的函数接口
 *
 * @author jizhi7
 * @since 1.0
 **/

@FunctionalInterface
public interface ThrowableFunction<T, R> {

    static <T, R> R execute(T t, ThrowableFunction<T, R> function) {
        return function.execute(t);
    }


    default R execute(T t) throws RuntimeException {
        R result = null;
        try {
            result = apply(t);
        } catch (Throwable e) {
            throw new RuntimeException(e.getCause());
        }
        return result;
    }

    /**
     * 该函数，消费参数，返回数据
     *
     * @param t 进入函数的参数
     * @return 函数返回的数据
     * @throws Throwable 函数抛出的异常
     */
    R apply(T t) throws Throwable;

}
