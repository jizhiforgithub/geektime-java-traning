package com.jizhi.geektime.projects.user.service.impl;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.projects.user.repository.DatabaseUserRepository;
import com.jizhi.geektime.projects.user.repository.UserRepository;
import com.jizhi.geektime.projects.user.service.IUserService;

import java.util.Collection;

/**
 * 2021/3/2
 * jizhi7
 **/
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    public UserServiceImpl() {
        this.userRepository = new DatabaseUserRepository();
    }

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
    public Collection<User> queryAll() {
        return userRepository.getAll();
    }
}
