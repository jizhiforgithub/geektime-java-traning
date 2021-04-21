package com.jizhi.geektime.microprofile.rest;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderResolver;

/**
 * 默认客户端解析器
 * jizhi7
 **/
public class DefaultRestClientBuilderResolver extends RestClientBuilderResolver {

    @Override
    public RestClientBuilder newBuilder() {
        return new DefaultRestClientBuilder(getClass().getClassLoader());
    }

}
