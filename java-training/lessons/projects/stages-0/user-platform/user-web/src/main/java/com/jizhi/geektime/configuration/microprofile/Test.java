package com.jizhi.geektime.configuration.microprofile;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.ServiceLoader;

/**
 * 测试
 * @author jizhi7
 * @since 1.0
 **/
public class Test {

    public static void main(String[] args) {

        ConfigProviderResolver provider = ServiceLoader.load(ConfigProviderResolver.class).iterator().next();
        String propertyName = "sun.arch.data.model";
        Integer value = provider.getConfig().getValue(propertyName, Integer.class);
        System.out.println(value);

    }

}
