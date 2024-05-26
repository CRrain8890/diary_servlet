package com.wishwzp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.wishwzp.model.Diary;
import com.wishwzp.model.PageBean;
import com.wishwzp.util.DateUtil;
import com.wishwzp.util.StringUtil;
/**
 * 日记数据访问对象，用于操作日记相关的数据库表 t_diary
 */
public class DiaryDao {
	/**
	 * PreparedStatement pstmt=con.prepareStatement(sb.toString());
	 *
	 * 这里使用的查询语句是动态生成的，使用了sb.toString()来构建查询语句，其中可能包含了动态的条件和参数。
	 * 对于这个PreparedStatement对象，执行的查询语句可能是类似于SELECT * FROM table WHERE condition=?这样的动态查询，其中?是一个占位符，需要在执行查询之前通过setXXX()方法设置具体的参数值。
	 * 这种情况下，查询结果通常是从数据库中获取一组数据，然后通过ResultSet对象逐行处理这些数据。
	 * PreparedStatement pstmt=con.prepareStatement(sql);
	 *
	 * 这里直接指定了查询语句为SELECT * from t_diaryType where diaryTypeId=?，该查询语句是静态的，没有动态的条件或参数。
	 * 对于这个PreparedStatement对象，执行的查询语句是固定的，不需要在执行查询之前设置参数值。
	 * 这种情况下，查询结果通常是从数据库中获取单个记录或特定字段的值，而不是一组数据。因此，ResultSet对象的处理方式可能会有所不同。
	 */

	/**
	 * 获取日记列表
	 * @param con 数据库连接
	 * @param pageBean 分页信息
	 * @param s_diary 查询条件
	 * @return 符合条件的日记列表
	 * @throws Exception
	 */
	public List<Diary> diaryList(Connection con, PageBean pageBean, Diary s_diary) throws Exception {
		// 创建用于存储日记的列表
		List<Diary> diaryList = new ArrayList<Diary>();
		// 构建查询语句
		StringBuffer sb = new StringBuffer("select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");

		// 添加条件：根据日记类型进行筛选
		if (s_diary.getTypeId() != -1) {
			sb.append(" and t1.typeId=" + s_diary.getTypeId());
		}
		// 添加条件：根据发布日期进行筛选
		if (StringUtil.isNotEmpty(s_diary.getReleaseDateStr())) {
			sb.append(" and DATE_FORMAT(t1.releaseDate,'%Y年%m月')='" + s_diary.getReleaseDateStr() + "'");
		}
		// 添加条件：根据标题进行模糊查询
		if (StringUtil.isNotEmpty(s_diary.getTitle())) {
			sb.append(" and t1.title like '%" + s_diary.getTitle() + "%'");
		}

		// 按发布日期降序排序
		sb.append(" order by t1.releaseDate desc");
		// 如果有分页信息，则进行分页
		if (pageBean != null) {
			sb.append(" limit " + pageBean.getStart() + "," + pageBean.getPageSize());
		}
		// 执行查询
		PreparedStatement pstmt = con.prepareStatement(sb.toString());
		ResultSet rs = pstmt.executeQuery();
		// 遍历结果集，将每条记录转换为 Diary 对象，并添加到列表中
		while (rs.next()) {
			Diary diary = new Diary();
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			// 格式化日期字符串为指定格式
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"), "yyyy-MM-dd HH:mm:ss"));
			diaryList.add(diary);
		}
		// 返回日记列表
		return diaryList;
	}


	/**
	 * 统计对应类别的日记个数
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public int diaryCount(Connection con,Diary s_diary)throws Exception{
		StringBuffer sb=new StringBuffer("select count(*) as total from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId ");

		if(StringUtil.isNotEmpty(s_diary.getTitle())){
			sb.append(" and t1.title like '%"+s_diary.getTitle()+"%'");
		}
		if(s_diary.getTypeId()!=-1){
			sb.append(" and t1.typeId="+s_diary.getTypeId());
		}
		if(StringUtil.isNotEmpty(s_diary.getReleaseDateStr())){
			sb.append(" and DATE_FORMAT(t1.releaseDate,'%Y年%m月')='"+s_diary.getReleaseDateStr()+"'");
		}

		PreparedStatement pstmt=con.prepareStatement(sb.toString());
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			return rs.getInt("total");
		}else{
			return 0;
		}
	}

	/**
	 * 查询日记日期列表显示
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public List<Diary> diaryCountList(Connection con)throws Exception{
		List<Diary> diaryCountList=new ArrayList<Diary>();
		String sql="SELECT DATE_FORMAT(releaseDate,'%Y年%m月') as releaseDateStr ,COUNT(*) AS diaryCount  FROM t_diary GROUP BY DATE_FORMAT(releaseDate,'%Y年%m月') ORDER BY DATE_FORMAT(releaseDate,'%Y年%m月') DESC;";
		PreparedStatement pstmt=con.prepareStatement(sql);
		//Statement stmt = con.createStatement();
		ResultSet rs=pstmt.executeQuery();
		while(rs.next()){
			Diary diary=new Diary();
			diary.setReleaseDateStr(rs.getString("releaseDateStr"));
			diary.setDiaryCount(rs.getInt("diaryCount"));
			diaryCountList.add(diary);
		}
		return diaryCountList;
	}

	/**
	 * 日记内容显示
	 * @param con
	 * @param diaryId
	 * @return
	 * @throws Exception
	 */
	public Diary diaryShow(Connection con,String diaryId)throws Exception{
		String sql="select * from t_diary t1,t_diaryType t2 where t1.typeId=t2.diaryTypeId and t1.diaryId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diaryId);
		ResultSet rs=pstmt.executeQuery();
		Diary diary=new Diary();
		if(rs.next()){
			diary.setDiaryId(rs.getInt("diaryId"));
			diary.setTitle(rs.getString("title"));
			diary.setContent(rs.getString("content"));
			diary.setTypeId(rs.getInt("typeId"));
			diary.setTypeName(rs.getString("typeName"));
			diary.setReleaseDate(DateUtil.formatString(rs.getString("releaseDate"),"yyyy-MM-dd HH:mm:ss"));
		}
		return diary;
	}
	
	/**
	 * 添加日记
	 * @param con
	 * @param diary
	 * @return
	 * @throws Exception
	 */
	public int diaryAdd(Connection con,Diary diary)throws Exception{
		String sql="insert into t_diary values(null,?,?,?,now())";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diary.getTitle());
		pstmt.setString(2, diary.getContent());
		pstmt.setInt(3, diary.getTypeId());
		return pstmt.executeUpdate();
	}
	
	/**
	 * 删除日记
	 * @param con
	 * @param diaryId
	 * @return
	 * @throws Exception
	 */
	public int diaryDelete(Connection con,String diaryId)throws Exception{
		String sql="delete from t_diary where diaryId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diaryId);
		return pstmt.executeUpdate();
	}
	
	/**
	 * 修改日记
	 * @param con
	 * @param diary
	 * @return
	 * @throws Exception
	 */
	public int diaryUpdate(Connection con,Diary diary)throws Exception{
		String sql="update t_diary set title=?,content=?,typeId=? where diaryId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, diary.getTitle());
		pstmt.setString(2, diary.getContent());
		pstmt.setInt(3, diary.getTypeId());
		pstmt.setInt(4, diary.getDiaryId());
		return pstmt.executeUpdate();
	}
	
	/**
	 * 判断该日记类别下是否有日记
	 * @param con
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public boolean existDiaryWithTypeId(Connection con,String typeId)throws Exception{
		String sql="select * from t_diary where typeId=?";
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, typeId);
		ResultSet rs=pstmt.executeQuery();
		if(rs.next()){
			return true;
		}else{
			return false;
		}
	}
}
