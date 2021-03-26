package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class DoubleConverter extends AbstractConverter<Double> {
    @Override
    protected Double doConvert(String value) {
        return Double.valueOf(value);
    }
}
