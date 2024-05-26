package com.wishwzp.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wishwzp.dao.DiaryDao;
import com.wishwzp.dao.DiaryTypeDao;
import com.wishwzp.model.DiaryType;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.StringUtil;

/**
 * Servlet implementation class DiaryTypeServlet
 */
public class DiaryTypeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 初始化数据库工具类和 DAO 类
	DbUtil dbUtil = new DbUtil();
	DiaryTypeDao diaryTypeDao = new DiaryTypeDao();
	DiaryDao diaryDao = new DiaryDao();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DiaryTypeServlet() {
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
		if ("list".equals(action)) {
			diaryTypeList(request, response);
		} else if ("preSave".equals(action)) {
			diaryTypePreSave(request, response);
		} else if ("save".equals(action)) {
			diaryTypeSave(request, response);
		} else if ("delete".equals(action)) {
			diaryTypeDelete(request, response);
		}
	}

	/**
	 * 显示日记类别列表
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryTypeList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbUtil.getCon();
			// 获取日记类别列表
			List<DiaryType> diaryTypeList = diaryTypeDao.diaryTypeList(con);
			// 将日记类别列表添加到请求属性中
			request.setAttribute("diaryTypeList", diaryTypeList);
			// 设置主页面为日记类别列表页面
			request.setAttribute("mainPage", "diaryType/diaryTypeList.jsp");
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
	 * 预保存日记类别
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryTypePreSave(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 用于识别是添加类别还是修改类别
		String diaryTypeId = request.getParameter("diaryTypeId");
		if (StringUtil.isNotEmpty(diaryTypeId)) {
			Connection con = null;
			try {
				// 获取数据库连接
				con = dbUtil.getCon();
				// 根据日记类别 ID 获取日记类别对象
				DiaryType diaryType = diaryTypeDao.diaryTypeShow(con, diaryTypeId);
				// 将日记类别对象添加到请求属性中
				request.setAttribute("diaryType", diaryType);
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
		// 设置主页面为日记类别保存页面
		request.setAttribute("mainPage", "diaryType/diaryTypeSave.jsp");
		// 请求转发到主模板页面
		request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
	}

	/**
	 * 保存日记类别
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryTypeSave(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获取请求参数
		String diaryTypeId = request.getParameter("diaryTypeId");
		String typeName = request.getParameter("typeName");

		// 创建日记类别对象
		DiaryType diaryType = new DiaryType(typeName);

		if (StringUtil.isNotEmpty(diaryTypeId)) {
			diaryType.setDiaryTypeId(Integer.parseInt(diaryTypeId));
		}
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbUtil.getCon();
			int saveNum = 0;
			// 用于判断是添加还是修改
			if (StringUtil.isNotEmpty(diaryTypeId)) {
				// 更新日记类别
				saveNum = diaryTypeDao.diaryTypeUpdate(con, diaryType);
			} else {
				// 添加日记类别
				saveNum = diaryTypeDao.diaryTypeAdd(con, diaryType);
			}

			if (saveNum > 0) {
				// 保存成功，转发到日记类别列表页面
				request.getRequestDispatcher("diaryType?action=list").forward(request, response);
			} else {
				// 保存失败，返回保存页面并显示错误信息
				request.setAttribute("diaryType", diaryType);
				request.setAttribute("error", "保存失败！");
				request.setAttribute("mainPage", "diaryType/diaryTypeSave.jsp");
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
	 * 删除日记类别
	 * @param request  HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void diaryTypeDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String diaryTypeId = request.getParameter("diaryTypeId");
		Connection con = null;
		try {
			// 获取数据库连接
			con = dbUtil.getCon();
			// 判断该日记类别下是否有日记
			if (diaryDao.existDiaryWithTypeId(con, diaryTypeId)) {
				// 如果该类别下有日记，则不能删除，显示错误信息
				request.setAttribute("error", "日志类别下有日志，不能删除该类别！");
			} else {
				// 删除指定 ID 的日记类别
				diaryTypeDao.diaryTypeDelete(con, diaryTypeId);
			}
			// 转发到日记类别列表页面
			request.getRequestDispatcher("diaryType?action=list").forward(request, response);
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
}
