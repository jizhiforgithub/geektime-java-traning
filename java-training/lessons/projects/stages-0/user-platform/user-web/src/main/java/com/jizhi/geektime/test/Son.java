package com.jizhi.geektime.test;

/**
 * 2021/3/23
 * jizhi7
 **/
public class Son extends Parent {

    public Parent getParent() {
        return super.my();
    }

}

class Test{
    public static void main(String[] args) {
        Parent p = new Parent();

        Son s = new Son();
        s.addName("lili");
        Parent p2 = s.getParent();
        System.out.println(s.getNames());
        System.out.println(p.getNames());
        System.out.println(p2.getNames());
    }
}
