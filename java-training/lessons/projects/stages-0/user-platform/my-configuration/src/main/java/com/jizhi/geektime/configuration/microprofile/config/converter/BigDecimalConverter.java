package com.jizhi.geektime.configuration.microprofile.config.converter;

import java.math.BigDecimal;

/**
 * 2021/3/24
 * jizhi7
 **/
public class BigDecimalConverter extends AbstractConverter<BigDecimal> {
    @Override
    protected BigDecimal doConvert(String value) {
        return new BigDecimal(value);
    }
}
