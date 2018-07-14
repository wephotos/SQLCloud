package cn.sql.cloud.sql;

/**
 * SQL语句接口
 * @author TQ
 *
 */
public interface ISQL {

	/**
	 * 获取驱动类
	 * @return
	 */
	String getDirverClass();
	
	/**
	 * 获取JDBC-URL
	 * @return
	 */
	String getJdbcURL(String host, int port, String database);
}
