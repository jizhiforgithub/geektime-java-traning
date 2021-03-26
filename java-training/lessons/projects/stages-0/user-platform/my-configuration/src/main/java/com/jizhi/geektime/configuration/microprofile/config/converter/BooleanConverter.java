package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class BooleanConverter extends AbstractConverter<Boolean> {
    @Override
    protected Boolean doConvert(String value) {
        return Boolean.parseBoolean(value);
    }
}
