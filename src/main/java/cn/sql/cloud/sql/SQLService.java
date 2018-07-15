package cn.sql.cloud.sql;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.Column;
import cn.sql.cloud.entity.JDBCInfo;
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
		String username = "root", jdbcName = "mysql";
		JDBCInfo jdbcInfo = new JDBCInfo();
		jdbcInfo.setDatabase("sqlcloud");
		jdbcInfo.setHost("localhost");
		jdbcInfo.setName(jdbcName);
		jdbcInfo.setUsername("root");
		jdbcInfo.setPassword("root");
		jdbcInfo.setPort(3306);
		jdbcInfo.setSqlType(SQLType.MYSQL);
		JDBCManager.addJdbcInfo(jdbcInfo, username);
		JDBCManager.holderJdbcInfo(username, jdbcName);
		ISQL sql = SQLManager.getSQL();
		String sqlTables = sql.getSQLTables(jdbcInfo.getDatabase());
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
}
