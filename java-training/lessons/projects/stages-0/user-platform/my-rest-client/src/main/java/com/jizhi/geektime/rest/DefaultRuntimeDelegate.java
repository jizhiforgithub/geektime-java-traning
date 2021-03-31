package com.jizhi.geektime.rest;

import com.jizhi.geektime.rest.client.DefaultVariantListBuilder;
import com.jizhi.geektime.rest.core.DefaultResponseBuilder;
import com.jizhi.geektime.rest.core.DefaultUriBuilder;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * 2021/3/29
 * jizhi7
 **/
public class DefaultRuntimeDelegate extends RuntimeDelegate {
    @Override
    public UriBuilder createUriBuilder() {
        return new DefaultUriBuilder();
    }

    @Override
    public Response.ResponseBuilder createResponseBuilder() {
        return new DefaultResponseBuilder();
    }

    @Override
    public Variant.VariantListBuilder createVariantListBuilder() {
        return new DefaultVariantListBuilder();
    }

    @Override
    public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        return null;
    }

    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Link.Builder createLinkBuilder() {
        return null;
    }
}
