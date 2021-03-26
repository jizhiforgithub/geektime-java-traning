package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class LongConverter extends AbstractConverter<Long> {
    @Override
    protected Long doConvert(String value) {
        return Long.valueOf(value);
    }
}
