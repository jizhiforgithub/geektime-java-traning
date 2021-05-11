package com.jizhi.geektime.cache;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

/**
 * K V 键值对
 *
 * @author jizhi7
 * @since 1.0
 **/
class KeyValueTypePair {

    private final Class<?> keyType;
    private final Class<?> valueType;

    KeyValueTypePair(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyValueTypePair that = (KeyValueTypePair) o;
        return Objects.equals(keyType, that.keyType) && Objects.equals(valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyType, valueType);
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    /**
     * 解析类的信息，创建 KV 对
     *
     * @param targetClass
     * @return
     */
    public static KeyValueTypePair resolve(Class<?> targetClass) {
        // 判定class不是接口和抽象类
        assertCache(targetClass);

        KeyValueTypePair pair = null;
        while (targetClass != null) {
            // 从class实现的接口从解析 KV 对
            pair = resolveFromInterfaces(targetClass);
            if (pair != null) {
                break;
            }
            // 返回继承的父类，具有泛型参数
            Type superType = targetClass.getGenericSuperclass();
            // 如果是由泛型参数的，就是一个参数化类型了
            if (superType instanceof ParameterizedType) {
                pair = resolveFromType(superType);
            }

            if (pair != null) {
                break;
            }
            // recursively
            // 直接返回继承的父类，由于编译器泛型擦除，没有泛型参数
            targetClass = targetClass.getSuperclass();

        }

        return pair;
    }

    /**
     * 判定缓存实现类，不是接口和抽象类
     *
     * @param cacheClass 缓存实现类
     */
    private static void assertCache(Class<?> cacheClass) {
        if (cacheClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Cache must not be an interface!");
        }
        if (Modifier.isAbstract(cacheClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Cache must not be abstract!");
        }
    }

    /**
     * 从类的实现接口中解析，获得KV
     *
     * @param type
     * @return
     */
    private static KeyValueTypePair resolveFromInterfaces(Class<?> type) {
        KeyValueTypePair pair = null;
        // 获取类实现的接口
        for (Type superInterface : type.getGenericInterfaces()) {
            // 解析接口的K,V获得
            pair = resolveFromType(superInterface);
            if (pair != null) {
                break;
            }
        }
        return pair;
    }

    /**
     * 从Type类型中解析KV键值对
     * Type是所有类型的父接口,
     * 如原始类型(raw types 对应 Class)、
     * 参数化类型(parameterized types 对应 ParameterizedType)、
     * 数组类型(array types 对应 GenericArrayType)、
     * 类型变量(type variables 对应 TypeVariable )和
     * 基本(原生)类型(primitive types 对应 Class),。
     *
     * @param type
     * @return
     */
    private static KeyValueTypePair resolveFromType(Type type) {

        KeyValueTypePair pair = null;
        // 是参数化类型，就是 Lis<String> 这样的
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            // 获取
            if (pType.getRawType() instanceof Class) {
                // 返回List的type
                Class<?> rawType = (Class) pType.getRawType();
                // 获取泛型变量，String的type
                Type[] arguments = pType.getActualTypeArguments();
                // 是两个泛型变量，一个是K，一个是V
                if (arguments.length == 2) {
                    Type keyTypeArg = arguments[0];
                    Type valueTypeArg = arguments[1];
                    // 将Type转成Class
                    Class<?> keyType = asClass(keyTypeArg);
                    Class<?> valueType = asClass(valueTypeArg);
                    if (keyType != null && valueType != null) {
                        pair = new KeyValueTypePair(keyType, valueType);
                    }
                }
            }
        }
        return pair;
    }

    /**
     * 参数类型，转为类对象
     *
     * @param typeArgument
     * @return
     */
    private static Class<?> asClass(Type typeArgument) {
        if (typeArgument instanceof Class) {
            return (Class<?>) typeArgument;
        }
        // 类型变量（泛型变量）
        else if (typeArgument instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) typeArgument;
            // 返回边界
            return asClass(typeVariable.getBounds()[0]);
        }
        return null;
    }
}
