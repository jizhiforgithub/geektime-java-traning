package com.jizhi.geektime.configuration.microprofile;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * 字符类型到Integer类型的转换器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class StringToIntegerConverter implements Converter<Integer> {

    @Override
    public Integer convert(String value) throws IllegalArgumentException, NullPointerException {
        return Integer.valueOf(value);
    }

}
