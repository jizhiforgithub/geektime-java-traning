package com.jizhi.geektime.configuration.perferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 2021/3/14
 * jizhi7
 **/
public class Test {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> objectClass = A.class;
        Method method = objectClass.getDeclaredMethod("aa");
        method.setAccessible(true);
        Object re = method.invoke(null);
        System.out.println(re);
    }
}

class A {
    private static int aa() {
        System.out.println("aa");
        return 1;
    }
}
