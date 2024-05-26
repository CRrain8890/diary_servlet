package com.wishwzp.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wishwzp.dao.DiaryDao;
import com.wishwzp.dao.DiaryTypeDao;
import com.wishwzp.model.Diary;
import com.wishwzp.model.DiaryType;
import com.wishwzp.model.PageBean;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.PaginationUtils;
import com.wishwzp.util.PropertiesUtil;
import com.wishwzp.util.StringUtil;

/**
 * Servlet implementation class MainServlet
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 初始化数据库工具类和 DAO 类
	DbUtil dbUtil = new DbUtil();
	DiaryDao diaryDao = new DiaryDao();
	DiaryTypeDao diaryTypeDao = new DiaryTypeDao();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 调用 doPost 方法处理 GET 请求
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置请求字符编码为 UTF-8
		request.setCharacterEncoding("utf-8");

		// 获取当前会话
		HttpSession session = request.getSession();

		// 获取请求参数
		String s_typeId=request.getParameter("s_typeId");
		String s_releaseDateStr=request.getParameter("s_releaseDateStr");
		String s_title=request.getParameter("s_title");
		String all=request.getParameter("all");
		String page=request.getParameter("page");

		// 创建 Diary 对象，用于存储查询条件
		Diary diary=new Diary();
		if("true".equals(all)){
			// 如果查询所有日志
			if(StringUtil.isNotEmpty(s_title)){
				diary.setTitle(s_title);
			}
			// 清除其他查询条件
			session.removeAttribute("s_releaseDateStr");
			session.removeAttribute("s_typeId");
			session.setAttribute("s_title", s_title);
		}else{
			// 根据特定条件查询日志
			if(StringUtil.isNotEmpty(s_typeId)){
				diary.setTypeId(Integer.parseInt(s_typeId));
				session.setAttribute("s_typeId", s_typeId);
				session.removeAttribute("s_releaseDateStr");
				session.removeAttribute("s_title");
			}
			if(StringUtil.isNotEmpty(s_releaseDateStr)){
				diary.setReleaseDateStr(s_releaseDateStr);
				session.setAttribute("s_releaseDateStr", s_releaseDateStr);
				session.removeAttribute("s_typeId");
				session.removeAttribute("s_title");
			}
			if(StringUtil.isEmpty(s_typeId)){
				Object o=session.getAttribute("s_typeId");
				if(o!=null){
					diary.setTypeId(Integer.parseInt((String)o));
				}
			}
			if(StringUtil.isEmpty(s_releaseDateStr)){
				Object o=session.getAttribute("s_releaseDateStr");
				if(o!=null){
					diary.setReleaseDateStr((String)o);
				}
			}
			if(StringUtil.isEmpty(s_title)){
				Object o=session.getAttribute("s_title");
				if(o!=null){
					diary.setTitle((String)o);
				}
			}
		}

		if(StringUtil.isEmpty(page)){
			page="1";
		}
		Connection con=null;
		// 初始化分页对象，默认每页显示4条记录
		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(PropertiesUtil.getValue("pageSize")));

		try {
			// 获取数据库连接
			con=dbUtil.getCon();
			// 获取符合条件的日志列表
			List<Diary> diaryList=diaryDao.diaryList(con,pageBean,diary);
			// 获取符合条件的日志总数
			int total=diaryDao.diaryCount(con,diary);
			// 生成分页代码
			String pageCode=PaginationUtils.getPagation(total, Integer.parseInt(page), Integer.parseInt(PropertiesUtil.getValue("pageSize")));
			// 将分页代码和日志列表添加到请求属性中
			request.setAttribute("pageCode", pageCode);
			request.setAttribute("diaryList", diaryList);

			// 获取日志类别统计列表
			List<DiaryType> diaryTypeCountList = diaryTypeDao.diaryTypeCountList(con);
			// 获取日志日期统计列表
			List<Diary> diaryCountList = diaryDao.diaryCountList(con);
			// 将统计列表添加到会话属性中
			session.setAttribute("diaryTypeCountList", diaryTypeCountList);
			session.setAttribute("diaryCountList", diaryCountList);
			// 设置主页面
			request.setAttribute("mainPage", "diary/diaryList.jsp");
			// 请求转发到主模板页面
			request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
		}
	}

}
