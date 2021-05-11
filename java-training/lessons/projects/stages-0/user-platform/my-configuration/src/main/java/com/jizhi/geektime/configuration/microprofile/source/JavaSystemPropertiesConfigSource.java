package com.jizhi.geektime.configuration.microprofile.source;

import java.util.Map;

/**
 * Java 系统属性配资源
 *
 * @author jizhi7
 * @since 1.0
 **/
public class JavaSystemPropertiesConfigSource extends MapBasedConfigSource {

    /**
     * 配置源名称
     */
    public static final String CONFIG_NAME = "Java System Properties";

    /**
     * 配资源优先级
     */
    public static final int CONFIG_ORDINAL = 400;

    public JavaSystemPropertiesConfigSource() {
        super(CONFIG_NAME, CONFIG_ORDINAL);
    }

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     *
     * @return {@link System#getProperties()}
     */
    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        configData.putAll(System.getProperties());
    }
}
