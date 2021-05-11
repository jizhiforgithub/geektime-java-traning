package com.jizhi.geektime.microprofile.rest;

import com.jizhi.geektime.microprofile.rest.annotation.AnnotatedParamMetadata;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.reflect.MethodUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Produces;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

import static com.jizhi.geektime.microprofile.rest.RequestTemplate.SUPPORTED_PARAM_ANNOTATION_TYPES;
import static com.jizhi.geektime.rest.util.PathUtils.resolvePath;

/**
 * 反射请求的模板解析器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ReflectiveRequestTemplateResolver implements RequestTemplateResolver {

    /**
     * 根据请求的接口类，请求方法。
     * 解析出该方法上的java.ws.rs的 *Param 注解，
     * 生成RequestTemplate
     *
     * @param resourceClass
     * @param resourceMethod
     * @return
     */
    @Override
    public RequestTemplate resolve(Class<?> resourceClass, Method resourceMethod) {

        // 获取方法上的 @HttpMethod 注解标注的 Http 方法
        String method = resolveHttpMethod(resourceMethod);
        if (method == null) {
            return null;
        }

        // 解析 接口类上的 @Path 注解，和方法上的 @Path 注解。获得请求请求url
        String uriTemplate = resolvePath(resourceClass, resourceMethod);
        // 解析 接口类或方法上的 @Consumer 注解，获取接受的MediaType
        String[] consumes = resolveConsumes(resourceClass, resourceMethod);
        // 解析 接口类或方法上的 @Produces 注解，获取响应的MediaType
        String[] produces = resolveProduces(resourceClass, resourceMethod);

        // 解析方法入参的 *Param 注解元数据
        List<AnnotatedParamMetadata> metadataList = resolveAnnotatedParamMetadata(resourceMethod);

        // 请求模板
        RequestTemplate requestTemplate = new RequestTemplate();
        // 设置解析内容
        requestTemplate.method(method)
                .urlTemplate(uriTemplate)
                .annotatedParamMetadata(metadataList)
                .consumes(consumes)
                .produces(produces);

        return requestTemplate;
    }

    /**
     * 解析方法的入参的注解信息
     *
     * @param resourceMethod
     * @return
     */
    private List<AnnotatedParamMetadata> resolveAnnotatedParamMetadata(Method resourceMethod) {
        List<AnnotatedParamMetadata> metadataList = new LinkedList<>();

        // 方法上的入参
        Parameter[] parameters = resourceMethod.getParameters();
        // 遍历每一个入参，解析入参的 *Param 注解
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            Annotation paramAnnotation = null;
            // 入参的 *Param 注解
            for (Class<? extends Annotation> annotationType : SUPPORTED_PARAM_ANNOTATION_TYPES) {
                paramAnnotation = parameter.getAnnotation(annotationType);
                // 有方法入参上有该注解
                if (paramAnnotation != null) {
                    break;
                }
            }

            // 获取注解元数据
            if (paramAnnotation != null) {
                AnnotatedParamMetadata metadata = new AnnotatedParamMetadata();
                Class<? extends Annotation> annotationType = paramAnnotation.annotationType();
                // 获取注解的value值
                String paramName = resolveParamName(paramAnnotation);
                // 获取方法入参的 @DefaultValue 注解的值
                String defaultValue = resolveDefaultValue(parameter);

                // 设置注解元数据
                metadata.setAnnotationType(annotationType);
                metadata.setParamName(paramName);
                metadata.setDefaultValue(defaultValue);
                metadata.setParameterIndex(index);
                metadataList.add(metadata);
            }
        }
        return metadataList;
    }

    /**
     * 反射获取 *Param 注解的 value 值
     *
     * @param paramAnnotation
     * @return
     */
    private String resolveParamName(Annotation paramAnnotation) {
        Class<? extends Annotation> annotationType = paramAnnotation.annotationType();
        String paramName = null;
        try {
            paramName = (String) MethodUtils.invokeMethod(paramAnnotation, "value", ArrayUtils.EMPTY_OBJECT_ARRAY);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return paramName;
    }

    /**
     * 获取参数上的 {@link DefaultValue} 的值
     *
     * @param parameter
     * @return
     */
    private String resolveDefaultValue(Parameter parameter) {
        DefaultValue defaultValueAnnotation = parameter.getAnnotation(DefaultValue.class);
        return defaultValueAnnotation == null ? null : defaultValueAnnotation.value();
    }

    /**
     * 解析方法上的 {@link HttpMethod} 注解，获取Http的调用方法
     *
     * @param resourceMethod
     * @return
     */
    private String resolveHttpMethod(Method resourceMethod) {
        String httpMethod = null;
        for (Annotation annotation : resourceMethod.getAnnotations()) {
            HttpMethod httpMethodMetaAnnotation = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethodMetaAnnotation != null) {
                httpMethod = httpMethodMetaAnnotation.value();
            }
        }
        return httpMethod;
    }

    /**
     * 解析 {@link Consumes} 注解，获取对应的 MediaType
     *
     * @param resourceClass
     * @param resourceMethod
     * @return
     */
    private String[] resolveConsumes(Class<?> resourceClass, Method resourceMethod) {
        // 获取接口类上的 MediaType
        Consumes consumes = getConsumes(resourceClass);
        if (consumes == null) {
            // 为空，再获取方法上的 MediaType
            consumes = getConsumes(resourceMethod);
        }
        // 转为非空的字符数组
        return getNullSafeStringArray(consumes == null ? null : consumes.value());
    }

    /**
     * 解析 {@link Produces} 注解，获取对应的 MediaType
     *
     * @param resourceClass
     * @param resourceMethod
     * @return
     */
    private String[] resolveProduces(Class<?> resourceClass, Method resourceMethod) {
        Produces produces = getProduces(resourceClass);
        if (produces == null) {
            produces = getProduces(resourceMethod);
        }
        return getNullSafeStringArray(produces == null ? null : produces.value());
    }

    /**
     * 获取 {@link Consumes} 注解，可能注解在类上，也可能在方法上
     *
     * @param annotatedElement
     * @return
     */
    private Consumes getConsumes(AnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotation(Consumes.class);
    }

    /**
     * 获取 {@link Produces} 注解，可能注解在类上，也可能在方法上
     *
     * @param annotatedElement
     * @return
     */
    private Produces getProduces(AnnotatedElement annotatedElement) {
        return annotatedElement.getAnnotation(Produces.class);
    }

    /**
     * 获取非空的字符数组
     *
     * @param values
     * @return
     */
    private String[] getNullSafeStringArray(String[] values) {
        return values == null ? ArrayUtils.EMPTY_STRING_ARRAY : values;
    }
}
