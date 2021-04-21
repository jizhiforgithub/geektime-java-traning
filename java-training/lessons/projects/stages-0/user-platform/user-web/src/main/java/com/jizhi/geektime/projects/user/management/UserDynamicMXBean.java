package com.jizhi.geektime.projects.user.management;

import javax.management.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态bean
 **/
public class UserDynamicMXBean implements DynamicMBean {

    // 五个属性
    // id、name、password、email、phoneNumber
    private Map<String, Object> attributes = new HashMap<>();


    /**
     * 获取属性
     * @param attribute
     * @return
     * @throws AttributeNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     */
    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (!attributes.containsKey(attribute)) {
            throw new AttributeNotFoundException("...");
        }
        return attributes.get(attribute);
    }

    /**
     * 设置属性
     * @param attribute
     * @throws AttributeNotFoundException
     * @throws InvalidAttributeValueException
     * @throws MBeanException
     * @throws ReflectionException
     */
    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        attributes.put(attribute.getName(), attribute.getValue());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String attribute : attributes) {
            try {
                Object attributeValue = getAttribute(attribute);
                attributeList.add(new Attribute(attribute, attributeValue));
            } catch (AttributeNotFoundException | MBeanException | ReflectionException e) {
            }
        }
        return attributeList;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        // 方法被调用时
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return null;
    }
}
