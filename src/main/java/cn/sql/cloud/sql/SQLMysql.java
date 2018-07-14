package cn.sql.cloud.sql;

import com.mysql.jdbc.Driver;

/**
 * MySQL
 * 
 * @author TQ
 *
 */
public class SQLMysql implements ISQL {

	private SQLMysql() {

	}

	static class InstanceProvider {
		static SQLMysql mysql = new SQLMysql();
	}

	public static SQLMysql build() {
		return InstanceProvider.mysql;
	}

	@Override
	public String getDirverClass() {
		return Driver.class.getName();
	}

	@Override
	public String getJdbcURL(String host, int port, String database) {
		return "jdbc:mysql://" + host + ":" + port + "/" + database;
	}

}
