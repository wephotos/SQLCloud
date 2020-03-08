package com.github.wephotos.sqlcloud.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.github.wephotos.sqlcloud.entity.meta.Catalog;
import com.github.wephotos.sqlcloud.entity.meta.Column;
import com.github.wephotos.sqlcloud.entity.meta.IMetaData;
import com.github.wephotos.sqlcloud.entity.meta.PrimaryKey;
import com.github.wephotos.sqlcloud.entity.meta.Table;
import com.github.wephotos.sqlcloud.jdbc.JDBCMapper;
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
	public List<? extends IMetaData> getDatabases(Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs =meta.getCatalogs()){
			return JDBCMapper.resultSet2List(rs, Catalog.class);
		}
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
			return JDBCMapper.resultSet2List(rs, Column.class);
		}
	}

	@Override
	public List<PrimaryKey> getPrimaryKeys(String database, String tableName, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getPrimaryKeys(database, "%", tableName)){
			return JDBCMapper.resultSet2List(rs, PrimaryKey.class);
		}
	}

	@Override
	public String pageSQL(String sql, int pageNo, int pageSize) {
		if (pageNo < 1) {
			throw new IllegalArgumentException("The page number can't be less than 1");
		}
		int start = (pageNo - 1) * pageSize;
		return new StringBuilder("SELECT * FROM (").append(sql).append(") pageTable LIMIT ").append(start).append(",")
				.append(pageSize).toString();
	}
}
