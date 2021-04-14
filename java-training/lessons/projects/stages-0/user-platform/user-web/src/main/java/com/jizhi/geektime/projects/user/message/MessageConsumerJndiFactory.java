package com.jizhi.geektime.projects.user.message;

import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *  jndi jms连接工厂类
 * @author jizhi7
 * @since 1.0
 **/
public class MessageConsumerJndiFactory implements ObjectFactory {

   private JmsConfig jmsConfig;


    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {

        // 获取Resource标签的属性
        if (obj instanceof Reference) {
            Reference reference = (Reference) obj;
            // 所有的属性
            Enumeration<RefAddr> iter = reference.getAll();
            RefAddr addr = iter.nextElement();
            String attrName = addr.getType();
            String content = (String) addr.getContent();

        }

        jmsConfig = (JmsConfig) nameCtx.lookup("config");
        ConnectionFactory factory = (ConnectionFactory) nameCtx.lookup(jmsConfig.getConnectionFactoryJndiName());
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(jmsConfig.getQueueName());
        MessageConsumer consumer = session.createConsumer(queue);
        //Message receive = consumer.receive();
        System.out.println();
        return consumer;
    }

}
