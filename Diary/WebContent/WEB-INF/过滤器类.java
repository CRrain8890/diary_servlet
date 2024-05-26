package com.wishwzp.filter;

import javax.servlet.*;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化代码
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 请求处理代码
        System.out.println("LoginFilter is invoked");

        // 继续请求链，调用下一个过滤器或目标资源
        chain.doFilter(request, response);

        // 响应处理代码
        System.out.println("LoginFilter response processing");
    }

    @Override
    public void destroy() {
        // 销毁代码
    }
}
