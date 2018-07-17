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
		return "jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8&useSSL=false&autoReconnect=true";
	}

	@Override
	public String getSQLTables(String database) {
		return "select table_name,create_time,update_time,table_comment from information_schema.tables where table_schema='"+database+"' and table_type='base table'";
	}

	@Override
	public String getSQLColumns(String database, String tableName) {
		return "select column_name,data_type,column_type,column_comment from information_schema.columns where table_schema='"+database+"' and table_name='"+tableName+"'";
	}

	@Override
	public String pageSQL(String sql, int pageNo) {
		if(pageNo < 1) {
			throw new IllegalArgumentException("The page number can't be less than 1");
		}
		int start = (pageNo - 1) * getPageSize();
		return "SELECT * FROM (" + sql + ") pageTable LIMIT " + start + "," + getPageSize();
	}

}
