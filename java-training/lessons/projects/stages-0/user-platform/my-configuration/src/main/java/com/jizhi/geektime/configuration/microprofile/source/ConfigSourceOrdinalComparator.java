package com.jizhi.geektime.configuration.microprofile.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Comparator;

/**
 * 配置源优先级比较器
 * @author jizhi7
 * @since 1.0
 **/
public class ConfigSourceOrdinalComparator implements Comparator<ConfigSource> {

    /**
     * 单例模式
     */
    public static final Comparator<ConfigSource> INSTANCE = new ConfigSourceOrdinalComparator();

    private ConfigSourceOrdinalComparator() {

    }

    @Override
    public int compare(ConfigSource o1, ConfigSource o2) {
        return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
    }
}
