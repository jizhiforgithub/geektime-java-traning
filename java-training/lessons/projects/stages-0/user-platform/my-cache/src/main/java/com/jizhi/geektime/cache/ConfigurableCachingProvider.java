package com.jizhi.geektime.cache;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * 可配置化的缓存提供者，入口是Caching#getProvider
 *
 * @author jizhi7
 * @see Caching#getCachingProvider()
 * @since 1.0
 **/
public class ConfigurableCachingProvider implements CachingProvider {

    /**
     * CacheManager的注册容器
     */
    private ConcurrentMap<String, CacheManager> cacheManagersRepository = new ConcurrentHashMap<>();

    /**
     * 默认的缓存实现URL
     */
    public static final URI DEFAULT_URI = URI.create("in-memory://localhost/");

    /**
     * CacheManager实现类的配置项的前缀
     */
    public static final String CACHE_MANAGER_MAPPINGS_PROPERTY_PREFIX = "javax.cache.CacheManager.mappings.";

    /**
     * 缓存的配置
     */
    private Properties defaultProperties;

    /**
     * 默认的缓存配置文件路径
     */
    public static final String DEFAULT_PROPERTIES_RESOURCE_NAME = "META-INF/default-caching-provider.properties";

    /**
     * 配置文件的默认编码
     */
    public static final String DEFAULT_ENCODING = System.getProperty("file.encoding", "UTF-8");

    /**
     * 获取类加载器，和SPI有关
     *
     * @return
     */
    @Override
    public ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = Caching.getDefaultClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        return classLoader;
    }

    /**
     * 获取默认的缓存URL
     *
     * @return
     */
    @Override
    public URI getDefaultURI() {
        return DEFAULT_URI;
    }

    @Override
    public Properties getDefaultProperties() {
        if (defaultProperties == null) {
            defaultProperties = loadDefaultProperties();
        }
        return defaultProperties;
    }

    /**
     * 加载默认配置文件
     *
     * @return
     */
    private Properties loadDefaultProperties() {
        ClassLoader classLoader = getDefaultClassLoader();
        Properties properties = new Properties();
        try {
            Enumeration<URL> resource = classLoader.getResources(DEFAULT_PROPERTIES_RESOURCE_NAME);
            while (resource.hasMoreElements()) {
                URL url = resource.nextElement();

                try (InputStream inputStream = url.openStream();
                     Reader reader = new InputStreamReader(inputStream, DEFAULT_ENCODING);) {
                    properties.load(reader);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    /**
     * 获取缓存管理器
     *
     * @param uri         缓存uri
     * @param classLoader 类加载器
     * @param properties  配置项
     * @return
     */
    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        URI actualURI = getOrDefault(uri, this::getDefaultURI);
        ClassLoader actualClassLoader = getOrDefault(classLoader, this::getDefaultClassLoader);
        Properties actualProperties = getOrDefault(properties, this::getDefaultProperties);

        // 根据uri，classLoader， properties生成一个key，标识该缓存管理器
        String key = generateCacheManagerKey(actualURI, actualClassLoader, actualProperties);

        return cacheManagersRepository.computeIfAbsent(key, k -> newCacheManager(actualURI, actualClassLoader, actualProperties));
    }

    /**
     * 生成
     * @param uri
     * @param classLoader
     * @param properties
     * @return
     */
    private String generateCacheManagerKey(URI uri, ClassLoader classLoader, Properties properties) {
        StringBuilder keyBuilder = new StringBuilder(uri.toASCIIString())
                .append("-").append(classLoader)
                .append("-").append(properties);
        return keyBuilder.toString();
    }

    /**
     * 获取值，如果为空就拿默认值
     *
     * @param value
     * @param defaultValue
     * @param <T>
     * @return
     */
    private <T> T getOrDefault(T value, Supplier<T> defaultValue) {
        return value == null ? defaultValue.get() : value;
    }

    /**
     * 反射实例化一个CacheManager
     * @param uri
     * @param classLoader
     * @param properties
     * @return
     */
    private CacheManager newCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        CacheManager cacheManager = null;
        try{
            Class<? extends AbstractCacheManager> cacheManagerClass = getCacheMangerClass(uri, classLoader, properties);
            Class[] parameterTypes = new Class[] {CachingProvider.class, URI.class, ClassLoader.class, Properties.class};
            Constructor<? extends AbstractCacheManager> constructor = cacheManagerClass.getConstructor(parameterTypes);
            cacheManager = constructor.newInstance(this, uri, classLoader, properties);
        }  catch (Throwable e) {
            throw new CacheException(e);
        }
        return cacheManager;
    }

    /**
     * 获取到CacheManager的class
     * @param uri
     * @param classLoader
     * @param properties
     * @return
     */
    private Class<? extends AbstractCacheManager> getCacheMangerClass(URI uri, ClassLoader classLoader, Properties properties) throws ClassNotFoundException {
        String className = getCacheManagerClassName(uri, properties);
        Class<? extends AbstractCacheManager> cacheManagerImplClass = null;
        Class<?> cacheManagerClass = classLoader.loadClass(className);
        // 必须得要是AbstractCacheManager的子类
        if (!AbstractCacheManager.class.isAssignableFrom(cacheManagerClass)) {
            throw new ClassCastException(format("The implementation class of %s must extend %s",
                    CacheManager.class.getName(), AbstractCacheManager.class.getName()));
        }
        cacheManagerImplClass = (Class<? extends AbstractCacheManager>) cacheManagerClass;
        return cacheManagerImplClass;
    }

    /**
     * 获取CacheManager的实现类的全路径类名称
     * @param uri
     * @param properties
     * @return
     */
    private String getCacheManagerClassName(URI uri, Properties properties) {
        // 在配置项中获得CacheManager的实现类，配置项的key是：
        String propertyName = getCacheManagerClassNamePropertyName(uri);
        String className = properties.getProperty(propertyName);
        if (className == null) {
            throw new IllegalStateException(format("The implementation class name of %s that is the value of property '%s' " +
                    "must be configured in the Properties[%s]", CacheManager.class.getName(), propertyName, properties));
        }
        return className;
    }

    /**
     * 获取CacheManager
     * @param uri
     * @return
     */
    private String getCacheManagerClassNamePropertyName(URI uri) {
        String scheme = uri.getScheme();
        return CACHE_MANAGER_MAPPINGS_PROPERTY_PREFIX + scheme;
    }


    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return getCacheManager(uri, classLoader, getDefaultProperties());
    }

    @Override
    public CacheManager getCacheManager() {
        return getCacheManager(getDefaultURI(), getDefaultClassLoader(), getDefaultProperties());
    }

    /**
     * 资源的关闭，关闭缓存管理器
     *
     * @param uri
     * @param classLoader
     */
    @Override
    public void close(URI uri, ClassLoader classLoader) {
        for (CacheManager cacheManager : cacheManagersRepository.values()) {
            if (Objects.equals(cacheManager.getURI(), uri)
                    && Objects.equals(cacheManager.getClassLoader(), classLoader)) {
                cacheManager.close();
            }
        }
    }

    @Override
    public void close() {
        this.close(getDefaultURI(), getDefaultClassLoader());
    }

    @Override
    public void close(ClassLoader classLoader) {
        this.close(getDefaultURI(), classLoader);
    }


    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
        return false;
    }
}
