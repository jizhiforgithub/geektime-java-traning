package com.jizhi.geektime.rest.client;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

/**
 * 不可变的web对象，就是一个请求
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ImmutableWebTarget implements WebTarget {

    private final UriBuilder uriBuilder;

    public ImmutableWebTarget(UriBuilder uriBuilder) {
        // 不可变的，所以要克隆
        this.uriBuilder = uriBuilder.clone();
    }

    @Override
    public URI getUri() {
        return uriBuilder.build();
    }

    @Override
    public UriBuilder getUriBuilder() {
        return uriBuilder;
    }

    /**
     * 路径
     *
     * @param path
     * @return
     */
    @Override
    public WebTarget path(String path) {
        // 新创建一个 WebTarget
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.path(path);
        return target;
    }

    protected ImmutableWebTarget newWebTarget() {
        return new ImmutableWebTarget(this.uriBuilder);
    }


    @Override
    public WebTarget resolveTemplate(String name, Object value) {
        return resolveTemplate(name, value, false);
    }

    @Override
    public WebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.resolveTemplate(name, value, encodeSlashInPath);
        return target;
    }

    @Override
    public WebTarget resolveTemplateFromEncoded(String name, Object value) {
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.resolveTemplateFromEncoded(name, value);
        return target;
    }

    @Override
    public WebTarget resolveTemplates(Map<String, Object> templateValues) {
        return resolveTemplates(templateValues, false);
    }

    @Override
    public WebTarget resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) {
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.resolveTemplates(templateValues, encodeSlashInPath);
        return target;
    }

    @Override
    public WebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.resolveTemplatesFromEncoded(templateValues);
        return target;
    }

    @Override
    public WebTarget matrixParam(String name, Object... values) {
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.matrixParam(name, values);
        return target;
    }

    @Override
    public WebTarget queryParam(String name, Object... values) {
        ImmutableWebTarget target = newWebTarget();
        target.uriBuilder.queryParam(name, values);
        return target;
    }

    @Override
    public Invocation.Builder request() {
        return new DefaultInvocationBuilder(uriBuilder);
    }

    @Override
    public Invocation.Builder request(String... acceptedResponseTypes) {
        return request().accept(acceptedResponseTypes);
    }

    @Override
    public Invocation.Builder request(MediaType... acceptedResponseTypes) {
        return request().accept(acceptedResponseTypes);
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public WebTarget property(String name, Object value) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass, int priority) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass, Class<?>... contracts) {
        return null;
    }

    @Override
    public WebTarget register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        return null;
    }

    @Override
    public WebTarget register(Object component) {
        return null;
    }

    @Override
    public WebTarget register(Object component, int priority) {
        return null;
    }

    @Override
    public WebTarget register(Object component, Class<?>... contracts) {
        return null;
    }

    @Override
    public WebTarget register(Object component, Map<Class<?>, Integer> contracts) {
        return null;
    }
}
