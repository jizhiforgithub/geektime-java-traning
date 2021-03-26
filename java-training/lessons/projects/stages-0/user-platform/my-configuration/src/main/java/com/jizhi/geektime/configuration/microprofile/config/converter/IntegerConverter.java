package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class IntegerConverter extends AbstractConverter<Integer> {
    @Override
    protected Integer doConvert(String value) {
        return Integer.valueOf(value);
    }
}
