package com.jizhi.geektime.microprofile.rest.reflect;

import com.jizhi.geektime.microprofile.rest.RequestTemplate;
import com.jizhi.geektime.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

/**
 * rest接口请求代理实现类
 * @author jizhi7
 * @since 1.0
 **/
public class RestClientInterfaceInvocationHandler implements InvocationHandler {

    /**
     * rest http调用客户端
     */
    private final Client client;

    /**
     * 接口方法对应的请求模板，就是接口上的注解参数
     */
    private final Map<Method, RequestTemplate> requestTemplates;

    public RestClientInterfaceInvocationHandler(Configuration configuration, Map<Method, RequestTemplate> requestTemplates) {
        this.client = ClientBuilder.newClient(configuration);
        this.requestTemplates = requestTemplates;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // HTTP request Around -> 显示的 Invoke -> Invocation.invoke
        // Timeout Around ->
        // Priority 优先级

        RequestTemplate requestTemplate = requestTemplates.get(method);

        if (requestTemplate == null) {
            throw new NullPointerException();
        }

        // 获取uri路径，带模板的
        String uriTemplate = requestTemplate.getUriTemplate();
        // uri中的模板参数替换
        UriBuilder uriBuilder = UriBuilder.fromUri(uriTemplate);

        // 处理 @PathParam @DefaultValue 注解
        for (AnnotatedParamMetadata metadata : requestTemplate.getAnnotatedParamMetadata(PathParam.class)) {
            String paramName = metadata.getParamName();
            int paramIndex = metadata.getParameterIndex();
            Object paramValue = args[paramIndex];
            if (paramValue == null) {
                // Handle @DefaultValue
                paramValue = metadata.getDefaultValue();
            }
            // 解析uri模板
            uriBuilder.resolveTemplate(paramName, paramValue);
        }

        // 处理 @QueryParam
        for (AnnotatedParamMetadata metadata : requestTemplate.getAnnotatedParamMetadata(QueryParam.class)) {
            String paramName = metadata.getParamName();
            int paramIndex = metadata.getParameterIndex();
            Object paramValue = args[paramIndex];
            uriBuilder.queryParam(paramName, paramValue);
        }

        // 处理 @QueryParam
        for (AnnotatedParamMetadata metadata : requestTemplate.getAnnotatedParamMetadata(MatrixParam.class)) {
            String paramName = metadata.getParamName();
            int paramIndex = metadata.getParameterIndex();
            Object paramValue = args[paramIndex];
            uriBuilder.matrixParam(paramName, paramValue);
        }

        // 接口的方法
        String httpMethod = requestTemplate.getMethod();
        // 生产的mediaType
        String[] acceptedResponseTypes = requestTemplate.getProduces().toArray(new String[0]);
        // 方法返回类型
        Class<?> returnType = method.getReturnType();
        // 构建参数请求实体
        Entity<?> entity = buildEntity(method, args);
        // 获取参数解析后的uri
        String uri = uriBuilder.build().toString();
        // java.ws.rs的client调用
        Invocation invocation = client.target(uri)
                .request(acceptedResponseTypes)
                .build(httpMethod, entity);
        // 发起rest调用
        return invocation.invoke(returnType);
    }

    /**
     * 构建参数请求实体
     * @param method
     * @param args
     * @return
     */
    private Entity<?> buildEntity(Method method, Object[] args) {
        return null;
    }

    private String[] getAcceptedResponseTypes(Method method) {
        return new String[0];
    }

    private String getHttpMethod(Method method) {
        return null;
    }

    private URI buildURI(Method method) {
        return null;
    }

}
