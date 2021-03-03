package com.jizhi.geektime.projects.user.web.controller;

import com.jizhi.geektime.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * hello word
 *
 * @author jizhi7
 * @since 1.0
 **/

@Path("/hello")
public class HelloWordController implements PageController {

    @GET
    @POST
    @Path("/word")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "index.jsp";
    }

}
