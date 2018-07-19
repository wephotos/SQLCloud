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
	public String getSQLDatabases() {
		return "SELECT SCHEMA_NAME NAME FROM INFORMATION_SCHEMA.SCHEMATA";
	}

	@Override
	public String getURL(String host, int port, String database) {
		return "jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8&useSSL=false&autoReconnect=true";
	}

	@Override
	public String getSQLTables(String database) {
		return "select table_schema data_base,table_name,create_time,update_time,table_comment "
				+ "from information_schema.tables where table_schema='" + database + "'";
	}

	@Override
	public String getSQLColumns(String database, String tableName) {
		return "SELECT COLUMN_NAME,DATA_TYPE,COLUMN_TYPE,COLUMN_COMMENT,"
				+ "CASE IS_NULLABLE WHEN 'NO' THEN FALSE ELSE TRUE END NULLABLE,"
				+ "CASE (SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE "
				+ "TABLE_SCHEMA = INFORMATION_SCHEMA.COLUMNS.TABLE_SCHEMA "
				+ "AND TABLE_NAME = INFORMATION_SCHEMA.COLUMNS.TABLE_NAME "
				+ "AND COLUMN_NAME=INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME AND CONSTRAINT_NAME='PRIMARY') "
				+ "WHEN 'PRIMARY' THEN TRUE ELSE FALSE END KEY_PRIMARY FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE TABLE_SCHEMA='" + database + "' AND TABLE_NAME='" + tableName + "'";
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
