package com.jizhi.geektime.projects.user.service;

import com.jizhi.geektime.projects.user.domain.User;

import java.util.Collection;

/**
 * 用户服务
 *
 * @author jizhi7
 * @since 1.0
 **/
public interface IUserService {

    /**
     * 用户注册
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    boolean register(User user);

    /**
     * 注销用户
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    boolean deregister(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    boolean update(User user);

    /**
     * 根据用户Id查询用户信息
     *
     * @param id 用户id
     * @return 返回用户id对应的用户信息，没有则返回<code>null</code>
     */
    User queryUserById(Long id);

    /**
     * 根据用户的姓名和密码查询用户信息
     *
     * @param userName 用户姓名
     * @param password 用户密码
     * @return 对应的用户信息，没有则返回<code>null</code>
     */
    User queryUserByNameAndPassword(String userName, String password);

    /*Collection<User> queryAll();

    void test();*/
}
