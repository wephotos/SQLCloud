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

import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.exception.JDBCNotFoundException;
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
	private final static ThreadLocal<JDBC> JDBC_INFO_HOLDER = new ThreadLocal<JDBC>();
	/**
	 * 用户的JDBC连接信息
	 */
	private final static Map<String, List<JDBC>> USER_JDBC_INFO_MAP = new HashMap<String, List<JDBC>>();
	
	/**
	 * 获取用户的JDBC连接信息
	 * @param username
	 * @return
	 */
	public static List<JDBC> getJdbcList(String username) {
		List<JDBC> list = USER_JDBC_INFO_MAP.get(username);
		if(list == null) {
			list = Collections.emptyList();
		}
		return Collections.unmodifiableList(list);
	}
	
	/**
	 * 移除用户下的JDBC连接
	 * @param username
	 * @param jdbcName
	 * @return
	 */
	public static boolean removeJdbc(String username, String jdbcName) {
		List<JDBC> list = USER_JDBC_INFO_MAP.get(username);
		if(list != null) {
			JDBC remove = null;
			for(JDBC jdbc:list) {
				if(jdbc.getName().equals(jdbcName)) {
					remove = jdbc;
					break;
				}
			}
			if(remove != null) {
				return list.remove(remove);
			}
		}
		return false;
	}
	
	/**
	 * 将指定的连接信息绑定到当前线程
	 * @param username
	 * @param jdbcName
	 * @throws JDBCNotFoundException 
	 */
	public static void holderJdbc(String username, String jdbcName) throws JDBCNotFoundException {
		List<JDBC> list = getJdbcList(username);
		JDBC jdbc = null;
		for(JDBC item:list) {
			if(item.getName().equalsIgnoreCase(jdbcName)) {
				jdbc = item;
				break;
			}
		}
		if(jdbc == null) {
			throw new JDBCNotFoundException("获取不到JDBC连接信息. jdbcName -> " + jdbcName);
		}else {
			JDBC_INFO_HOLDER.remove();
			JDBC_INFO_HOLDER.set(jdbc);
		}
	}
	
	/**
	 * 将JDBC连接添加到用户下
	 * @param jdbc
	 * @param username
	 * @return
	 */
	public static boolean addJdbc(JDBC jdbc, String username) {
		List<JDBC> list = USER_JDBC_INFO_MAP.get(username);
		if(list == null) {
			list = new ArrayList<JDBC>();
			USER_JDBC_INFO_MAP.put(username, list);
		}
		for(JDBC _jdbc:list) {
			if(_jdbc.getName().equals(jdbc.getName())) {
				return false;
			}
		}
		Connection conn = null;
		try {
			conn = createConnection(jdbc);
		} catch (SQLException e) {
			throw new SQLCloudException("连接失败:" + e.getMessage());
		}finally {
			if(conn != null) {
				try {conn.close();} catch (SQLException e) {}
			}
		}
		return list.add(jdbc);
	}
	
	/**
	 * 获取当前线程持有的连接信息
	 * @return
	 */
	public static JDBC getHolderJdbc() {
		JDBC jdbc = JDBC_INFO_HOLDER.get();
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
			JDBC jdbc = getHolderJdbc();
			try {
				conn = createConnection(jdbc);
			} catch (SQLException e) {
				logger.error(e.getMessage());
				throw new SQLCloudException(e);
			}
			CONN_HOLDER.remove();
			CONN_HOLDER.set(conn);
		}
		return conn;
	}
	
	private static Connection createConnection(JDBC jdbc) throws SQLException {
		ISQL sql = SQLManager.getSQL(jdbc.getSqlType());
		String url = sql.getURL(jdbc.getHost(), jdbc.getPort(), jdbc.getDatabase());
		try {
			Class.forName(sql.getDirverClass());
			return DriverManager.getConnection(url, jdbc.getUsername(), jdbc.getPassword());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}
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
				logger.info("closed Connection:{}, isClosed:{} ", conn.toString(), conn.isClosed());
			} catch (SQLException e) {
				logger.error("close jdbc Connection failed:" + e.getMessage());
				throw new SQLCloudException(e);
			}
		}
	}
}
