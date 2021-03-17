package com.jizhi.geektime.configuration.perferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 偏好操作demo，windows基于注册表，linux基于文件
 *
 * @author jizhi7
 **/
public class PreferencesDemo {

    public static void main(String[] args) throws BackingStoreException {

        // 用户组， 在 Software\JavaSoft\Prefs 路劲下
        Preferences preferences = Preferences.userRoot();
        // 向注册表添加数据
        // 第一次放的时候，没有至，需要创建。此时运行需要管理员权限
        // cmd 运行的时候，java 加类名称运行的时候
        // java类名是包名 + 类名
        // classpath环境变量配置了 .; 说明cmd运行的时候，java会从到当前目录下查找这个类
        // 查找类是基于包名+类名的完整类名
        preferences.put("my-key", "Hello world");
        preferences.flush();
        System.out.println(preferences.get("my-key", "defult-val"));
        // 删除
        preferences.remove("my-key");


        WindowsRegistry myRoot = WindowsRegistry.currentUser();
        myRoot.set("my", "my-key", "my-val");
        myRoot.flush("my");
        System.out.println(myRoot.get("my", "my-key"));

    }

}


