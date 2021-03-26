package com.jizhi.geektime.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * 字符类型转换器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class StringConverter implements Converter<String> {

    @Override
    public String convert(String value) throws IllegalArgumentException, NullPointerException {
        return value;
    }

}
