package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class FloatConverter extends AbstractConverter<Float> {
    @Override
    protected Float doConvert(String value) {
        return Float.valueOf(value);
    }
}
