package com.jizhi.geektime.jmx.mbean;

/**
 * 2021/3/16
 * jizhi7
 **/
public class Hello implements HelloMBean {

    private String name;

    public Hello(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void printHello() {
        System.out.println("hello " + name);
    }

    @Override
    public void printHello(String whoName) {
        System.out.println("hello " + whoName);
    }

}
