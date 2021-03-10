package com.jizhi.geektime.projects.user.transaction.annotation;

/**
 * 事务隔离级别枚举
 * @author jizhi7
 * @since 1.0
 **/

public final class LocalIsolation {

    /**
     * 默认
     */
    public static final int DEFAULT = -1;


    /**
     * 未提交读
     */
    public static final int READ_UNCOMMITTED = 1;

    /**
     * 提交读
     */
    public static final int READ_COMMITTED = 2;

    /**
     * 可重复读
     */
    public static final int REPEATABLE_READ = 4;

    /**
     * 序列化
     */
    public static final int SERIALIZABLE = 8;



}
