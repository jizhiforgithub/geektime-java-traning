package com.jizhi.geektime.projects.user.web.controller;

import com.jizhi.geektime.projects.user.domain.User;
import com.jizhi.geektime.projects.user.service.IUserService;
import com.jizhi.geektime.web.mvc.controller.RestController;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * 登录Controller
 *
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
    @POST
    @GET
    public Collection<User> all() {
        //Collection<User> users = userService.queryAll();
        return null;
    }

    @Path("/register")
    @POST
    public String register(HttpServletRequest request, @Valid User user, Map<String, String> error) {
        if (error != null && error.size() > 0) {
            request.setAttribute("error", error);
            return "/register-form.jsp";
        }
        if (userService.register(user)) {
            return "/register-success.jsp";
        }
        return "/register-form.jsp";
    }

    @Path("/gitee/login")
    @GET
    @POST
    public void giteeLogin(HttpServletRequest request, HttpServletResponse response) {
        String url = "https://gitee.com/oauth/authorize?client_id=63ebd19c2e5b5d1e771d93c4cd8e9403e3da8c8f540a517726104fe836d982b7&redirect_uri=http://localhost:8080/user-web/user/gitee/loginback&response_type=code";
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Path("/gitee/loginback")
    @GET
    public String giteeLoginBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String addr = "https://gitee.com/oauth/token?grant_type=authorization_code&code=" + code + "&client_id=63ebd19c2e5b5d1e771d93c4cd8e9403e3da8c8f540a517726104fe836d982b7&redirect_uri=http://localhost:8080/user-web/gitee.jsp&client_secret=55e56b2ad06420637a48b9f846819b498ecbd8ed022c9a19c50c7f2d48304ea1";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(addr).openConnection();
            connection.setRequestMethod("POST");

            // 设置请求编码
            connection.addRequestProperty("encoding", "UTF-8");
            // 设置允许输入
            connection.setDoInput(true);
            // 设置允许输出
            connection.setDoOutput(true);
            InputStream inputStream = connection.getInputStream();
            String userinfo = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            System.out.println(userinfo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/user-web/index.jsp");
        return null;
    }

    @Path("/test")
    @GET
    @POST
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
