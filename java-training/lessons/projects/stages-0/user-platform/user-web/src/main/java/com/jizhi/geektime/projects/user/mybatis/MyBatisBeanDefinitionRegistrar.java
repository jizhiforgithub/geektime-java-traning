package com.jizhi.geektime.projects.user.mybatis;

import com.jizhi.geektime.projects.user.mybatis.annotation.EnableMybatis;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 注册 mybatis 的相关 bean
 * 参考 {@link org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration}
 */
public class MyBatisBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ApplicationContextAware {


    private Environment environment;
    private ApplicationContext applicationContext;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMybatis.class.getName());

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class);

        // 设置 SqlSessionFactoryBean 的相关属性
        builder.addPropertyReference("dataSource", (String) annotationAttributes.get("dataSourceBean"));
        builder.addPropertyValue("vfs", annotationAttributes.get("vfsClass"));
        String[] pluginBeans = (String[]) annotationAttributes.get("pluginBeans");
        Interceptor[] interceptors = new Interceptor[pluginBeans.length];
        for (int i = 0; i < pluginBeans.length; i++) {
            Interceptor bean = applicationContext.getBean(Interceptor.class, pluginBeans[i]);
            interceptors[i] = bean;
        }
        builder.addPropertyValue("plugins", interceptors);
        builder.addPropertyReference("databaseIdProviderBean", (String) annotationAttributes.get("databaseIdProviderBean"));

        String[] typeHandlerBeans = (String[]) annotationAttributes.get("typeHandlerBeans");
        TypeHandler[] typeHandlers = new TypeHandler[typeHandlerBeans.length];
        for (int i = 0; i < typeHandlerBeans.length; i++) {
            typeHandlers[i] = applicationContext.getBean(TypeHandler.class, typeHandlerBeans[i]);
        }
        builder.addPropertyValue("typeHandlers", typeHandlers);

        String[] languageDriverBeans = (String[]) annotationAttributes.get("languageDriverBeans");
        LanguageDriver[] LanguageDriver = new LanguageDriver[languageDriverBeans.length];
        for (int i = 0; i < languageDriverBeans.length; i++) {
            LanguageDriver[i] = applicationContext.getBean(LanguageDriver.class, languageDriverBeans[i]);
        }
        builder.addPropertyValue("scriptingLanguageDrivers", LanguageDriver);
        builder.addPropertyValue("defaultScriptingLanguageDriver", LanguageDriver[0]);

        // String propertiesBean = (String) annotationAttributes.get("propertiesBean");
        ObjectProvider<MybatisProperties> propertyProvider = applicationContext.getBeanProvider(MybatisProperties.class);
        MybatisProperties properties = propertyProvider.getIfAvailable();
        if (properties == null) {
            BeanDefinition beanDefinition = buildMybatisPropertiesBean();
            AnnotationBeanNameGenerator generator = AnnotationBeanNameGenerator.INSTANCE;
            String name = generator.generateBeanName(beanDefinition, registry);
            registry.registerBeanDefinition(name, beanDefinition);
            properties = applicationContext.getBean(MybatisProperties.class, name);
        }

        String[] configurationCustomizerBeans = (String[]) annotationAttributes.get("configurationCustomizerBeans");
        List<ConfigurationCustomizer> customizers = new ArrayList<>();
        for (String configurationCustomizerBean : configurationCustomizerBeans) {
            customizers.add(applicationContext.getBean(ConfigurationCustomizer.class, configurationCustomizerBean));
        }
        applyConfiguration(properties, customizers, builder);


        //
        String beanName = (String) annotationAttributes.get("value");
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

    }

    private BeanDefinition buildMybatisPropertiesBean() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MybatisProperties.class);
        return builder.getBeanDefinition();
    }

    private void applyConfiguration(MybatisProperties properties, List<ConfigurationCustomizer> configurationCustomizers, BeanDefinitionBuilder builder) {
        Configuration configuration = properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(properties.getConfigLocation())) {
            configuration = new Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        // factory.setConfiguration(configuration);
        builder.addPropertyValue("configuration", configuration);
        // factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        builder.addPropertyValue("configurationProperties", properties.getConfigurationProperties());
        // factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        builder.addPropertyValue("typeAliasesPackage", properties.getTypeAliasesPackage());
        if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
            // factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
            builder.addPropertyValue("typeHandlersPackage", properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
            // factory.setMapperLocations(this.properties.resolveMapperLocations());
            builder.addPropertyValue("mapperLocations", properties.resolveMapperLocations());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
