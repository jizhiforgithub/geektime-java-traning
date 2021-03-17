package com.jizhi.geektime.projects.user.ioc;

import com.jizhi.geektime.ioc.Container;
import com.jizhi.geektime.projects.user.proxy.ProxyCallBack;
import com.jizhi.geektime.projects.user.proxy.ProxyUtils;
import com.jizhi.geektime.projects.user.transaction.TransactionalCallBack;
import com.jizhi.geektime.projects.user.transaction.annotation.LocalTransactional;
import com.jizhi.geektime.projects.user.validator.proxy.ValidatorCallBack;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IoC容器，从jndi容器中取bean
 *
 * @author jizhi7
 * @since 1.0
 **/
public class IoCContainer implements Container {

    /**
     * jndi根路径
     */
    private static final String JNDI_ROOT_NAME = "java:comp/env";
    /**
     * IoC容器名称，初始化，使用该名称放在servletContext中
     */
    public static final String IoC_NAME = IoCContainer.class.getName();

    /**
     * 需要创建代理的 annotation
     */
    private List<Class<? extends Annotation>> needProxyAnnotations = new ArrayList<>();

    private List<String> jndiNames = new ArrayList<>();

    /**
     * 早期的bean，刚从jndi容器中取出来，还没执行@PostConstruct方法，还没执行依赖注入
     */
    private Map<String, Object> earlySingletonObjects = new HashMap<>();

    /**
     * IoC初始化后，完整的bean容器
     */
    private Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * servletContext缓存
     */
    public static final Map<ClassLoader, ServletContext> currentContextPerThread =
            new ConcurrentHashMap<>(1);

    private Context context;

    public IoCContainer() {
        try {
            this.context = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static void addServletContext(ClassLoader classLoader, ServletContext context) {
        IoCContainer.currentContextPerThread.put(classLoader, context);
    }

    @Override
    public Object getObject(String jndiName) {
        Object obj = singletonObjects.get(jndiName);
        if (obj == null) {
            obj = earlySingletonObjects.get(jndiName);
            if (obj == null) {
                obj = loadJndiBeanByBeanName(jndiName);
            }
            if (obj == null) {
                throw new RuntimeException("no such bean , bean name : " + jndiName);
            }
            earlySingletonObjects.remove(jndiName);
            // 初始化
            doInitalionlized(obj);
            singletonObjects.put(jndiName, obj);
            // 注入
            doInject(obj);
        }
        return obj;
    }

    /**
     * 从jndi中根据名字查找bean
     *
     * @param jndiName 名称
     * @return bean实例
     */
    private Object loadJndiBeanByBeanName(String jndiName) {
        return loadJndiBeanByFullName(JNDI_ROOT_NAME + "/" + jndiName);
    }

    /**
     * 从jndi中根据名字查找bean
     *  如果需要的话，就创建代理类
     * @param name 名称, 带有根路径的全名称
     * @return bean实例
     */
    private Object loadJndiBeanByFullName(String name) {
        try {
            Object obj = this.context.lookup(name);
            // 创建代理类
            Object proxyObj = createProxyIfNecessary(name, obj);
            return proxyObj == null ? obj : proxyObj;
        } catch (NamingException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public Container getParentContainer() {
        return null;
    }

    @Override
    public void setParentContainer(Container container) {
        throw new RuntimeException("no ");
    }

    /**
     * 将传递进来的对象，实现依赖注入
     * @param obj 注入的对象
     */
    private void doInject(Object obj) {

        try {
            Class<?> clazz = obj.getClass();
            // 如果这个对象是CGlib提升过的，只注入它代理的对象
            if(clazz.getName().contains("$$EnhancerByCGLIB")) {
                // 拿到回调接口
                ProxyCallBack callback = (ProxyCallBack)clazz.getDeclaredMethod("getCallback", int.class).invoke(obj, 0);
                // 拿到回调接口里面的目标对象
                obj = callback.getTarget();
                // 获取代理对象的类
                clazz = obj.getClass();
            }
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Resource.class)) {
                    String jndiName = field.getAnnotation(Resource.class).name();
                    Object fieldObj = getObject(jndiName);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(obj, fieldObj);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getCause());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建代理对象
     *
     * @param jndiName bean名称
     * @param obj      被代理对象
     * @return 代理对象
     */
    private Object createProxyIfNecessary(String jndiName, Object obj) {
        Object result = null;
        Method[] methods = obj.getClass().getDeclaredMethods();
        boolean isNeedProxy = false;
        for (Method method : methods) {
            for (Class<? extends Annotation> an : needProxyAnnotations) {
                if (method.isAnnotationPresent(an)) {
                    isNeedProxy = true;
                    break;
                }
            }
        }
        if (isNeedProxy) {
            ProxyCallBack callBack = new ProxyCallBack(obj);
            // 添加事务
            if (isNeedTransactionCallBack(methods)) {
                TransactionalCallBack transactionalCallBack = new TransactionalCallBack();
                callBack.addBeforeInvoker(transactionalCallBack);
                callBack.addAfterInvoker(transactionalCallBack);
                callBack.addThrowableInvoker(transactionalCallBack);
                callBack.addFinallyInvoker(transactionalCallBack);
            }
            // 添加校验
            if (isNeedValidatorCallBack(methods)) {
                callBack.addBeforeInvoker(new ValidatorCallBack());
            }
            result = ProxyUtils.createProxy(obj.getClass(), callBack);
        }
        return result;
    }

    private boolean isNeedValidatorCallBack(Method[] methods) {
        for (Method method : methods) {
            for (Parameter parameter : method.getParameters()) {
                if (parameter.isAnnotationPresent(Valid.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNeedTransactionCallBack(Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(LocalTransactional.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化过程，调用 @PostConstruct 的方法
     * @param obj bean初始化对象
     */
    private void doInitalionlized(Object obj) {
        try {
            Method[] methods = obj.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class) &&
                        method.getParameterTypes().length == 0 &&
                        Modifier.STATIC != method.getModifiers()) {
                    method.invoke(obj);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private Object getObjectFromEarly(String jndiName) {
        return earlySingletonObjects.get(jndiName);
    }

    /**
     * IoC容器的初始化
     */
    public void init() {
        try {
            this.needProxyAnnotations.add(LocalTransactional.class);
            loadJndiNames(JNDI_ROOT_NAME);
            loadBean();
            loadJndiParams();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void loadJndiParams() throws NamingException {
        handleJndiParams("maxVal");
    }

    private void handleJndiParams(String paramName) throws NamingException {
        System.out.println("JNDI params : [" + paramName + "]=" + this.context.lookup(JNDI_ROOT_NAME + "/" + paramName));
    }

    /**
     * 根据bean名称，从jndi容器中加载所有的bean出来，放到earlySingletonObjects中
     */
    private void loadBean() {
        for (String name : jndiNames) {
            earlySingletonObjects.put(name.substring(JNDI_ROOT_NAME.length() + 1), loadJndiBeanByFullName(name));
        }
    }

    /**
     * 从jndi中加载出所有的bean名称
     * @param name    jndi bean路径
     */
    private void loadJndiNames(String name) {
        try {
            Object obj = this.context.lookup(name);
            if (obj instanceof Context) {
                NamingEnumeration<NameClassPair> naming = ((Context) obj).list("");
                while (naming.hasMore()) {
                    NameClassPair nameClassPair = naming.nextElement();
                    loadJndiNames(name + "/" + nameClassPair.getName());
                }
            } else {
                jndiNames.add(name);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取IoC容器实例
     *
     * @return
     */
    public static IoCContainer getInstance() {
        ServletContext context = currentContextPerThread.get(Thread.currentThread().getContextClassLoader());
        return (IoCContainer) context.getAttribute(IoCContainer.IoC_NAME);
    }

}
