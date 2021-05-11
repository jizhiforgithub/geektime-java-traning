package com.jizhi.geektime.rest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.*;

import static com.jizhi.geektime.rest.util.URLUtils.DEFAULT_ENCODING;

/**
 * 2021/3/28
 * jizhi7
 **/
public class DefaultResponse extends Response {

    private int status;

    private Object entity;

    private Annotation[] annotations;

    private Set<String> allowedMethods;

    private CacheControl cacheControl;

    private String encoding = DEFAULT_ENCODING;

    private MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    private Locale locale;

    private MediaType mediaType;

    private List<Variant> variants = new LinkedList<>();

    private URI contentLocation;

    private List<NewCookie> newCookies = new LinkedList<>();

    private Map<String, NewCookie> cookies = new HashMap<>();

    private Date date;

    private Date expires;

    private Date lastModified;

    private URI location;

    private EntityTag entityTag;

    private Set<Link> links = new LinkedHashSet<>();

    private HttpURLConnection connection;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public StatusType getStatusInfo() {
        return null;
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    /**
     * 将连接中的流数据映射成为一个实体对象
     *
     * @return
     */
    @Override
    public <T> T readEntity(Class<T> entityType) {
        T entity = null;
        try {
            InputStream inputStream = connection.getInputStream();
            if (String.class.equals(entityType)) {
                Object value = IOUtils.toString(inputStream, encoding);
                entity = (T) value;
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                entity = objectMapper.readValue(new InputStreamReader(inputStream, encoding), entityType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType) {
        return null;
    }

    @Override
    public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        return null;
    }

    @Override
    public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        return null;
    }

    @Override
    public boolean hasEntity() {
        return false;
    }

    @Override
    public boolean bufferEntity() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public Locale getLanguage() {
        return locale;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    @Override
    public Map<String, NewCookie> getCookies() {
        return cookies;
    }

    @Override
    public EntityTag getEntityTag() {
        return entityTag;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public URI getLocation() {
        return location;
    }

    @Override
    public Set<Link> getLinks() {
        return links;
    }

    @Override
    public boolean hasLink(String relation) {
        return false;
    }

    @Override
    public Link getLink(String relation) {
        return null;
    }

    @Override
    public Link.Builder getLinkBuilder(String relation) {
        return null;
    }

    @Override
    public MultivaluedMap<String, Object> getMetadata() {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getStringHeaders() {
        return null;
    }

    @Override
    public String getHeaderString(String name) {
        return null;
    }
}
