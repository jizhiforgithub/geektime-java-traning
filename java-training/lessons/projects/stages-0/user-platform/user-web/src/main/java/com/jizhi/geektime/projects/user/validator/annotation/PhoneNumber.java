package com.jizhi.geektime.projects.user.validator.annotation;

import com.jizhi.geektime.projects.user.validator.impl.PhoneNumberConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 电话校验，采用中国大陆方式（11 位校验）
 *
 * @author jizhi7
 * @since 1.0
 **/

@Documented
@Constraint(validatedBy = {PhoneNumberConstraintValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface PhoneNumber {

    String message() default "{com.jizhi.geektime.projects.user.validator.annotation.PhoneNumber.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
