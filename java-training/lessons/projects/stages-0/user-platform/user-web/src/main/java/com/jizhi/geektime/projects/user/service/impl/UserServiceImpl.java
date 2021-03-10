package com.jizhi.geektime.projects.user.service.impl;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.projects.user.repository.DatabaseUserRepository;
import com.jizhi.geektime.projects.user.repository.UserRepository;
import com.jizhi.geektime.projects.user.service.IUserService;
import com.jizhi.geektime.projects.user.transaction.TransactionalCallBack;
import com.jizhi.geektime.projects.user.transaction.annotation.LocalPropagation;
import com.jizhi.geektime.projects.user.transaction.annotation.LocalTransactional;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * 用户服务
 * @author jizhi7
 * @since 1.0
 **/
public class UserServiceImpl implements IUserService {

    @Resource(name = "bean/DatabaseUserRepository")
    private UserRepository userRepository;

    @Resource(name = "bean/UserService2")
    private UserService2 userService2;

    @Override
    public boolean register(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String userName, String password) {
        return null;
    }

    @Override
    @LocalTransactional
    public Collection<User> queryAll() {
        return userRepository.getAll();
    }

    @LocalTransactional
    @Override
    public void test() {
        User user = new User();
        user.setName("111");
        user.setEmail("111");
        user.setPassword("****");
        user.setPhoneNumber("111");
        userRepository.save(user);
        userService2.test2(user, null);
        int s = 2/0;
    }

}
