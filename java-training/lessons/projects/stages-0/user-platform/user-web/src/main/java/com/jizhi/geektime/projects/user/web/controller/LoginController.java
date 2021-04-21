package com.jizhi.geektime.projects.user.web.controller;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.projects.user.service.IUserService;
import com.jizhi.geektime.web.mvc.controller.RestController;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.Map;

/**
 * 登录Controller
 * @author jizhi7
 * @since 1.0
 **/
@Path("/user")
public class LoginController implements RestController {

    @Resource(name = "bean/UserService")
    private IUserService userService;

    @Path("/login")
    @POST
    public Collection<User> login() {
        //Collection<User> users = userService.queryAll();
        return null;
    }

    @Path("/all")
    @POST @GET
    public Collection<User> all() {
        //Collection<User> users = userService.queryAll();
        return null;
    }

    @Path("/register")
    @POST
    public String register(HttpServletRequest request, @Valid User user, Map<String, String> error) {
        if(error != null && error.size() > 0) {
            request.setAttribute("error", error);
            return "/register-form.jsp";
        }
        if (userService.register(user)) {
            return "/register-success.jsp";
        }
        return "/register-form.jsp";
    }

    @Path("/test")
    @GET @POST
    public String test() {
        //userService.test();
        return "ok";
    }

    @Path("/hello")
    @GET
    public Object hello(HttpServletRequest request, Config config) {
        request.getServletContext().getAttribute(Config.class.getName());
        return ConfigProviderResolver.instance().getConfig().getValue("hello", String.class);
    }

}
