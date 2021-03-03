package com.jizhi.geektime.projects.user.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 字符编码过滤器
 *
 * @author jizhi7
 * @since 1.0
 **/
public class CharsetEncodingFilter implements Filter {

    private String encoding = null;
    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = HttpServletRequest.class.cast(request);
            req.setCharacterEncoding(encoding);
            HttpServletResponse res = HttpServletResponse.class.cast(response);
            res.setCharacterEncoding(encoding);
            servletContext.log("当前编码已经设置成为：" + encoding);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
