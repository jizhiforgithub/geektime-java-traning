package com.jizhi.geektime.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * 基于jackson实现的的序列化和反序列化
 *
 * @author jizhi7
 * @since 1.0
 **/
public class JackSonSerialize implements DataSerialize {

    @Override
    public Object serialize(Object obj, Class resultType) {
        String str = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            str = objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }

    @Override
    public Object deserialize(Object value, Class clazz) {
        Object obj = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            obj = objectMapper.readValue(value.toString(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public boolean supportResultType(Class clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public int getPriority() {
        return 4;
    }

}
