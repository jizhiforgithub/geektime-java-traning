package com.jizhi.geektime.projects.user.service;

import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.naming.NamingException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2021/3/31
 * jizhi7
 **/
public class JmsService {

    @Resource(name = "jms/producer")
    private MessageProducer producer;

    @Resource(name = "jms/consumer")
    private MessageConsumer consumer;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public void sendMsg(String msg) throws JMSException {
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText("autowired produce test" + atomicInteger.getAndIncrement());
        System.out.println("autowired producer msg : " + message.getText());
        producer.send(message);
    }

    public void receive() throws JMSException, NamingException {
        consumer.setMessageListener(message -> {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
            try {
                System.out.println("autowired consumer revive msg : " + msg.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }

}
