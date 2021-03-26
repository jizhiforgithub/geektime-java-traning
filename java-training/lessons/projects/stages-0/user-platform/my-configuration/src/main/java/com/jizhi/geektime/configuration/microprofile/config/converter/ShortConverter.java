package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/23
 * jizhi7
 **/
public class ShortConverter extends AbstractConverter<Short> {
    @Override
    protected Short doConvert(String value) {
        return Short.parseShort(value);
    }
}
