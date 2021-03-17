package com.jizhi.geektime.jmx.mbean;

/**
 * 2021/3/16
 * jizhi7
 **/
public class My implements MyMBean {

    private String name;
    private int age;

    public My() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "My{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
