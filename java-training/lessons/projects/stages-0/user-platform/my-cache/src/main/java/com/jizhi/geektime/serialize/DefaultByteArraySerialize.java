package com.jizhi.geektime.serialize;

import java.io.*;

/**
 * 基于字节数组的序列化和反序列化
 *
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultByteArraySerialize implements DataSerialize {

    @Override
    public Object serialize(Object obj, Class resultType) {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(obj);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Override
    public Object deserialize(Object value, Class clazz) {
        Object obj = null;
        byte[] bytes = (byte[]) value;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);) {
            obj = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public boolean supportResultType(Class clazz) {
        return byte[].class.equals(clazz);
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
