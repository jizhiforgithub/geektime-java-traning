package com.jizhi.geektime.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jizhi.geektime.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 2021/3/30
 * jizhi7
 **/
public class HttpPostInvocation implements Invocation {

    private final URI uri;

    private final URL url;

    private final MultivaluedMap<String, Object> headers;

    private final Entity<?> entity;

    public HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<?> entity) {
        this.uri = uri;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        this.entity = entity;
        if (headers == null) {
            this.headers = new MultivaluedHashMap<String, Object>();
        } else {
            this.headers = headers;
        }
        if (getMediaTypeString(entity) != null) {
            this.headers.add("content-type", getMediaTypeString(entity));
        }

    }

    private String getMediaTypeString(Entity<?> entity) {
        if (entity.getMediaType() == null) {
            return null;
        }
        if (entity.getMediaType().getSubtype() == null) {
            return entity.getMediaType().getType();
        }
        return entity.getMediaType().getType() + "/" + entity.getMediaType().getSubtype();
    }


    @Override
    public Invocation property(String name, Object value) {
        return this;
    }

    @Override
    public Response invoke() {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    for (Object val : entry.getValue()) {
                        connection.setRequestProperty(key, val.toString());
                    }
                }
            }
            connection.setDoInput(true);
            if (entity != null) {
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                byte[] bytes = null;
                String encoding = (entity.getEncoding() == null || "".equals(entity.getEncoding())) ? "utf-8" : entity.getEncoding();
                switch (getMediaTypeString(entity)) {
                    case MediaType.APPLICATION_JSON:
                        ObjectMapper mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(entity.getEntity());
                        bytes = str.getBytes(encoding);
                        break;
                }
                outputStream.write(bytes);
                outputStream.flush();
            }
            int responseCode = connection.getResponseCode();
            Response build = RuntimeDelegate.getInstance().createResponseBuilder().build();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(responseCode);
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T invoke(Class<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public <T> T invoke(GenericType<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public Future<Response> submit() {
        return null;
    }

    @Override
    public <T> Future<T> submit(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(GenericType<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(InvocationCallback<T> callback) {
        return null;
    }
}
