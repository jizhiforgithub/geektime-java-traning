package com.jizhi.geektime.web.mvc;

import com.alibaba.fastjson.JSONObject;
import com.jizhi.geektime.web.mvc.controller.Controller;
import com.jizhi.geektime.web.mvc.controller.PageController;
import com.jizhi.geektime.web.mvc.controller.RestController;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
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

    /**
     * servlet 框架的初始化servlet方法
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        initHandleMethods();
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
                    if (controller instanceof PageController) {
                        PageController pageController = PageController.class.cast(controller);
                        String viewPath = pageController.execute(req, resp);

                        if (!viewPath.startsWith("/")) {
                            viewPath = "/" + viewPath;
                        }
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher(viewPath);
                        requestDispatcher.forward(req, resp);
                        return;
                    } else if (controller instanceof RestController) {
                        RestController restController = RestController.class.cast(controller);
                        Class<?>[] parameterTypes = handlerMethodInfo.getHandlerMethod().getParameterTypes();
                        Object[] parameters = new Object[parameterTypes.length];
                        for (int i = 0; i < parameterTypes.length; i++) {
                            Class<?> parameterType = parameterTypes[i];
                            if (parameterType.equals(HttpServletRequest.class)) {
                                parameters[i] = req;
                            } else if (parameterType.equals(HttpServletResponse.class)) {
                                parameters[i] = resp;
                            } else {
                                Object obj = convertRequestParamsToEntity(req.getParameterMap(), parameterType);
                                parameters[i] = obj;
                            }
                        }
                        Object result = handlerMethodInfo.getHandlerMethod().invoke(restController, parameters);
                        if (result instanceof String) {
                            String str = String.class.cast(result);
                            if(str.endsWith(".jsp") || str.endsWith(".html")) {
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
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Object convertRequestParamsToEntity(Map<String, String[]> parameterMap, Class<?> parameterType) {
        try {
            Object obj = parameterType.getConstructor().newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(parameterType, Object.class);
            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                String fieldName = property.getName();
                // TODO
                Object[] values = parameterMap.get(fieldName);
                if(values != null && values.length > 0) {
                    Object val = values[0];
                    Method writeMethod = property.getWriteMethod();
                    writeMethod.invoke(obj, val);
                }
            }
            return obj;
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

    /**
     * 读取所有的 {@link RestController} 的 @Path 注解元信息
     * 利用 ServiceLoader 技术(Java SPI) 查找所有的RestController
     * 需要在 META-INF/services 下新建一个文件，以接口的全限定名为文件名，内容为实现类的全限定名。
     */
    private void initHandleMethods() {
        for (Controller controller : ServiceLoader.load(Controller.class)) {
            Class<?> controllerClass = controller.getClass();
            Path controllerPath = controllerClass.getAnnotation(Path.class);
            String controllerRequestPath = controllerPath.value();
            Method[] publicMethods = controllerClass.getMethods();
            for (Method method : publicMethods) {
                Set<String> supportHttpMethods = findSupportHttpMethods(method);
                Path methodPath = method.getAnnotation(Path.class);
                String requestPath = controllerRequestPath;

                if (methodPath != null) {
                    requestPath += methodPath.value();

                    handleMethodInfoMapping.put(requestPath, new HandlerMethodInfo(requestPath, method, supportHttpMethods));
                    controllersMapping.put(requestPath, controller);
                }
            }
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

}
