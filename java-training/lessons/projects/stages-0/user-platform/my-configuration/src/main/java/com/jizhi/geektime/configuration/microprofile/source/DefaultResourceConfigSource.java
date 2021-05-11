package com.jizhi.geektime.configuration.microprofile.source;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 默认 resource 资源下的配置数据源
 *
 * @author jizhi7
 * @since 1.0
 **/
public class DefaultResourceConfigSource extends MapBasedConfigSource {

    /**
     * 配置源所在的文件
     */
    public static final String configFileLocation = "META-INF/microprofile-config.properties";

    /**
     * 配置源名称
     */
    public static final String CONFIG_NAME = "Default Config File";

    /**
     * 配资源优先级
     */
    public static final int CONFIG_ORDINAL = 100;

    private final Logger logger = Logger.getLogger(this.getClass().getName());


    public DefaultResourceConfigSource() {
        super(CONFIG_NAME, CONFIG_ORDINAL);
    }

    /**
     * 准备配置元数据
     * 读取对应文件数据，转化成properties
     *
     * @param configData
     * @throws Throwable
     */
    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(configFileLocation);
        if (resource == null) {
            logger.info("The default config file can't be found in the classpath : " + configFileLocation);
            return;
        }
        try (InputStream inputStream = resource.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            configData.putAll(properties);
        }
    }
}
