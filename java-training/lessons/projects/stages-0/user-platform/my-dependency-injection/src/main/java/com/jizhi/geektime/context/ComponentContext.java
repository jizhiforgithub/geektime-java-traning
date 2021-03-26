package com.jizhi.geektime.context;

import java.util.List;

/**
 * 组件在上下文
 * @author jizhi7
 * @since 1.0
 **/
public interface ComponentContext {

    /**
     * 组件上下文初始化
     */
    void init();

    /**
     * 组件上下文销毁
     */
    void destroy();

    /**
     * 根据组件名称获取组件对象
     * @param name 组件名称
     * @param <C>
     * @return 返回组件对象，如果没有返回 <code>null</code>
     */
    <C> C getComponent(String name);

    /**
     * 获取所有的组件名称
     * @return 返回组件名称列表，没有返回一个空列表
     */
    List<String> getComponentNames();

}
