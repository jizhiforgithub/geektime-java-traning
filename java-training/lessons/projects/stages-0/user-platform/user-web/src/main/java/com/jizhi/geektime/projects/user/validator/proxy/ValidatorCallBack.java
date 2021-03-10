package com.jizhi.geektime.projects.user.validator.proxy;


import com.jizhi.geektime.projects.user.ioc.IoCContainer;
import com.jizhi.geektime.projects.user.proxy.BeforeInvoker;
import com.jizhi.geektime.web.validator.ValidatorDelegate;

import javax.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 校验代理类的回调方法，在这里实现 校验
 *
 * @author jizhi7
 * @since 1.0
 **/
public class ValidatorCallBack implements BeforeInvoker {

    public static final String VALIDATOR_DELEGATE_BEAN_NAME = "bean/ValidatorDelegate";
    private static Logger logger = Logger.getLogger(ValidatorCallBack.class.getName());

    private ValidatorDelegate validatorDelegate;

    public ValidatorCallBack() {
        validatorDelegate = (ValidatorDelegate) IoCContainer.getInstance().getObject(VALIDATOR_DELEGATE_BEAN_NAME);
    }

    /**
     * @param method
     * @param methodArgs
     * @throws SQLException
     */
    @Override
    public void before(Object proxyObj, Object targetObj, Method method, Object[] methodArgs) {
        boolean needValidate = false;
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(Valid.class)) {
                needValidate = true;
                break;
            }
        }
        if (needValidate) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(Valid.class)) {
                    Map<String, String> result = doValidator(methodArgs[i]);
                    String parameterName = parameter.getName();
                    int index = getValidatorResultArgIndex(parameterName, parameters);
                    if (index != -1) {
                        methodArgs[index] = result;
                    }
                }
            }
        }
    }

    /**
     * 获取对应的校验错误结果的参数下标
     *
     * @param parameterName
     * @return
     */
    private int getValidatorResultArgIndex(String parameterName, Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getName().equals(parameterName + "Errors") &&
                    Map.class.isAssignableFrom(parameter.getType())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 校验逻辑
     *
     * @param arg
     * @return
     */
    private Map<String, String> doValidator(Object arg) {
        if (validatorDelegate != null) {
            return validatorDelegate.validate(arg);
        }
        return null;
    }

}
