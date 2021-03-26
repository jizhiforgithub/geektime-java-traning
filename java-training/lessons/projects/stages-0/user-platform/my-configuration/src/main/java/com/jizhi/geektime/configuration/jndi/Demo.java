package com.jizhi.geektime.configuration.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;

/**
 * 2021/3/14
 * jizhi7
 **/
public class Demo {

    public static void main(String[] args) throws NamingException {

        Context context = new InitialContext();
        context.lookup("maxVal");

    }

}
