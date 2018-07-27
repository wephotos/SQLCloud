package cn.sql.cloud.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	 * 根据名称获取JDBC信息
	 * @param username
	 * @param name
	 * @return
	 */
	@Nullable
	public static JDBC getJdbcByName(@Nonnull String username, @Nonnull String name) {
		List<JDBC> list = getJdbcList(username);
		for(JDBC item:list) {
			if(item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * 移除用户下的JDBC连接
	 * @param username
	 * @param jdbcName
	 * @return
	 */
	public static boolean removeJdbc(String username, String jdbcName) {
		JDBC remove = getJdbcByName(username, jdbcName);
		if(remove != null) {
			
			return USER_JDBC_INFO_MAP.get(username).remove(remove);
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
		JDBC jdbc = getJdbcByName(username, jdbcName);
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
		try(Connection conn = createConnection(jdbc)){} catch (SQLException e) {/*close Connection*/}
		return list.add(jdbc);
	}
	
	/**
	 * 获取当前线程持有的连接信息
	 * @return
	 */
	@Nullable
	public static JDBC getHolderJdbc() {
		return JDBC_INFO_HOLDER.get();
	}
	/**
	 * 获取当前连接
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = CONN_HOLDER.get();
		if(conn == null) {
			JDBC jdbc = getHolderJdbc();
			if(jdbc == null) {
				throw new SQLCloudException("当前线程未设置jdbc连接信息");
			}
			conn = createConnection(jdbc);
			CONN_HOLDER.remove();
			CONN_HOLDER.set(conn);
		}
		return conn;
	}
	
	/**
	 * 根据JDBC信息创建连接
	 * @param jdbc
	 * @return
	 */
	public static Connection createConnection(JDBC jdbc) {
		ISQL sql = SQLManager.getSQL(jdbc.getSqlType());
		String url = sql.getURL(jdbc.getHost(), jdbc.getPort(), jdbc.getDatabase());
		try {
			Class.forName(sql.getDirverClass());
			return DriverManager.getConnection(url, jdbc.getUsername(), jdbc.getPassword());
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}
	}
	
	/**
	 * 使用指定的数据源名称创建连接
	 * @param username 用户名
	 * @param jdbcName JDBC名称
	 * @return 新连接
	 */
	public static Connection getConnection(String username, String jdbcName) {
		JDBC jdbc = getJdbcByName(username, jdbcName);
		if(jdbc == null) {
			throw new SQLCloudException("获取不到JDBC连接信息. jdbcName -> " + jdbcName);
		} else {
			return createConnection(jdbc);
		}
	}
	
	/**
	 * 关闭当前数据库连接
	 */
	public static void closeConnection() {
		close(CONN_HOLDER.get());
	}
	
	/**
	 * 关闭指定的数据库连接
	 * @param conn 数据库连接
	 */
	public static void close(Connection conn) {
		if(conn == null) {
			return;
		}
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
