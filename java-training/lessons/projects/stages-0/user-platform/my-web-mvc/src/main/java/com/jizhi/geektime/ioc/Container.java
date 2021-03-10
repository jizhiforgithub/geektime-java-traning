package com.jizhi.geektime.ioc;

/**
 * IoC容器接口
 * @author jizhi7
 * @since 1.0
 **/
public interface Container {

    /**
     * 根据bean名称，获取IoC容器中的bean
     * @param name bean名称
     * @return bean实例
     */
    Object getObject(String name);

    /**
     * 获取父容器
     * @return 父容器，没有返回<code>null</code>
     */
    Container getParentContainer();

    /**
     * 设置父容器
     * @param container 该容器的父容器
     */
    void setParentContainer(Container container);

}
