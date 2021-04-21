package com.jizhi.geektime.microprofile.rest;

import com.jizhi.geektime.microprofile.rest.reflect.RestClientInterfaceInvocationHandler;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.RestClientDefinitionException;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Configuration;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * Rest调用端构建器
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultRestClientBuilder implements RestClientBuilder {

    /**
     * 类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 请求的参数模板解析器
     */
    private final RequestTemplateResolver requestTemplateResolver;

    /**
     * 基本url
     */
    private URL baseUrl;

    /**
     * 连接超时
     */
    private long connectTimeoutInMillis;

    /**
     * 读取超时
     */
    private long readTimeoutInMillis;

    /**
     * 执行器，请求的执行
     */
    private ExecutorService executor;

    /**
     * HTTPS的
     */
    private SSLContext sslContext;

    private KeyStore trustStore;

    private KeyStore keyStore;

    private String keystorePassword;

    private HostnameVerifier hostnameVerifier;

    private String proxyHost;

    private int proxyPort;

    private QueryParamStyle queryParamStyle;

    /**
     * 获取默认的rest客户端构造器
     */
    public DefaultRestClientBuilder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public DefaultRestClientBuilder(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.requestTemplateResolver = new ReflectiveRequestTemplateResolver();
    }


    @Override
    public RestClientBuilder baseUrl(URL url) {
        this.baseUrl = url;
        return this;
    }

    @Override
    public RestClientBuilder connectTimeout(long timeout, TimeUnit timeUnit) {
        this.connectTimeoutInMillis = timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public RestClientBuilder readTimeout(long timeout, TimeUnit timeUnit) {
        this.readTimeoutInMillis = timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public RestClientBuilder executorService(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public RestClientBuilder sslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    @Override
    public RestClientBuilder trustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
        return this;
    }

    @Override
    public RestClientBuilder keyStore(KeyStore keyStore, String keystorePassword) {
        this.keyStore = keyStore;
        this.keystorePassword = keystorePassword;
        return this;
    }

    @Override
    public RestClientBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    @Override
    public RestClientBuilder followRedirects(boolean follow) {
        throw new UnsupportedOperationException("To Support in the future.");
    }

    @Override
    public RestClientBuilder proxyAddress(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        return this;
    }

    @Override
    public RestClientBuilder queryParamStyle(QueryParamStyle style) {
        this.queryParamStyle = style;
        return this;
    }

    /**
     * 构建rest客户端，解析接口注解，创建代理实例
     * @param clazz 请求的接口类，
     * @param <T>
     * @return
     * @throws IllegalStateException
     * @throws RestClientDefinitionException
     */
    @Override
    public <T> T build(Class<T> clazz) throws IllegalStateException, RestClientDefinitionException {
        // 必须是接口
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("The 'clazz' argument must be a Java interface.");
        }

        // 解析接口类，获取该类对应的注解参数
        Map<Method, RequestTemplate> requestTemplates = resolveRequestTemplates(clazz);

        // 创建代理实例，
        return (T) newProxyInstance(classLoader, new Class[]{clazz},
                new RestClientInterfaceInvocationHandler(getConfiguration(), requestTemplates));
    }

    /**
     * 解析接口上的 java.ws.rs 的*Param 注解
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> Map<Method,RequestTemplate> resolveRequestTemplates(Class<T> clazz) {
        Map<Method, RequestTemplate> requestTemplates = new LinkedHashMap<>();
        // public 的方法
        for (Method method : clazz.getMethods()) {
            // 解析方法
            RequestTemplate requestTemplate = requestTemplateResolver.resolve(clazz, method);
            if(requestTemplate != null) {
                requestTemplate.urlTemplate(baseUrl.toString() + requestTemplate.getUriTemplate());
                requestTemplates.put(method, requestTemplate);
            }
        }
        return requestTemplates;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public RestClientBuilder property(String name, Object value) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass, int priority) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
        return null;
    }

    @Override
    public RestClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component, int priority) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component, Class<?>... contracts) {
        return null;
    }

    @Override
    public RestClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
        return null;
    }
}
