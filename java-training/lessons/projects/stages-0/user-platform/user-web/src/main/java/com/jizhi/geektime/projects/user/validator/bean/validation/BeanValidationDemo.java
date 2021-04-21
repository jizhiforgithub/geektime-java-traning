package com.jizhi.geektime.projects.user.validator.bean.validation;

import com.jizhi.geektime.projects.user.domain.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * 2021/4/19
 * jizhi7
 **/
public class BeanValidationDemo {

    public static void main(String[] args) {

        // 校验工厂
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // 获取校验器
        Validator validator = factory.getValidator();

        User user = new User();
        user.setPassword("***");

        // 获取校验结果
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        violations.forEach(c -> {
            System.out.println(c.getMessage());
        });
    }

}
