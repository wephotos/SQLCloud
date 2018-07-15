package cn.sql.cloud.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.exception.SQLCloudException;
import cn.sql.cloud.sql.ISQL;
import cn.sql.cloud.sql.SQLManager;

/**
 * JDBC连接按理
 * @author TQ
 *
 */
public final class JDBCManager {
	//log
	private static final Logger logger = LoggerFactory.getLogger(JDBCMapper.class);

	/**
	 * JDBC连接 - 线程变量
	 */
	private final static ThreadLocal<Connection> CONN_HOLDER = new ThreadLocal<Connection>();
	/**
	 * JDBC连接信息 - 线程变量
	 */
	private final static ThreadLocal<JDBCInfo> JDBC_INFO_HOLDER = new ThreadLocal<JDBCInfo>();
	/**
	 * 用户的JDBC连接信息
	 */
	private final static Map<String, List<JDBCInfo>> USER_JDBC_INFO_MAP = new HashMap<String, List<JDBCInfo>>();
	
	/**
	 * 获取用户的JDBC连接信息
	 * @param username
	 * @return
	 */
	public static List<JDBCInfo> getJdbcInfoList(String username) {
		List<JDBCInfo> list = USER_JDBC_INFO_MAP.get(username);
		if(list == null) {
			list = Collections.emptyList();
		}
		return list;
	}
	
	/**
	 * 将指定的连接信息绑定到当前线程
	 * @param username
	 * @param jdbcName
	 */
	public static void holderJdbcInfo(String username, String jdbcName) {
		List<JDBCInfo> list = getJdbcInfoList(username);
		JDBCInfo jdbcInfo = null;
		for(JDBCInfo jdbc:list) {
			if(jdbc.getName().equalsIgnoreCase(jdbcName)) {
				jdbcInfo = jdbc;
				break;
			}
		}
		if(jdbcInfo == null) {
			throw new SQLCloudException("获取不到JDBC连接信息. jdbcName -> " + jdbcName);
		}else {
			JDBC_INFO_HOLDER.remove();
			JDBC_INFO_HOLDER.set(jdbcInfo);
		}
	}
	
	/**
	 * 将JDBC连接添加到用户下
	 * @param jdbcInfo
	 * @param username
	 * @return
	 */
	public static boolean addJdbcInfo(JDBCInfo jdbcInfo, String username) {
		List<JDBCInfo> list = USER_JDBC_INFO_MAP.get(username);
		if(list == null) {
			list = new ArrayList<JDBCInfo>();
			USER_JDBC_INFO_MAP.put(username, list);
		}
		return list.add(jdbcInfo);
	}
	
	/**
	 * 获取当前线程持有的连接信息
	 * @return
	 */
	public static JDBCInfo getHolderJdbcInfo() {
		JDBCInfo jdbc = JDBC_INFO_HOLDER.get();
		if(jdbc == null) {
			throw new SQLCloudException("当前线程中获取不到JDBC连接信息");
		}else {
			return jdbc;
		}
	}
	/**
	 * 获取当前连接
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = CONN_HOLDER.get();
		if(conn == null) {
			JDBCInfo jdbc = getHolderJdbcInfo();
			ISQL sql = SQLManager.getSQL(jdbc.getSqlType());
			String url = sql.getURL(jdbc.getHost(), jdbc.getPort(), jdbc.getDatabase());
			
			try {
				Class.forName(sql.getDirverClass());
				conn = DriverManager.getConnection(url, jdbc.getUsername(), jdbc.getPassword());
				CONN_HOLDER.remove();
				CONN_HOLDER.set(conn);
			} catch (ClassNotFoundException | SQLException e) {
				logger.error(e.getMessage());
				throw new SQLCloudException(e);
			}
		}
		return conn;
	}
	
	/**
	 * 关闭当前数据库连接
	 */
	public static void closeConnection() {
		Connection conn = CONN_HOLDER.get();
		if(conn != null) {
			CONN_HOLDER.remove();
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
				throw new SQLCloudException(e);
			}
		}
	}
}
