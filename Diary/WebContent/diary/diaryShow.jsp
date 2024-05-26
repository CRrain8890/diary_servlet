<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script type="text/javascript">
	function diaryDelete(diaryId){
		if(confirm("您确定要删除这个日志吗？")){
			window.location="diary?action=delete&diaryId="+diaryId;
		}
	}
</script>

<div class="data_list">
		<div class="data_list_title">
		<img src="${pageContext.request.contextPath}/images/diary_show_icon.png"/>
		日记信息</div>
		<div>
			<div class="diary_title"><h3>${diary.title }</h3></div>
			
			<div class="diary_info">
				发布时间：『 <fmt:formatDate value="${diary.releaseDate }" type="date" pattern="yyyy-MM-dd HH:mm:ss"/>』&nbsp;&nbsp;日志类别：${diary.typeName}
			</div>
			
			<!-- 内容信息 -->
			<div class="diary_content">
				${diary.content }
			</div>
			
			<!-- 三个按钮 -->
			<div class="diary_action">
				<button class="btn btn-primary" type="button" onclick="javascript:window.location='diary?action=preSave&diaryId=${diary.diaryId}'">修改日志</button>
				<button class="btn btn-primary" type="button" onclick="javascript:history.back()">返回</button>
				<button class="btn btn-danger" type="button" onclick="diaryDelete(${diary.diaryId})">删除日志</button>
			</div>
		</div>
</div>