package com.jizhi.geektime.configuration.microprofile.config.converter;

import java.math.BigInteger;

/**
 * 2021/3/24
 * jizhi7
 **/
public class BigIntegerConverter extends AbstractConverter<BigInteger> {
    @Override
    protected BigInteger doConvert(String value) {
        return new BigInteger(value);
    }
}
