package com.jizhi.geektime.projects.spring.cloud.config;

import org.springframework.beans.BeansException;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 本地文件系统的 {@link PropertySourceLocator} 实现
 * 文件格式支持 properties
 */
public class LocalFilePropertySourceLocator implements PropertySourceLocator, ApplicationContextAware {

    private static final String LOCAL_FILE_PROPERTY_SOURCE_NAME = "localFile";
    private static final String propertiesLocal = "META-INF/config/default.properties";
    private static final Object localFilePropertySourceMonitor = new Object();
    private ConfigurableEnvironment environment;

    @Override
    public PropertySource<?> locate(Environment environment) {
        CompositePropertySource composite = new CompositePropertySource(
                LOCAL_FILE_PROPERTY_SOURCE_NAME);
        loadPropertySources(composite);
        return composite;
    }

    private void loadPropertySources(CompositePropertySource composite) {
        List<PropertySource<?>> propertySources = loadResource(propertiesLocal);
        propertySources.forEach(composite::addPropertySource);
    }

    private List<PropertySource<?>> loadResource(String path) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(propertiesLocal);
        PropertySourceLoader loader;
        if (resource.getFilename().endsWith(".yaml") || resource.getFilename().endsWith(".yml")) {
            loader = new YamlPropertySourceLoader();
        } else {
            loader = new PropertiesPropertySourceLoader();
        }
        try {
            return loader.load(resource.getFilename(), resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void onFileResourceChange(Resource resource) {
        if (!resource.isFile()) {
            return;
        }
        try {
            File file = resource.getFile();
            Path parentPath = file.getParentFile().toPath();
            FileSystem fileSystem = FileSystems.getDefault();
            // 监听器
            WatchService watchService = fileSystem.newWatchService();
            // 监听文件修改事件
            parentPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            processFileCChange(resource.getFilename(), watchService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ThreadPoolExecutor fileChangeThreadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(0), new CustomizableThreadFactory("file-change-thread-pool-"));

    private void processFileCChange(String fileName, WatchService watchService) {
        fileChangeThreadPool.submit(() -> {
            while (true) {
                WatchKey key = watchService.take();
                try {
                    if (key.isValid()) {
                        for (WatchEvent<?> watchEvent : key.pollEvents()) {
                            Watchable watchable = key.watchable();
                            // 目录路径（监听注册的目录）
                            Path dirPath = (Path) watchable;
                            Path fileRelativePath = (Path) watchEvent.context();
                            if (fileName.equals(fileRelativePath.getFileName())) {
                                // 处理为绝对路径
                                Path filePath = dirPath.resolve(fileRelativePath);
                                System.out.println("修改的文件：" + filePath);
                                synchronized (localFilePropertySourceMonitor) {
                                    List<PropertySource<?>> propertySources = loadResource(filePath.toAbsolutePath().toString());
                                    MutablePropertySources propertySources1 = this.environment.getPropertySources();
                                    CompositePropertySource composite = new CompositePropertySource(
                                            LOCAL_FILE_PROPERTY_SOURCE_NAME);
                                    for (PropertySource<?> source : propertySources) {
                                        composite.addPropertySource(source);
                                    }
                                    propertySources1.replace(LOCAL_FILE_PROPERTY_SOURCE_NAME, composite);
                                }
                            }
                        }
                    }
                } finally {
                    key.reset();
                }

            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
    }
}
