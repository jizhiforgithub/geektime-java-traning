package com.jizhi.geektime.projects.user.orm.jpa;

import com.jizhi.geektime.context.ClassicComponentContext;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 委派实现（静态 AOP 实现）
 * 保持容器内单例
 * 目前实现 DelegatingEntityManager : EntityManager = 1:1
 * DelegatingEntityManager : EntityManager = 1:N
 **/
//public class DelegatingEntityManager implements EntityManager {
public class DelegatingEntityManager {

    /**
     * 持久化
     */
    private String persistenceUnitName;

    /**
     * 配置文件位置
     */
    private String propertiesLocation;

    /**
     * 实体管理器工厂
     */
    //private EntityManagerFactory entityManagerFactory;
    @PostConstruct
    public void init() {

    }

    /**
     * 如果存在多态的情况，尽可能保持方法是 protected
     * 每个线程获取的 EntityManager 实例，原型实例
     * @return
     */


    /**
     * 加载配置，解析，从容器中查找
     *
     * @param propertiesLocation
     * @return
     */
    private Map loadProperties(String propertiesLocation) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL propertiesFileURL = classLoader.getResource(propertiesLocation);
        Properties properties = new Properties();
        try {
            properties.load(propertiesFileURL.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 增加 JNDI 引用处理
        ClassicComponentContext componentContext = ClassicComponentContext.getInstance();

        /**
         * 如果配置文件里面的的val带有@，说明这个具体值要依赖注入
         */
        for (String propertyName : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(propertyName);
            if (propertyValue.startsWith("@")) {
                String componentName = propertyValue.substring(1);
                Object component = componentContext.getComponent(componentName);
                properties.put(propertyName, component);
            }
        }

        return properties;
    }

    /**
     * Setter 方法会被 Tomcat JNDI 实现调用
     *
     * @param persistenceUnitName
     */
    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public void setPropertiesLocation(String propertiesLocation) {
        this.propertiesLocation = propertiesLocation;
    }


}
