package com.jizhi.geektime.projects.user.validator.bean.validation;

import com.jizhi.geektime.projects.user.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private int idRange;

    @Override
    public void initialize(UserValid annotation) {
        this.idRange = annotation.idRange();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        // 获取模板信息
        context.getDefaultConstraintMessageTemplate();

        return false;
    }
}
