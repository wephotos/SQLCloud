package com.github.wephotos.sqlcloud.sql;

import com.github.wephotos.sqlcloud.entity.JDBC;
import com.github.wephotos.sqlcloud.exception.SQLCloudException;
import com.github.wephotos.sqlcloud.jdbc.JDBCManager;

/**
 * SQL管理类
 * @author TQ
 *
 */
public final class SQLManager {
	
	/**
	 * 获取当前线程操作的SQL对象
	 * @return
	 */
	public static ISQL getSQL() {
		JDBC jdbc = JDBCManager.getHolderJdbc();
		if(jdbc != null) {
			return getSQL(jdbc.getSqlType());
		}
		throw new SQLCloudException("当前线程未持有数据库连接信息");
	}
	
	/**
	 * 根据SQLTypes获取SQL对象
	 * @param sqlType
	 * @return
	 */
	public static ISQL getSQL(SQLType sqlType) {
		if(SQLType.ORACLE == sqlType) {
			return SQLOracle.getInstance();
		}else if(SQLType.MYSQL == sqlType) {
			return SQLMysql.getInstance();
		}else {
			throw new IllegalArgumentException(SQLManager.class.getName()+".getSQL(?) ->" + sqlType);
		}
	}
}
