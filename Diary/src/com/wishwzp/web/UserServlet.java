package com.wishwzp.web;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.wishwzp.dao.UserDao;
import com.wishwzp.model.User;
import com.wishwzp.util.DateUtil;
import com.wishwzp.util.DbUtil;
import com.wishwzp.util.PropertiesUtil;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 初始化数据库工具类和 DAO 类
	DbUtil dbUtil = new DbUtil();
	UserDao userDao = new UserDao();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
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

		// 获取请求参数
		String action=request.getParameter("action");
		if("preSave".equals(action)){
			// 进入用户信息编辑页面
			userPreSave(request,response);
		}else if("save".equals(action)){
			// 保存用户信息
			userSave(request,response);
		}
	}

	/**
	 * 进入用户信息编辑页面
	 * @param request HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void userPreSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		request.setAttribute("mainPage", "user/userSave.jsp");
		request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
	}

	/**
	 * 保存用户信息
	 * @param request HTTP 请求
	 * @param response HTTP 响应
	 * @throws ServletException 如果请求处理失败
	 * @throws IOException 如果发生 I/O 错误
	 */
	private void userSave(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		// 判断是否为文件上传表单
		if(ServletFileUpload.isMultipartContent(request)){
			System.out.println("yes");
		}
		// 实例化一个硬盘文件工厂,用来配置上传组件ServletFileUpload
		FileItemFactory factory=new DiskFileItemFactory();
		// 创建ServletFileUpload对象
		ServletFileUpload upload=new ServletFileUpload(factory);

		List<FileItem> items=null;
		try {
			// 解析请求，获取上传的文件项列表
			items=upload.parseRequest(new ServletRequestContext(request));
		} catch (FileUploadException e) {
			// 处理文件上传异常
			e.printStackTrace();
		}
		// 取得items的迭代器
		Iterator<FileItem> itr=items==null?null:items.iterator();

		HttpSession session=request.getSession();
		// 获取当前用户信息
		User user=(User)session.getAttribute("currentUser");
		boolean imageChange=false;
		// 迭代文件项列表
		while(itr.hasNext()){
			FileItem item=(FileItem)itr.next();
			// 如果传过来的是普通的表单域
			if(item.isFormField()){
				String fieldName=item.getFieldName();
				if("nickName".equals(fieldName)){
					user.setNickName(item.getString("utf-8"));
				}
				if("mood".equals(fieldName)){
					user.setMood(item.getString("utf-8"));
				}
			}else if(!"".equals(item.getName())){
				try{
					// 上传了新头像
					imageChange=true;
					String imageName=DateUtil.getCurrentDateStr();
					user.setImageName(imageName+"."+item.getName().split("\\.")[1]);
					String filePath=PropertiesUtil.getValue("imagePath")+imageName+"."+item.getName().split("\\.")[1];
					// 保存上传的文件
					item.write(new File(filePath));
				}catch(Exception e){
					// 处理文件写入异常
					e.printStackTrace();
				}
			}
		}

		// 如果没有上传新头像，保留原头像路径
		if(!imageChange){
			user.setImageName(user.getImageName().replaceFirst(PropertiesUtil.getValue("imageFile"), ""));
		}

		Connection con=null;
		try {
			con=dbUtil.getCon();
			// 更新用户信息
			int saveNums=userDao.userUpdate(con, user);
			if(saveNums>0){
				user.setImageName(PropertiesUtil.getValue("imageFile")+user.getImageName());
				session.setAttribute("currentUser", user);
				// 保存成功，跳转到用户管理页面
				request.getRequestDispatcher("main?all=true").forward(request, response);
			}else{
				// 保存失败，返回用户信息编辑页面
				request.setAttribute("currentUser", user);
				request.setAttribute("error", "保存失败！");
				request.setAttribute("mainPage", "user/userSave.jsp");
				request.getRequestDispatcher("mainTemp.jsp").forward(request, response);
			}
		} catch (Exception e) {
			// 处理数据库连接异常
			e.printStackTrace();
		}finally{
			try {
				dbUtil.closeCon(con);
			} catch (Exception e) {
				// 处理数据库连接关闭异常
				e.printStackTrace();
			}
		}
	}

}
