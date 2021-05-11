package com.jizhi.geektime.projects.user.repository;

import com.jizhi.geektime.projects.user.domain.User;

import java.util.Collection;

/**
 * 用户存储仓库
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface UserRepository {

    boolean save(User user);

    boolean deleteById(Long id);

    boolean update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    Collection<User> getAll();

}
