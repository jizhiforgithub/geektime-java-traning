package com.jizhi.springboot.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.support.WebApplicationContextUtils;

@SpringBootApplication
@ComponentScan(basePackages = "com.jizhi")
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);



    }

}
