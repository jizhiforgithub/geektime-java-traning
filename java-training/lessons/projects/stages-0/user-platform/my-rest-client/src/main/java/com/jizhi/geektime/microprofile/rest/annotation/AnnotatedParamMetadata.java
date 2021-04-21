package com.jizhi.geektime.microprofile.rest.annotation;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;

/**
 * javax.ws.rs的Param相关的注解的注解参数元数据
 * @see javax.ws.rs.PathParam
 * @see javax.ws.rs.QueryParam
 * @see javax.ws.rs.MatrixParam
 * @see javax.ws.rs.FormParam
 * @see javax.ws.rs.CookieParam
 * @see javax.ws.rs.HeaderParam
 * @see javax.ws.rs.DefaultValue
 * @author jizhi7
 * @since 1.0
 **/
public class AnnotatedParamMetadata {

    /**
     * 注解类型
     */
    private Class<? extends Annotation> annotationType;

    /**
     * 参数值，具体注解参数对应的值。如 {@link Path#value()} 的值
     */
    private String paramName;

    /**
     * 注解默认值，值来至 {@link javax.ws.rs.DefaultValue} ，有可能为<code>null</code>
     */
    private String defaultValue;

    /**
     * 在方法入参中的位置
     */
    private int parameterIndex;

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public void setParameterIndex(int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }
}
