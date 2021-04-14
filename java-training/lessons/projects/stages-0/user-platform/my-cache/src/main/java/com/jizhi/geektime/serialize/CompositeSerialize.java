package com.jizhi.geektime.serialize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

/**
 * 序列化反序列化组合实现
 * @author jizhi7
 * @since 1.0
 **/
public class CompositeSerialize implements DataSerialize {

    private List<DataSerialize> serializes;

    public CompositeSerialize() {
        this.serializes = new ArrayList<>();
        loadSerializes();
        loadDiscoverySerializes();
        serializesSort();
    }

    /**
     * 排序
     */
    private void serializesSort() {
        serializes = serializes.stream()
                .sorted(DataSerialize.PRIORITY_COMPARATOR::compare)
                .collect(toList());
    }

    /**
     * 加载SPI配置的
     */
    private void loadDiscoverySerializes() {
        Iterator<DataSerialize> iterator = ServiceLoader.load(DataSerialize.class).iterator();
        while (iterator.hasNext()) {
            this.serializes.add(iterator.next());
        }
    }

    /**
     * 加载默认实现
     */
    private void loadSerializes() {
        this.serializes.add(new DefaultByteArraySerialize());
    }

    /**
     * 用户添加的
     * @param serialize
     */
    public void addSerializes(DataSerialize serialize) {
        this.serializes.add(serialize);
    }


    @Override
    public <T> T serialize(Object obj, Class resultType) {
        DataSerialize useSerialize = null;
        for (DataSerialize serialize : serializes) {
            if(serialize.supportResultType(resultType)) {
                useSerialize = serialize;
                break;
            }
        }
        return useSerialize.serialize(obj, resultType);
    }

    @Override
    public <T> Object deserialize(T value, Class clazz) {
        DataSerialize useDeserialize = null;
        for (DataSerialize deserialize : serializes) {
            if(deserialize.supportResultType(value.getClass())) {
                useDeserialize = deserialize;
                break;
            }
        }
        return useDeserialize.deserialize(value, clazz);
    }

    @Override
    public boolean supportResultType(Class clazz) {
        for (DataSerialize serialize : serializes) {
            if(serialize.supportResultType(clazz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
