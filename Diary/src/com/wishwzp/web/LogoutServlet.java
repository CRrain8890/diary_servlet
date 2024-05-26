package com.wishwzp.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造方法
	 */
	public LogoutServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 处理 GET 请求
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 调用 doPost 方法处理请求
		this.doPost(request, response);
	}

	/**
	 * 处理 POST 请求
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取当前会话
		HttpSession session = request.getSession();

		// 移除会话中的 "currentUser" 属性，表示用户已注销
		session.removeAttribute("currentUser");

		// 转发请求到登录页面
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	/**
	 * 类注释：
	 *
	 * 说明 LogoutServlet 类继承自 HttpServlet，用于处理用户注销请求。
	 * 构造方法注释：
	 *
	 * 解释构造方法的用途，这里调用父类的构造方法。
	 * 方法注释：
	 *
	 * doGet(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 处理 GET 请求，将其转发给 doPost 方法处理。
	 * doPost(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 处理 POST 请求，从会话中移除 currentUser 属性，表示用户已注销。
	 * 使用 RequestDispatcher 将请求转发到 login.jsp 页面。
	 * 代码注释：
	 *
	 * 在 doPost 方法中，获取当前会话，并从会话中移除 currentUser 属性。
	 * 使用 RequestDispatcher 将请求转发到 login.jsp 页面，让用户重新登录。
	 */
}
