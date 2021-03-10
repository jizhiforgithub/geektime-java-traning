package com.jizhi.geektime.projects.user.service.impl;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.projects.user.repository.UserRepository;
import com.jizhi.geektime.projects.user.transaction.annotation.LocalPropagation;
import com.jizhi.geektime.projects.user.transaction.annotation.LocalTransactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户服务2，测试事务
 * @author jizhi7
 * @since 1.0
 **/
public class UserService2 {

    @Resource(name = "bean/DatabaseUserRepository")
    public UserRepository userRepository;



    @LocalTransactional(propagation = LocalPropagation.PROPAGATION_NESTED)
    public void test2(@Valid User user2, Map<String,String> user2Errors) {
        User user = new User();
        user.setName("222");
        user.setEmail("222");
        user.setPassword("****");
        user.setPhoneNumber("2222");
        userRepository.save(user);
        int a = 1/0;
    }

}
