package com.jizhi.geektime.projects.user.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.jizhi.geektime.web.mvc.controller.RestController;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.Map;

/**
 * Rest测试接口
 *
 * @author jizhi7
 * @since 1.0
 **/
@Path("/rest")
public class RestClientTestController implements RestController {

    @Path("/get")
    @GET
    public String get() {
        return "hello get";
    }

    @Path("/post")
    @POST
    public String post(@BeanParam String params) {
        return "ok post, params : " + params;
    }

}
