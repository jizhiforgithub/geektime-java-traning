package com.jizhi.geektime.configuration.microprofile.config.converter;

/**
 * 2021/3/24
 * jizhi7
 **/
public class CharacterConverter extends AbstractConverter<Character> {
    @Override
    protected Character doConvert(String value) {
        if(value.isEmpty()) {
            return null;
        }
        return Character.valueOf(value.charAt(0));
    }
}
