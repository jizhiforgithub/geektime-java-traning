package com.jizhi.geektime.test;

import java.util.ArrayList;
import java.util.List;

/**
 * 2021/3/23
 * jizhi7
 **/
public class Parent {

    private List<String> names = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public void addName(String name) {
        this.names.add(name);
    }

    public Parent my() {
        return this;
    }
}
