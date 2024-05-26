package com.wishwzp.web;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wishwzp.dao.DiaryDao;
import com.wishwzp.model.Diary;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.StringUtil;

/**
 * Servlet implementation class DiaryServlet
 */
public class DiaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 初始化数据库工具类和 DAO 类
	DbUtil dbUtil = new DbUtil();
	DiaryDao diaryDao = new DiaryDao();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DiaryServlet() {
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

		// 获取请求参数 action，用于区分不同的操作
		String action = request.getParameter("action");

		// 根据 action 参数的值调用不同的方法处理请求
		if ("show".equals(action)) {
			diaryShow(request, response);
		} else if ("preSave".equals(action)) {
			diaryPreSave(request, response);
		} else if ("save".equals(action)) {
			diarySave(request, response);
		} else if ("delete".equals(action)) {
			diaryDelete(request, response);
		}
	}

	/**
	 * 显示日记内容
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryShow(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String diaryId = request.getParameter("diaryId");
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbUtil.getCon();
			// 根据日记 ID 获取日记对象
			Diary diary = diaryDao.diaryShow(con, diaryId);
			// 将日记对象添加到请求属性中
			request.setAttribute("diary", diary);
			// 设置主页面为日记显示页面
			request.setAttribute("mainPage", "diary/diaryShow.jsp");
			// 请求转发到主模板页面
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭数据库连接
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 预保存日记内容
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryPreSave(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String diaryId = request.getParameter("diaryId");
		Connection con = null;
		try {
			if (StringUtil.isNotEmpty(diaryId)) {
				// 获取数据库连接
				con = dbUtil.getCon();
				// 根据日记 ID 获取日记对象
				Diary diary = diaryDao.diaryShow(con, diaryId);
				// 将日记对象添加到请求属性中
				request.setAttribute("diary", diary);
			}
			// 设置主页面为日记保存页面
			request.setAttribute("mainPage", "diary/diarySave.jsp");
			// 请求转发到主模板页面
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存日记内容
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diarySave(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取请求参数
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String typeId = request.getParameter("typeId");
		String diaryId = request.getParameter("diaryId");

		// 创建日记对象
		Diary diary = new Diary(title, content, Integer.parseInt(typeId));
		if (StringUtil.isNotEmpty(diaryId)) {
			diary.setDiaryId(Integer.parseInt(diaryId));
		}
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbUtil.getCon();
			int saveNums;

			if (StringUtil.isNotEmpty(diaryId)) {
				// 更新日记
				saveNums = diaryDao.diaryUpdate(con, diary);
			} else {
				// 添加日记
				saveNums = diaryDao.diaryAdd(con, diary);
			}

			if (saveNums > 0) {
				// 保存成功，转发到主页面
				request.getRequestDispatcher("main?all=true").forward(request, response);
			} else {
				// 保存失败，返回保存页面并显示错误信息
				request.setAttribute("diary", diary);
				request.setAttribute("error", "保存失败");
				request.setAttribute("mainPage", "diary/diarySave.jsp");
				request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭数据库连接
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除日记内容
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String diaryId = request.getParameter("diaryId");
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbUtil.getCon();
			// 删除指定 ID 的日记
			diaryDao.diaryDelete(con, diaryId);
			// 删除成功，转发到主页面
			request.getRequestDispatcher("main?all=true").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭数据库连接
				dbUtil.closeCon(con);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 类注释：
	 *
	 * 说明 DiaryServlet 类继承自 HttpServlet，用于处理日记相关的请求操作，包括显示、预保存、保存和删除日记。
	 * 构造方法注释：
	 *
	 * 解释构造方法的用途，这里调用父类的构造方法。
	 * 方法注释：
	 *
	 * doGet(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 处理 GET 请求，将其转发给 doPost 方法处理。
	 * doPost(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 处理 POST 请求，设置请求字符编码为 UTF-8，获取请求参数 action，并根据其值调用不同的私有方法处理具体操作。
	 * diaryShow(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 显示日记内容，根据日记 ID 获取日记对象，将其添加到请求属性中，转发到显示页面。
	 * diaryPreSave(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 预保存日记内容，如果日记 ID 不为空，则获取日记对象，将其添加到请求属性中，转发到保存页面。
	 * diarySave(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 保存日记内容，根据请求参数创建日记对象，保存到数据库，如果保存成功则转发到主页面，否则返回保存页面并显示错误信息。
	 * diaryDelete(HttpServletRequest request, HttpServletResponse response) 方法：
	 * 删除日记内容，根据日记 ID 删除日记，删除成功后转发到主页面。
	 */
}
