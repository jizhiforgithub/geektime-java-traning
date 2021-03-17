package com.jizhi.geektime.projects.user.validator;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.web.validator.ValidatorDelegate;

import java.util.Map;

/**
 * 2021/3/7
 * jizhi7
 **/
public class ValidatorTest {

    public static void main(String[] args) {

        User user = new User();

        ValidatorDelegate v = new ValidatorDelegate();
        Map<String, String> re = v.validate(user);
        System.out.println(re);

    }

}
