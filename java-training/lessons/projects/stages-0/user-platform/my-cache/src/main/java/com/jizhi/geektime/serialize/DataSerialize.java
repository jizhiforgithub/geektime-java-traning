package com.jizhi.geektime.serialize;

import java.util.Comparator;

/**
 * 反序列化
 * @author jizhi7
 * @since 1.0
 **/
public interface DataSerialize {

    /**
     * 优先级比较器
     */
    Comparator<DataSerialize> PRIORITY_COMPARATOR = new PriorityComparator();

    boolean supportResultType(Class clazz);

    /**
     * 优先级
     * @return
     */
    int getPriority();

    /**
     * 序列化
     * @param obj
     * @return
     */
    <T> T serialize(Object obj, Class resultType);

    /**
     * 反序列化
     * @param value
     * @param clazz 反序列化的类型
     * @return
     */
    <T> Object deserialize(T value, Class clazz);


    class PriorityComparator implements Comparator<DataSerialize> {
        @Override
        public int compare(DataSerialize o1, DataSerialize o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }
}
