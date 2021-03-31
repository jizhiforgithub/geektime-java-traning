package com.jizhi.geektime.projects.user.message;

/**
 * jms 的jndi配置
 * @author jizhi7
 * @since 1.0
 **/
public class JmsConfig {

    private String connectionFactoryJndiName;
    private String queueName;

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getConnectionFactoryJndiName() {
        return connectionFactoryJndiName;
    }

    public void setConnectionFactoryJndiName(String connectionFactoryJndiName) {
        this.connectionFactoryJndiName = connectionFactoryJndiName;
    }
}
