package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class ByteConverter extends AbstractConverter<Byte> {
    @Override
    protected Byte doConvert(String value) {
        return Byte.valueOf(value);
    }
}
