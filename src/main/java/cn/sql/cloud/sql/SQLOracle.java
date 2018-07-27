package cn.sql.cloud.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.IMetaData;
import cn.sql.cloud.entity.meta.PrimaryKey;
import cn.sql.cloud.entity.meta.Table;
import cn.sql.cloud.entity.meta.Virtual;
import cn.sql.cloud.jdbc.JDBCMapper;

/**
 * Oracle
 * 
 * @author TQ
 *
 */
public class SQLOracle implements ISQL {

	private SQLOracle() {

	}

	static class InstanceProvider {
		static SQLOracle ORACLE = new SQLOracle();
	}

	public static SQLOracle getInstance() {
		return InstanceProvider.ORACLE;
	}

	@Override
	public String getDirverClass() {
		return "oracle.jdbc.driver.OracleDriver";
	}

	@Override
	public String getURL(String host, int port, String database) {
		return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
	}
	
	@Override
	public List<? extends IMetaData> getDatabases(Connection conn) throws SQLException {
		return Arrays.asList(new Virtual("Tables"));
	}

	@Override
	public List<Table> getTables(String database, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getTables(null, meta.getUserName(), "%", null)){
			List<Table> tables = JDBCMapper.resultSet2List(rs, Table.class);
			//移除回收站的表
			List<Table> bins = new ArrayList<Table>();
			for(Table table:tables) {
				if(table.getTableName().startsWith("BIN$")) {
					bins.add(table);
				}
			}
			if(bins.size() > 0) {
				tables.removeAll(bins);
			}
			return tables;
		}
	}

	@Override
	public List<Column> getColumns(String database, String tableName, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getColumns(null, meta.getUserName(), tableName, "%")){
			return JDBCMapper.resultSet2List(rs, Column.class);
		}
	}

	@Override
	public List<PrimaryKey> getPrimaryKeys(String database, String tableName, Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();
		try(ResultSet rs = meta.getPrimaryKeys(null, meta.getUserName(), tableName)){
			return JDBCMapper.resultSet2List(rs, PrimaryKey.class);
		}
	}

	@Override
	public String pageSQL(String sql, int pageNo, int pageSize) {
		if (pageNo < 1) {
			throw new IllegalArgumentException("The page number can't be less than 1");
		}
		int start = (pageNo - 1) * pageSize;
		return new StringBuilder("SELECT * FROM(SELECT PAGETABLE.*,ROWNUM RN FROM(").append(sql)
				.append(") PAGETABLE WHERE ROWNUM <= (").append((start + pageSize)).append(")) WHERE RN > ")
				.append(start).toString();
	}
}
