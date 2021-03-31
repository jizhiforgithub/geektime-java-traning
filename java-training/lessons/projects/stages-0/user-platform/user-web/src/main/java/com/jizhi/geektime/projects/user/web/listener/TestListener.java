package com.jizhi.geektime.projects.user.web.listener;

import com.jizhi.geektime.jmx.mbean.MBeanLoadAgent;
import com.jizhi.geektime.projects.user.ioc.IoCContainer;
import org.eclipse.microprofile.config.ConfigValue;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 测试监听器，
 *
 * @author jizhi7
 * @since 1.0
 **/
@WebListener
@Deprecated
public class TestListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(getClass().getName());
    /**
     * jndi根路径
     */
    private static final String JNDI_ROOT_NAME = "java:comp/env";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //configurationTest(sce);
//        mbeanTest(sce);
        try {
            InitialContext context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(JNDI_ROOT_NAME + "/" + "jms/activemq-factory");
            testJms(factory);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void mbeanTest(ServletContextEvent sce) {
//        IoCContainer container = (IoCContainer) sce.getServletContext().getAttribute(IoCContainer.IoC_NAME);
//        MBeanLoadAgent agent = (MBeanLoadAgent) container.getObject("bean/MBeanLoadAgent");
//        agent.loadMBean();
    }

    private void configurationTest(ServletContextEvent sce) {
//        IoCContainer container = (IoCContainer) sce.getServletContext().getAttribute(IoCContainer.IoC_NAME);
//        JavaEEConfigProviderResolver provider = (JavaEEConfigProviderResolver) container.getObject("bean/JavaEEConfigProviderResolver");
//        String propertyName = "sun.arch.data.model";
//        ConfigValue configValue = provider.getConfig().getConfigValue(propertyName);
//        Integer value = provider.getConfig().getValue(propertyName, Integer.class);
//        logger.log(Level.WARNING, "参数输出：");
//        logger.log(Level.WARNING, "[name : " + propertyName + ",value : " + value + ",type : " + value.getClass() + ",source : " + configValue.getSourceName() + "]");
//        logger.log(Level.WARNING, "参数结束。");
    }



    private void testJms(ConnectionFactory connectionFactory) {
        try{
            // 创建 jms 连接
            Connection connection = connectionFactory.createConnection();
            // 连接
            connection.start();

            // 创建 jms 会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 使用会话创建队列
            Queue queue = session.createQueue("Test");

            // 使用会话创建生产者
            MessageProducer producer = session.createProducer(queue);
            // 设置生产的消息不持久化
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // 创建消息
            String text = "hello world! from : " + Thread.currentThread().getName();
            TextMessage textMessage = session.createTextMessage(text);


            producer.send(textMessage);
            System.out.println("send msg : " + text);

            // 使用会话创建消费者
            MessageConsumer consumer = session.createConsumer(queue);

            // 消费者监听，监听处理数据的时候，会话和连接不能断开
            consumer.setMessageListener((message) -> {
                try {
                    System.out.println("receive msg : " + ((TextMessage)message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });

           // TextMessage message = (TextMessage) consumer.receive(1000);
           // System.out.println("receive msg : " + message.getText());

           // session.close();
          //  connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
