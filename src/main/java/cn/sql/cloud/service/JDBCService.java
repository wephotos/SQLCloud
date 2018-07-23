package cn.sql.cloud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.sql.SQLType;

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
		if(jdbc.getSqlType() != SQLType.ORACLE){
			jdbc.setDatabase(database);
		}
	}
}
