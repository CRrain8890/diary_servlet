package com.wishwzp.web;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wishwzp.dao.UserDao;
import com.wishwzp.model.User;
import com.wishwzp.util.DbUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final Logger logger = LogManager.getLogger(LoginServlet.class);

	private static final long serialVersionUID = 1L;

	// 初始化数据库工具类和用户 DAO 类
	DbUtil dbutil = new DbUtil();
	UserDao userDao = new UserDao();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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
		// 设置请求字符编码为 UTF-8
		request.setCharacterEncoding("utf-8");

		// 获取请求参数
		String username = request.getParameter("userName");
		String password = request.getParameter("password");
		String remember = request.getParameter("remember");
		logger.debug("username:" + username);
		logger.debug("password:" + password);
		logger.debug("remember:" + remember);

		// 获取 HttpSession 对象
		HttpSession session = request.getSession();
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbutil.getCon();
			User user = new User(username, password);
			// 调用 DAO 的登录方法
			User currentUser = userDao.login(con, user);

			// 如果用户不存在，返回登录页面并显示错误信息
			if (currentUser == null) {
				request.setAttribute("user", user);
				request.setAttribute("error", "用户名或密码错误！");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			} else {
				// 如果选择了记住我功能，则调用 rememberMe 方法
				if ("remember-me".equals(remember)) {
					rememberMe(username, password, response);
				}
				// 将当前用户对象保存到 Session 中
				session.setAttribute("currentUser", currentUser);
				// 请求转发到主页面
				request.getRequestDispatcher("main").forward(request, response);
			}
		} catch (Exception e) {
			// 打印异常堆栈信息
			e.printStackTrace();
		}
	}

	/**
	 * 记住密码功能
	 * @param username 用户名
	 * @param password 密码
	 * @param response HTTP 响应
	 */
	private void rememberMe(String username, String password, HttpServletResponse response) {
		// 创建包含用户名和密码的 Cookie 对象
		Cookie user = new Cookie("user", username + "-" + password);
		// 设置 Cookie 的最大生存时间为一周
		user.setMaxAge(1 * 60 * 60 * 24 * 7);
		// 将 Cookie 添加到响应中
		response.addCookie(user);
	}

	/**
	 * 类注释：
	 *
	 * 说明 LoginServlet 类继承自 HttpServlet，用于处理登录请求。
	 * 构造方法注释：
	 *
	 * 解释构造方法的用途，这里调用父类的构造方法。
	 * 方法注释：
	 *
	 * doGet(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 处理 GET 请求，将其转发给 doPost 方法处理。
	 * doPost(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 处理 POST 请求，设置请求字符编码为 UTF-8，获取请求参数 username、password 和 remember，获取 HttpSession 对象，并通过 DAO 调用登录方法验证用户信息。
	 * 如果用户信息验证失败，返回登录页面并显示错误信息；如果验证成功且用户选择了记住我功能，调用 rememberMe 方法保存用户信息到 Cookie 中，并将当前用户对象保存到 HttpSession 中，最后请求转发到主页面。
	 * rememberMe(String username, String password, HttpServletResponse response) 方法：
	 * 实现记住密码功能，创建包含用户名和密码的 Cookie 对象，设置其生存时间为一周，并将 Cookie 添加到响应中。
	 */
}
