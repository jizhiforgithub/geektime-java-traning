package com.jizhi.geektime.serialize;

/**
 *
 * @author jizhi7
 * @since 1.0
 **/
public class SerializeProvider {

    private static CompositeSerialize compositeSerialize = new CompositeSerialize();

    public static DataSerialize getSerialize() {
        return compositeSerialize;
    }
}
