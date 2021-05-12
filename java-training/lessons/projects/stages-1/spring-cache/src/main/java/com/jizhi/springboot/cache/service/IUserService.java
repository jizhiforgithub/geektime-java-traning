package com.jizhi.springboot.cache.service;


import com.jizhi.springboot.cache.repository.domain.User;

import java.util.Collection;

public interface IUserService {

    Collection<User> getUserAllList();

    User getUserById(int id);

    User modifyUserAgeById(int id, int age);

    boolean deleteUserBuId(int id);

    Collection<User> getUserByName(String name);
}
