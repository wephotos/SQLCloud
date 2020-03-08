package com.github.wephotos.sqlcloud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.wephotos.sqlcloud.entity.JDBC;
import com.github.wephotos.sqlcloud.jdbc.JDBCManager;
import com.github.wephotos.sqlcloud.sql.SQLType;

/**
 * JDBC连接信息管理
 * @author Administrator
 *
 */
@Service("jdbcService")
public class JDBCService {

	/**
	 * 添加 JDBC 连接信息到用户下
	 * @param jdbc
	 * @param username
	 * @return
	 */
	public boolean add(JDBC jdbc, String username) {
		return JDBCManager.addJdbc(jdbc, username);
	}
	
	/**
	 * 移除JDBC连接
	 * @param jdbcName 连接名
	 * @param username 用户名
	 * @return
	 */
	public boolean remove(String jdbcName, String username) {
		return JDBCManager.removeJdbc(username, jdbcName);
	}
	
	/**
	 * 获取用户下的JDBC连接信息
	 * @param username
	 * @return
	 */
	public List<JDBC> list(String username){
		return JDBCManager.getJdbcList(username);
	}

	/**
	 * 切换数据库
	 * @param database
	 */
	public void useDatabase(String database) {
		JDBC jdbc = JDBCManager.getHolderJdbc();
		if(jdbc == null) {
			return;
		}
		if(jdbc.getSqlType() != SQLType.ORACLE){
			jdbc.setDatabase(database);
		}
	}
}
