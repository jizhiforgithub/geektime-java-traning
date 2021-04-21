package com.jizhi.geektime.projects.user.enums;

// 底层实际 public final class UserType extends java.lang.Enum
public enum UserType {

    VIP,
    NORMAL;

    // 枚举中构造器是 private
    UserType(){

    }

    public static void main(String[] args) {
        System.out.println(UserType.VIP.name());
        System.out.println(UserType.VIP.ordinal());
        System.out.println(UserType.NORMAL.ordinal());
    }

}
