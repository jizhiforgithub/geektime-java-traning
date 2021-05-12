package com.jizhi.geektime.projects.user.mybatis.annotation;

import com.jizhi.geektime.projects.user.mybatis.MyBatisBeanDefinitionRegistrar;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 激活 MyBatis
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(MyBatisBeanDefinitionRegistrar.class)
public @interface EnableMybatis {

    /**
     * @return {@link org.mybatis.spring.SqlSessionFactoryBean} 的 bean 名称
     */
    String value() default "sqlSessionFactoryBean";

    /**
     * @return 数据源 bean 名称
     */
    String dataSourceBean();

    /**
     * @return VFS 类
     */
    Class<?> vfsClass() default SpringBootVFS.class;


    /**
     * @return 插件
     */
    String[] pluginBeans() default {};

    /**
     * @return
     */
    String[] typeHandlerBeans() default {};

    /**
     * @return 属性配置的 bean 名称
     */
    //String propertiesBean() default "";

    /**
     * @return 自定义的配置 bean 名称
     */
    String[] configurationCustomizerBeans() default {};

}
