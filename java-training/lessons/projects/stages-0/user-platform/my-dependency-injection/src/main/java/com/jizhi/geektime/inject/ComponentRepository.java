package com.jizhi.geektime.inject;

import java.util.Set;

/**
 * 组件仓库
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface ComponentRepository {

    /**
     * 通过名称查找组件对象
     *
     * @param name 组件名称
     * @param <C>  组件对象类型
     * @return 如果找不到返回, <code>null</code>
     */
    <C> C getComponent(String name);

    /**
     * 获取所有的组件名称
     *
     * @return
     */
    Set<String> getComponentNames();

}
