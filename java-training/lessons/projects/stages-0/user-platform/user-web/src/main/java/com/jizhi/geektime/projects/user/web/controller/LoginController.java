package com.jizhi.geektime.projects.user.web.controller;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.projects.user.service.IUserService;
import com.jizhi.geektime.projects.user.service.impl.UserServiceImpl;
import com.jizhi.geektime.web.mvc.controller.RestController;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collection;

/**
 * 2021/3/2
 * jizhi7
 **/
@Path("/user")
public class LoginController implements RestController {

    private final IUserService userService;

    public LoginController() {
        this.userService = new UserServiceImpl();
    }

    @Path("/login")
    @POST
    public Collection<User> login() {
        Collection<User> users = userService.queryAll();
        return users;
    }

    @Path("/register")
    @POST
    public String register(User user) {
        if (userService.register(user)) {
            return "/register-success.jsp";
        }
        return "/register-form.jsp";
    }

}
