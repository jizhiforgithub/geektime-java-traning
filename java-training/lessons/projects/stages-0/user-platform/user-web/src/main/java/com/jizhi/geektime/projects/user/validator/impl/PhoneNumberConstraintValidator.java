package com.jizhi.geektime.projects.user.validator.impl;

import com.jizhi.geektime.projects.user.validator.annotation.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话校验，采用中国大陆方式（11 位校验）
 * @author jizhi7
 * @since 1.0
 **/
public class PhoneNumberConstraintValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final Pattern CHINA_PATTERN = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");


    @Override
    public void initialize(PhoneNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value != null && !"".equals(value)) {
            Matcher m = CHINA_PATTERN.matcher(value);
            return m.matches();
        }
        return false;
    }

}
