package com.jizhi.geektime.projects.user.transaction.annotation;

/**
 * 事务传播级别
 * @author jizhi7
 * @since 1.0
 **/
public final class LocalPropagation {

    /**
     * 默认的，如果当前没有事务就创建一个来执行，有就不创建了
     */
    public static final int	REQUIRED = 0;

    /**
     * 支持事务，当前有事务就用，没有就不用
     */
    public static final int PROPAGATION_SUPPORTS = 1;

    /**
     * 支持当前事务，如果没有事务就抛出一个异常
     */
    public static final int PROPAGATION_MANDATORY = 2;

    /**
     * 总是开启一个新的事务
     */
    public static final int PROPAGATION_REQUIRES_NEW = 3;

    /**
     * 不以事务执行
     */
    public static final int PROPAGATION_NOT_SUPPORTED = 4;

    /**
     * 不支持事务，如果当前上下文有事务，就抛出一个异常
     */
    public static final int PROPAGATION_NEVER = 5;

    /**
     * 嵌套事务，物理上就开启一个事务，使用savePoint事前嵌套事务
     */
    public static final int PROPAGATION_NESTED = 6;

}
