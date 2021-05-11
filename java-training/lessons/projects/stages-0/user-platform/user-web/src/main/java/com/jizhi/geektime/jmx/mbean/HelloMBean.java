package com.jizhi.geektime.jmx.mbean;

/**
 * 2021/3/16
 * jizhi7
 **/
public interface HelloMBean {

    String getName();

    void setName(String name);

    void printHello();

    void printHello(String whoName);

}
