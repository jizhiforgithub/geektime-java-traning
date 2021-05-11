package com.jizhi.geektime.web.validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * BeanValidator bean校验委托类
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ValidatorDelegate {

    private Validator validator = null;

    public ValidatorDelegate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public <T> Map<String, String> validate(T t) {
        Set<ConstraintViolation<T>> result = this.validator.validate(t);
        Map<String, String> error = new HashMap<>();

        if (result != null) {
            result.forEach(r -> error.put(r.getPropertyPath().toString(), r.getMessage()));
        }
        return error;
    }

}
