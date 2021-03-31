package com.jizhi.geektime.projects.user.web.controller;

import com.jizhi.geektime.projects.user.service.JmsService;
import com.jizhi.geektime.web.mvc.controller.RestController;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * 2021/3/31
 * jizhi7
 **/
@Path("/jms")
public class JmsController implements RestController {

    @Resource(name = "bean/jmsService")
    private JmsService jmsService;

    @Path("/send")
    @GET
    public String send() throws JMSException {
        jmsService.sendMsg("hello controller");
        return "ok";
    }

    @Path("/receive")
    @GET
    public String receive() throws Exception {
        jmsService.receive();
        return "ok";
    }

}
