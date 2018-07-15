package cn.sql.cloud.sql;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.Column;
import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.entity.MapQuery;
import cn.sql.cloud.entity.Table;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.jdbc.SQLRunner;

/**
 * SQL业务
 * @author TQ
 *
 */
@Service("sqlService")
public class SQLService {
	
	/**
	 * 获取当前连接下的所有表
	 * @return
	 */
	public List<Table> tables() {
		JDBCInfo jdbc = JDBCManager.getHolderJdbcInfo();
		ISQL sql = SQLManager.getSQL();
		String sqlTables = sql.getSQLTables(jdbc.getDatabase());
		return SQLRunner.executeQuery(sqlTables, Table.class);
	}
	
	/**
	 * 获取表中所有的列
	 * @param tableName
	 * @return
	 */
	public List<Column> columns(String tableName){
		JDBCInfo jdbc = JDBCManager.getHolderJdbcInfo();
		String sqlColumns = SQLManager.getSQL().getSQLColumns(jdbc.getDatabase(), tableName);
		return SQLRunner.executeQuery(sqlColumns, Column.class);
	}
	
	/**
	 * 执行查询语句
	 * @param sql
	 * @return
	 */
	public MapQuery executeQuery(String sql) {
		return SQLRunner.executeMapQuery(sql);
	}

	/**
	 * DML:影响行数; DDL:0,-1
	 * @param sql
	 * @return
	 */
	public int executeUpdate(String sql) {
		return SQLRunner.executeUpdate(sql);
	}
}
