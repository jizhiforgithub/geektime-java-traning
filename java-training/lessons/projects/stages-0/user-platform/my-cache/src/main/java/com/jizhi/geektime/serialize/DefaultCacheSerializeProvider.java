package com.jizhi.geektime.serialize;

/**
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultCacheSerializeProvider implements SerializeProvider {

    private static CompositeSerialize compositeSerialize = new CompositeSerialize();

    @Override
    public DataSerialize getSerialize() {
        return compositeSerialize;
    }
}
