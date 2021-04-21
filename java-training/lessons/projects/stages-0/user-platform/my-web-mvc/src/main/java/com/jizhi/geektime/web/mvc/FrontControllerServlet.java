package com.jizhi.geektime.web.mvc;

import com.alibaba.fastjson.JSONObject;
import com.jizhi.geektime.configuration.microprofile.config.DefaultConfigProviderResolver;
import com.jizhi.geektime.ioc.Container;
import com.jizhi.geektime.web.mvc.controller.Controller;
import com.jizhi.geektime.web.mvc.controller.PageController;
import com.jizhi.geektime.web.mvc.controller.RestController;
import com.jizhi.geektime.web.validator.ValidatorDelegate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.Config;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MVC框架的请求调度类
 *
 * @author jizhi7
 * @since 1.0
 **/
public class FrontControllerServlet extends HttpServlet {


    /**
     * 请求路径和controller的映射关系缓存
     */
    private Map<String, Controller> controllersMapping = new HashMap<>();

    /**
     * 请求路径和处理方法 {@link HandlerMethodInfo} 的映射关系缓存
     */
    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    //private ComponentContext componentContext;

    /**
     * servlet 框架的初始化servlet方法
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //componentContext = (ComponentContext) config.getServletContext().getAttribute("com.jizhi.geektime.context.ClassicComponentContext");
        //setParentContainer(container);
        initHandleMethods();
    }

    /**
     * 读取所有的 {@link RestController} 的 @Path 注解元信息
     * 利用 ServiceLoader 技术(Java SPI) 查找所有的RestController
     * 需要在 META-INF/services 下新建一个文件，以接口的全限定名为文件名，内容为实现类的全限定名。
     */
    private void initHandleMethods() {
        try {
            // 加载SPI配置的Controller
            for (Controller controller : ServiceLoader.load(Controller.class)) {
                Class<?> controllerClass = controller.getClass();
                // 遍历这些Controller的所有字段，实现controller层的注入
                /*for (Field field : controllerClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Resource.class)) {
                        String name = field.getAnnotation(Resource.class).name();
                        Object fieldVal = getObject(name);
                        field.setAccessible(true);
                        field.set(controller, fieldVal);
                    }
                }*/

                // 遍历这些controller的所有public的方法，初始化方法、url映射
                Path controllerPath = controllerClass.getAnnotation(Path.class);
                String requestPath = controllerPath.value();
                Method[] publicMethods = controllerClass.getMethods();
                for (Method method : publicMethods) {
                    Set<String> supportHttpMethods = findSupportHttpMethods(method);
                    Path methodPath = method.getAnnotation(Path.class);
                    if (methodPath != null) {
                        requestPath += methodPath.value();
                        handleMethodInfoMapping.put(requestPath, new HandlerMethodInfo(requestPath, method, supportHttpMethods));
                    }
                }
                controllersMapping.put(requestPath, controller);
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * 读取 Method 方法上的 @Path 注解信息
     * 将方法上的注解的http方法信息保存到一个 Set 中
     * 如果没有相应的注解，那么默认全部支持。
     *
     * @param method
     * @return
     */
    private Set<String> findSupportHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotation : method.getAnnotations()) {
            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }
        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(Arrays.asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }
        return supportedHttpMethods;
    }

    /**
     * servlet 框架请求进来掉用的方法
     *
     * @param req  请求
     * @param resp 响应
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //req.setAttribute(Config.class.getName(), DefaultConfigProviderResolver.instance().getConfig());

        // 不包含应用上下文的路径
        String requestURI = req.getRequestURI();
        // 应用的路径
        String contextPath = req.getContextPath();
        // 映射的路径
        String requestMappingPath = StringUtils.substringAfter(requestURI,
                StringUtils.replace(contextPath, "//", "/"));

        Controller controller = controllersMapping.get(requestMappingPath);
        if (controller != null) {
            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
            try {
                if (handlerMethodInfo != null) {
                    String httpMethod = req.getMethod();
                    // 不支持的http method
                    if (!handlerMethodInfo.getSupportHttpMethods().contains(httpMethod)) {
                        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                    // 页面跳转的
                    if (controller instanceof PageController) {
                        pageControllerMethodHandle(req, resp, controller);
                        return;
                    }
                    // REST请求的
                    else if (controller instanceof RestController) {
                        restControllerMethodHandle(req, resp, controller, handlerMethodInfo);
                        return;
                    }
                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * restController 类的方法执行
     *
     * @param req               请求
     * @param resp              响应
     * @param controller        restController
     * @param handlerMethodInfo 方法信息
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ServletException
     * @throws IOException
     */
    private void restControllerMethodHandle(HttpServletRequest req, HttpServletResponse resp, Controller controller, HandlerMethodInfo handlerMethodInfo) throws IllegalAccessException, InvocationTargetException, ServletException, IOException {
        RestController restController = RestController.class.cast(controller);
        Class<?>[] parameterTypes = handlerMethodInfo.getHandlerMethod().getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        Annotation[][] parameterAnnotations = handlerMethodInfo.getHandlerMethod().getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            if (parameterType.equals(HttpServletRequest.class)) {
                parameters[i] = req;
            } else if (parameterType.equals(HttpServletResponse.class)) {
                parameters[i] = resp;
            } else if (parameterType.equals(Config.class)) {
                parameters[i] = DefaultConfigProviderResolver.instance().getConfig();
            } else {
                List<? extends Class<? extends Annotation>> collect = Stream.of(parameterAnnotation).map(an -> an.annotationType()).collect(Collectors.toList());
                if (collect.contains(BeanParam.class)) {
                    Object obj = null;
                    String encoding = req.getCharacterEncoding() == null ? "utf-8" : req.getCharacterEncoding();
                    String mediaType = req.getHeader("content-type") == null ? MediaType.APPLICATION_JSON : req.getHeader("content-type");
                    String body = IOUtils.toString(req.getInputStream(), encoding);
                    if (String.class.equals(parameterType)) {
                        obj = body;
                    } else {
                        switch (mediaType) {
                            case MediaType.APPLICATION_JSON:
                                obj = JSONObject.toJavaObject(JSONObject.parseObject(body), parameterType);
                                break;
                        }
                    }
                    parameters[i] = obj;
                } else {
                    Object obj = convertRequestParamsToEntity(req.getParameterMap(), parameterType);
                    parameters[i] = obj;
                }
            }
        }
        Map<String, String> error = validateBean(handlerMethodInfo.getHandlerMethod(), parameters);
        Parameter[] methodParameters = handlerMethodInfo.getHandlerMethod().getParameters();
        for (int i = 0; i < methodParameters.length; i++) {
            if ("error".equals(methodParameters[i].getName()) &&
                    Map.class.isAssignableFrom(methodParameters[i].getType())) {
                parameters[i] = error;
                break;
            }
        }
        Object result = handlerMethodInfo.getHandlerMethod().invoke(restController, parameters);
        if (result instanceof String) {
            String str = String.class.cast(result);
            if (str.endsWith(".jsp") || str.endsWith(".html")) {
                req.getRequestDispatcher(str).forward(req, resp);
                return;
            } else {
                resp.setHeader("Content-type", "text/html;charset=UTF-8");
                resp.getWriter().write(str);
                resp.flushBuffer();
                return;
            }
        } else {
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            resp.getWriter().write(JSONObject.toJSONString(result));
            resp.flushBuffer();
            return;
        }
    }

    /**
     * 参数校验
     *
     * @param handlerMethod
     * @param parameters
     */
    private Map<String, String> validateBean(Method handlerMethod, Object[] parameters) {
        Map<String, String> error = new HashMap<>();
        Annotation[][] parameterAnnotations = handlerMethod.getParameterAnnotations();
        ValidatorDelegate validatorDelegate = null;
        //ValidatorDelegate validatorDelegate = (ValidatorDelegate) getObject("bean/ValidatorDelegate");
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            List<? extends Class<? extends Annotation>> an = Stream.of(annotations).map(a -> a.getClass()).collect(Collectors.toList());
            boolean isValidator = false;
            for (Class<? extends Annotation> aClass : an) {
                if (Valid.class.isAssignableFrom(aClass)) {
                    isValidator = true;
                    break;
                }
            }
            if (isValidator) {
                error.putAll(validatorDelegate.validate(parameters[i]));
            }
        }
        return error;
    }

    /**
     * 页面跳转请求，PageController的方法的执行
     *
     * @param req        请求
     * @param resp       响应
     * @param controller pageController
     * @throws Throwable
     */
    private void pageControllerMethodHandle(HttpServletRequest req, HttpServletResponse resp, Controller controller) throws Throwable {
        PageController pageController = PageController.class.cast(controller);
        String viewPath = pageController.execute(req, resp);

        if (!viewPath.startsWith("/")) {
            viewPath = "/" + viewPath;
        }
        // 调用forward转发
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(viewPath);
        requestDispatcher.forward(req, resp);
    }

    /**
     * 将 HttpServletRequest 请求中的参数，转换为对应的实体类
     *
     * @param parameterMap  请求参数Map
     * @param parameterType 要转换的对象的类型
     * @return 转换后的对象
     */
    private Object convertRequestParamsToEntity(Map<String, String[]> parameterMap, Class<?> parameterType) {
        try {
            if (parameterType.isArray() || Collection.class.isAssignableFrom(parameterType) ||
                    Map.class.isAssignableFrom(parameterType)) {
                // TODO
                return null;
            } else {
                Object obj = parameterType.getConstructor().newInstance();
                BeanInfo beanInfo = Introspector.getBeanInfo(parameterType, Object.class);
                for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                    String fieldName = property.getName();
                    // TODO
                    Object[] values = parameterMap.get(fieldName);
                    if (values != null && values.length > 0) {
                        Object val = values[0];
                        Method writeMethod = property.getWriteMethod();
                        writeMethod.invoke(obj, val);
                    }
                }
                return obj;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }



    //@Override
    //public Object getObject(String name) {
   //     return componentContext.getComponent(name);
    //}

  //  private Container parentContainer;

//    @Override
//    public Container getParentContainer() {
//        return this.parentContainer;
//    }
//
//    @Override
//    public void setParentContainer(Container container) {
//        this.parentContainer = container;
//    }
}
