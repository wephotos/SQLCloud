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
	public String getURL(String host, int port, String database) {
		return "jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8&useSSL=true";
	}

	@Override
	public String getSQLTables(String database) {
		return "select table_name,create_time,update_time,table_comment from information_schema.tables where table_schema='"+database+"' and table_type='base table'";
	}

	@Override
	public String getSQLColumns(String database, String tableName) {
		return "select column_name,data_type,column_type,column_comment from information_schema.columns where table_schema='"+database+"' and table_name='"+tableName+"'";
	}

}
