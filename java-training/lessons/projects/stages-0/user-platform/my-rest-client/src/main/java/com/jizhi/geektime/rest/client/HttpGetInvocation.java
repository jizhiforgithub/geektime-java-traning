package com.jizhi.geektime.rest.client;

import com.jizhi.geektime.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Get 请求调用
 *
 * @author jizhi7
 * @since 1.0
 **/
public class HttpGetInvocation implements Invocation {

    private final URI uri;

    private final URL url;

    private final MultivaluedMap<String, Object> headers;

    public HttpGetInvocation(URI uri, MultivaluedMap<String, Object> headers) {
        this.uri = uri;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.headers = headers;
    }

    @Override
    public Invocation property(String name, Object value) {
        return this;
    }

    /**
     * 调用接口，使用 HttpUrlConnection 实现
     *
     * @return
     */
    @Override
    public Response invoke() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.GET);
            setRequestHeaders(connection);
            int responseCode = connection.getResponseCode();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(responseCode);
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置请求头
     *
     * @param connection
     */
    private void setRequestHeaders(HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> header : headers.entrySet()) {
            String headerName = header.getKey();
            for (Object headerValue : header.getValue()) {
                connection.setRequestProperty(headerName, headerValue.toString());
            }
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
