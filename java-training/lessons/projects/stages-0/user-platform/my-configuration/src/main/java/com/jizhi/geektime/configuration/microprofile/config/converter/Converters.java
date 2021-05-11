package com.jizhi.geektime.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 配置类转换器的集合
 *
 * @author jizhi7
 * @since 1.0
 **/
public class Converters implements Iterable<Converter> {

    public static final int DEFAULT_PRIORITY = 100;

    /**
     * 转换器Map集合，
     * key：可以转换的类，val：转换器
     * 转换器集合使用 PriorityQueue 堆
     */
    private final Map<Class<?>, PriorityQueue<PrioritizedConverter>> typedConverters = new HashMap<>();

    private ClassLoader classLoader;

    private boolean addedDiscoveredConverters = false;

    public Converters() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public Converters(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 添加 找到的 转换器
     * 默认是配置了 spi 的转换器
     */
    public void addDiscoveredConverters() {
        if (addedDiscoveredConverters) {
            return;
        }
        addConverters(ServiceLoader.load(Converter.class, classLoader));
        addedDiscoveredConverters = true;
    }

    /**
     * 添加转换器
     *
     * @param converters 转换器迭代器
     */
    public void addConverters(Iterable<Converter> converters) {
        converters.forEach(this::addConverter);
    }

    /**
     * 添加转换器， 设置优先级为默认优先级 100 {@link #DEFAULT_PRIORITY}
     *
     * @param converter 转换器
     */
    public void addConverter(Converter converter) {
        addConverter(converter, DEFAULT_PRIORITY);
    }

    /**
     * 添加转换器
     *
     * @param converter 转换器
     * @param priority  优先级
     */
    public void addConverter(Converter converter, int priority) {
        Class<?> convertedType = resolveConvertedType(converter);
        addConverter(converter, priority, convertedType);
    }

    /**
     * 添加转换器
     *
     * @param converter     转换器
     * @param priority      优先级
     * @param convertedType 转换的类型
     */
    public void addConverter(Converter converter, int priority, Class<?> convertedType) {
        PriorityQueue priorityQueue = typedConverters.computeIfAbsent(convertedType, t -> new PriorityQueue<>());
        priorityQueue.offer(new PrioritizedConverter(converter, priority));
    }

    /**
     * 解析转换器支持的转换类型，获取转换器的泛型
     *
     * @param converter 转换器
     * @return 转换器能转换的类型
     */
    protected Class<?> resolveConvertedType(Converter<?> converter) {
        // 转换器不是接口、抽象类判断
        assertConverter(converter);
        Class<?> convertedType = null;
        Class<?> converterClass = converter.getClass();
        while (converterClass != null) {
            convertedType = resolveConvertedType(converterClass);
            if (convertedType != null) {
                break;
            }
            // 获得带有泛型的父类
            // Type是 Java 编程语言中所有类型的公共高级接口。
            // 它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
            Type superType = converterClass.getGenericSuperclass();
            // ParameterizedType参数化类型，即泛型
            if (superType instanceof ParameterizedType) {
                convertedType = resolveConvertedType(superType);
            }

            if (convertedType != null) {
                break;
            }
            // recursively
            converterClass = converterClass.getSuperclass();
        }

        return convertedType;
    }

    /**
     * 转换器是否是一个接口判断
     * 转换器是否是抽象类判断
     *
     * @param converter
     */
    private void assertConverter(Converter<?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Converter must not be an interface!");
        }
        if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Converter must not be abstract!");
        }
    }


    /**
     * 解析转换器支持的类型
     *
     * @param converterClass 转换器的类
     * @return
     */
    private Class<?> resolveConvertedType(Class<?> converterClass) {
        Class<?> convertedType = null;
        // 遍历转换器类实现的所有接口
        for (Type superInterface : converterClass.getGenericInterfaces()) {
            convertedType = resolveConvertedType(superInterface);
            if (convertedType != null) {
                break;
            }
        }
        return convertedType;
    }

    /**
     * 解析转换器类型
     *
     * @param type 转换器的接口类
     * @return
     */
    private Class<?> resolveConvertedType(Type type) {
        Class<?> convertedType = null;
        // 如果是泛型化参数
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            //
            if (pType.getRawType() instanceof Class) {
                Class<?> rawType = (Class) pType.getRawType();
                if (Converter.class.isAssignableFrom(rawType)) {
                    Type[] arguments = pType.getActualTypeArguments();
                    if (arguments.length == 1 && arguments[0] instanceof Class) {
                        convertedType = (Class) arguments[0];
                    }
                }
            }
        }
        return convertedType;
    }

    /**
     * 添加转换器
     *
     * @param converters
     */
    public void addConverters(Converter... converters) {
        addConverters(Arrays.asList(converters));
    }

    /**
     * 根据类型，获取所有支持的转换器
     *
     * @param convertedType
     * @return
     */
    public List<Converter> getConverters(Class<?> convertedType) {
        PriorityQueue<PrioritizedConverter> prioritizedConverters = typedConverters.get(convertedType);
        if (prioritizedConverters == null || prioritizedConverters.isEmpty()) {
            return Collections.emptyList();
        }
        // 转换为converter
        List<Converter> converters = new LinkedList<>();
        for (PrioritizedConverter prioritizedConverter : prioritizedConverters) {
            converters.add(prioritizedConverter.getConverter());
        }
        return converters;
    }

    /**
     * 转换器迭代
     *
     * @return 迭代器
     */
    @Override
    public Iterator<Converter> iterator() {
        List<Converter> allConverters = new LinkedList<>();
        // 将所有的转换器方法一个 List 列表中
        for (PriorityQueue<PrioritizedConverter> converters : typedConverters.values()) {
            for (PrioritizedConverter converter : converters) {
                allConverters.add(converter.getConverter());
            }
        }
        return allConverters.iterator();
    }

    public void setClassLoader(ClassLoader loader) {
        this.classLoader = loader;
    }
}
