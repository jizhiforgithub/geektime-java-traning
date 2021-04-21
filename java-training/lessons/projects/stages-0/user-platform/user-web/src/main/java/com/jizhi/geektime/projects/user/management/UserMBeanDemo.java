package com.jizhi.geektime.projects.user.management;

import com.jizhi.geektime.projects.user.domain.User;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * 2021/4/19
 * jizhi7
 **/
public class UserMBeanDemo {

    public static void main(String[] args) throws Exception {

        // 获取平台 MBean Server
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        // 为 UserMXBean 定义 ObjectName
        ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
        // 创建 UserMBean 实例
        User user = new User();
        // 注册MBean
        mBeanServer.registerMBean(createUserMBean(user), objectName);
        while (true) {
            Thread.sleep(2000);
            System.out.println(user);
        }
    }

    private static Object createUserMBean(User user) throws Exception {
        return new UserManager(user);
    }

}
