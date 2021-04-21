package com.jizhi.geektime.projects.user.management;

import com.jizhi.geektime.projects.user.domain.User;

/**
 * {@link User} MBean 接口的描述
 **/
public interface UserManagerMBean {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getPassword();

    void setPassword(String password);

    String getEmail();

    void setEmail(String email);

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    // MBeanOperationInfo
    String toString();

    User getUser();

}
