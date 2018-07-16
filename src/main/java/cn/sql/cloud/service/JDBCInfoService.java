package cn.sql.cloud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.jdbc.JDBCManager;

/**
 * JDBC连接信息管理
 * @author Administrator
 *
 */
@Service("jdbcInfoService")
public class JDBCInfoService {

	/**
	 * 添加 JDBC 连接信息到用户下
	 * @param jdbc
	 * @param username
	 * @return
	 */
	public boolean add(JDBCInfo jdbc, String username) {
		return JDBCManager.addJdbcInfo(jdbc, username);
	}
	
	/**
	 * 移除JDBC连接
	 * @param jdbcName 连接名
	 * @param username 用户名
	 * @return
	 */
	public boolean remove(String jdbcName, String username) {
		return JDBCManager.removeJdbcInfo(username, jdbcName);
	}
	
	/**
	 * 获取用户下的JDBC连接信息
	 * @param username
	 * @return
	 */
	public List<JDBCInfo> list(String username){
		return JDBCManager.getJdbcInfoList(username);
	}
}
