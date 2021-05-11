package com.jizhi.geektime.function;

/**
 * 能抛出异常的 函数式方法 没有入参，没有返回值
 *
 * @author jizhi7
 * @since 1.0
 **/
@FunctionalInterface
public interface ThrowableAction {

    static void execute(ThrowableAction action) {
        try {
            action.execute();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    void execute() throws Throwable;

}
