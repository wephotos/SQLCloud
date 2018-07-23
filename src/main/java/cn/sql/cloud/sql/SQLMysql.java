package cn.sql.cloud.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.mysql.jdbc.Driver;

import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.PrimaryKey;
import cn.sql.cloud.entity.meta.Table;
import cn.sql.cloud.jdbc.JDBCMapper;

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
		final static SQLMysql MYSQL = new SQLMysql();
	}

	public static SQLMysql getInstance() {
		return InstanceProvider.MYSQL;
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
	public String pageSQL(String sql, int pageNo) {
		if(pageNo < 1) {
			throw new IllegalArgumentException("The page number can't be less than 1");
		}
		int start = (pageNo - 1) * getPageSize();
		return "SELECT * FROM (" + sql + ") pageTable LIMIT " + start + "," + getPageSize();
	}

	@Override
	public List<Table> getTables(String database, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getTables(database, "%", "%", null)){
			return JDBCMapper.resultSet2List(rs, Table.class);
		}
	}

	@Override
	public List<Column> getColumns(String database, String tableName, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getColumns(database, "%", tableName, "%")){
			List<Column> columns = JDBCMapper.resultSet2List(rs, Column.class);
			List<PrimaryKey> keys = getPrimaryKeys(database, tableName, conn);
			for(PrimaryKey key:keys) {
				for(Column column:columns) {
					if(key.getColumnName().equals(column.getName())) {
						column.setPrimaryKey(true);
						break;
					}
				}
			}
			return columns;
		}
	}

	@Override
	public List<PrimaryKey> getPrimaryKeys(String database, String tableName, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getPrimaryKeys(database, "%", tableName)){
			return JDBCMapper.resultSet2List(rs, PrimaryKey.class);
		}
	}
}
