<%@ page language="java" contentType="text/html; charset=utf-8"
		 pageEncoding="utf-8"%>
<%@ page language="java" import="com.wishwzp.model.User"%> <!-- 导入 User 类 -->

<%
	// 判断是否存在用户对象，若不存在，则从 Cookie 中获取用户名和密码，设置到 user 对象中
	if(request.getAttribute("user")==null){
		String userName=null;
		String password=null;

		// 获取所有的 Cookie
		Cookie[] cookies=request.getCookies();
		// 遍历 Cookie，查找名为 "user" 的 Cookie，并获取用户名和密码
		for(int i=0;cookies!=null && i<cookies.length;i++){
			if(cookies[i].getName().equals("user")){
				userName=cookies[i].getValue().split("-")[0];
				password=cookies[i].getValue().split("-")[1];
			}
		}

		// 如果用户名为空，则设置为空字符串
		if(userName==null){
			userName="";
		}

		// 如果密码为空，则设置为空字符串
		if(password==null){
			password="";
		}

		// 将用户名和密码设置到 user 对象中
		pageContext.setAttribute("user", new User(userName,password));
	}
%>

<html lang="zh">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>个人日记本登录</title>
	<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.css" rel="stylesheet"> <!-- 引入 Bootstrap 样式文件 -->
	<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap-responsive.css" rel="stylesheet"> <!-- 引入 Bootstrap 响应式布局样式文件 -->
	<script src="${pageContext.request.contextPath}/bootstrap/js/jQuery.js"></script> <!-- 引入 jQuery 库 -->
	<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.js"></script> <!-- 引入 Bootstrap JavaScript 文件 -->
	<style type="text/css">
		/* 自定义样式 */
		body {
			padding-top: 200px;
			padding-bottom: 40px;
			background-image: url('images/star.gif');
		}

		.form-signin-heading{
			text-align: center;
		}

		.form-signin {
			max-width: 300px;
			padding: 19px 29px 0px;
			margin: 0 auto 20px;
			background-color: #fff;
			border: 1px solid #e5e5e5;
			-webkit-border-radius: 5px;
			-moz-border-radius: 5px;
			border-radius: 5px;
			-webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
			-moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
			box-shadow: 0 1px 2px rgba(0,0,0,.05);
		}

		.form-signin .form-signin-heading,
		.form-signin .checkbox {
			margin-bottom: 10px;
		}

		.form-signin input[type="text"],
		.form-signin input[type="password"] {
			font-size: 16px;
			height: auto;
			margin-bottom: 15px;
			padding: 7px 9px;
		}
	</style>
	<script type="text/javascript">
		// JavaScript 函数，用于在提交表单前进行表单验证，确保用户名和密码不为空
		function checkForm(){
			var userName=document.getElementById("userName").value;
			var password=document.getElementById("password").value;
			if(userName==null || userName==""){
				document.getElementById("error").innerHTML="用户名不能为空";
				return false;
			}
			if(password==null || password==""){
				document.getElementById("error").innerHTML="密码不能为空";
				return false;
			}
			return true;
		}
	</script>
</head>
<body>
<div class="container">
	<!-- 登录表单 -->
	<form name="myForm" class="form-signin" action="login" method="post" onsubmit="return checkForm()">
		<h2 class="form-signin-heading">个人日记本</h2>
		<!-- 用户名输入框 -->
		<input id="userName" name="userName" value="${user.userName}"  type="text" class="input-block-level" placeholder="用户名">
		<!-- 密码输入框 -->
		<input id="password" name="password" value="${user.password}"   type="password" class="input-block-level" placeholder="密码" >
		<!-- 记住我复选框 -->
		<label class="checkbox">
			<input id="remember" name="remember" type="checkbox" value="remember-me">记住我 &nbsp;&nbsp;&nbsp;&nbsp; <font id="error" color="red">${error }</font>
		</label>
		<!-- 重置按钮 -->
		<button class="btn btn-large btn-primary" style="margin-left: 55px" type="reset">重置</button>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<!-- 提交按钮 -->
		<button class="btn btn-large btn-primary" type="submit" >提交</button>
		<p align="center" style="padding-top: 15px;"></p>
	</form>
</div>
</body>
