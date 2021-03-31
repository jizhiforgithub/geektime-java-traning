package com.jizhi.geektime.projects.user.message;

import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 *  jndi jms连接工厂类
 * @author jizhi7
 * @since 1.0
 **/
public class MessageProducerJndiFactory implements ObjectFactory {

   private JmsConfig jmsConfig;


    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        jmsConfig = (JmsConfig) nameCtx.lookup("config");
        ConnectionFactory factory = (ConnectionFactory) nameCtx.lookup(jmsConfig.getConnectionFactoryJndiName());
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(jmsConfig.getQueueName());
        MessageProducer producer = session.createProducer(queue);
        return producer;
    }

}
