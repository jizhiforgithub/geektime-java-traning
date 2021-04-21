package com.jizhi.geektime.microprofile.rest;

import com.jizhi.geektime.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

/**
 * 请求模板
 * @author jizhi7
 * @since 1.0
 **/
public class RequestTemplate {

    /**
     * 请求方法，值是 {@link javax.ws.rs.HttpMethod} 对应的值
     */
    private String method;

    /**
     * uri模板，值是从 {@link Path} 注解获取的值
     */
    private String uriTemplate;

    /**
     * 请求模板解析支持的注解类型
     * @see PathParam
     * @see QueryParam
     * @see MatrixParam
     * @see FormParam
     * @see CookieParam
     * @see HeaderParam
     */
    public static Set<Class<? extends Annotation>> SUPPORTED_PARAM_ANNOTATION_TYPES =
            unmodifiableSet(new LinkedHashSet<>(asList(
                    PathParam.class,
                    QueryParam.class,
                    MatrixParam.class,
                    FormParam.class,
                    CookieParam.class,
                    HeaderParam.class
            )));

    /**
     * 注解和注解元信息的映射关系
     *  注解有
     * @see PathParam
     * @see javax.ws.rs.QueryParam
     * @see javax.ws.rs.MatrixParam
     * @see javax.ws.rs.FormParam
     * @see javax.ws.rs.CookieParam
     * @see javax.ws.rs.HeaderParam
     */
    private Map<Class<? extends Annotation>, List<AnnotatedParamMetadata>> annotatedParamMetadataMap = new HashMap<>();

    /**
     * 接受的 mediaType 的值，{@link Consumes} 注解的值
     */
    private Set<String> consumes = new LinkedHashSet<>();

    /**
     * 生产的数据的 mediaType 的值，{@link Produces} 注解的值
     */
    private Set<String> produces = new LinkedHashSet<>();


    public RequestTemplate method(String method) {
        this.method = method;
        return this;
    }

    public RequestTemplate urlTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    public RequestTemplate annotatedParamMetadata(List<AnnotatedParamMetadata> annotatedParamMetadata) {
        annotatedParamMetadata.forEach(this::annotatedParamMetadata);
        return this;
    }

    public RequestTemplate annotatedParamMetadata(AnnotatedParamMetadata... annotatedParamMetadata) {
        Arrays.stream(annotatedParamMetadata).forEach(this::annotatedParamMetadata);
        return this;
    }

    public RequestTemplate annotatedParamMetadata(AnnotatedParamMetadata annotatedParamMetadata) {
        Class<? extends Annotation> annotationType = annotatedParamMetadata.getAnnotationType();
        List<AnnotatedParamMetadata> metadataList = annotatedParamMetadataMap.computeIfAbsent(annotationType, type -> new LinkedList<>());
        metadataList.add(annotatedParamMetadata);
        return this;
    }

    public RequestTemplate consumes(String... consumes) {
        this.consumes.addAll(asList(consumes));
        return this;
    }

    public RequestTemplate produces(String... produces) {
        this.produces.addAll(asList(produces));
        return this;
    }

    public List<AnnotatedParamMetadata> getAnnotatedParamMetadata(Class<? extends Annotation> annotationType) {
        return annotatedParamMetadataMap.getOrDefault(annotationType, emptyList());
    }

    public String getMethod() {
        return method;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public Set<String> getConsumes() {
        return unmodifiableSet(consumes);
    }

    public Set<String> getProduces() {
        return unmodifiableSet(produces);
    }
}
