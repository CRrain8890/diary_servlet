package com.wishwzp.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter implements Filter {
	private static final Logger logger = LogManager.getLogger(LoginFilter.class);

	// 过滤器销毁方法，在过滤器被卸载之前调用
	public void destroy() {
		// TODO Auto-generated method stub
	}

	// 过滤器的核心方法，用于拦截和处理请求和响应
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
						 FilterChain filterChain) throws IOException, ServletException {

		// 将通用的 ServletRequest 和 ServletResponse 转换为 HTTP 请求和响应
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		// 获取当前会话
		HttpSession session = request.getSession();

		// 检查会话中是否有名为 "currentUser" 的属性，用于判断用户是否已登录
		Object object = session.getAttribute("currentUser");

		// 获取请求的路径
		String path = request.getServletPath();
		logger.info("当前请求url:"+path);
		// 如果用户未登录并且请求路径不包含 "login"、"bootstrap" 和 "images"，则重定向到登录页面
		if (object == null && path.indexOf("login") < 0 && path.indexOf("bootstrap") < 0 && path.indexOf("images") < 0) {
			response.sendRedirect("login.jsp");
		} else {
			// 否则继续处理请求
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	// 过滤器初始化方法，在过滤器实例化时调用
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * 注释说明
	 * 类注释：
	 *
	 * 说明 LoginFilter 是一个实现了 Filter 接口的过滤器，用于处理登录验证。
	 * 方法注释：
	 *
	 * destroy() 方法：在过滤器被销毁之前调用，用于清理资源。
	 * doFilter() 方法：过滤器的核心方法，用于拦截和处理请求和响应。
	 * init(FilterConfig arg0) 方法：在过滤器实例化时调用，用于初始化过滤器。
	 * 代码注释：
	 *
	 * 将通用的 ServletRequest 和 ServletResponse 转换为具体的 HttpServletRequest 和 HttpServletResponse。
	 * 获取当前会话，检查会话中是否有名为 currentUser 的属性，以判断用户是否已登录。
	 * 获取请求的路径，检查路径是否包含 login、bootstrap 和 images，这些路径不需要登录验证。
	 * 如果用户未登录且请求路径需要登录验证，则重定向到登录页面。
	 * 否则继续处理请求，调用下一个过滤器或目标资源。
	 *
	 */
}
