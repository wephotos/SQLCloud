package cn.sql.cloud.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.Column;
import cn.sql.cloud.entity.Database;
import cn.sql.cloud.entity.QueryResult;
import cn.sql.cloud.entity.SQLObject;
import cn.sql.cloud.entity.SQLResult;
import cn.sql.cloud.entity.Table;
import cn.sql.cloud.entity.UpdateResult;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.jdbc.SQLRunner;
import cn.sql.cloud.sql.ISQL;
import cn.sql.cloud.sql.SQLManager;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * SQL业务
 * @author TQ
 *
 */
@Service("sqlService")
public class SQLService {
	
	
	/**
	 * 获取数据库对象树顶级节点
	 * @return
	 */
	public List<? extends SQLObject> topTreeNodes(){
		List<Database> databases = databases();
		if(databases.size() > 0) {
			return databases;
		}else {
			return tables(null);
		}
	}
	
	/**
	 * 获取当前所有数据库
	 * @return
	 */
	public List<Database> databases(){
		ISQL sql = SQLManager.getSQL();
		String sqlDatabases = sql.getSQLDatabases();
		if(StringUtils.isBlank(sqlDatabases)) {
			return Collections.emptyList();
		}else {
			return SQLRunner.executeQuery(sqlDatabases, Database.class);
		}
	}
	
	/**
	 * 获取当前连接下的所有表
	 * @param 数据库
	 * @return
	 */
	public List<Table> tables(String database) {
		ISQL sql = SQLManager.getSQL();
		if(StringUtils.isBlank(database)) {
			database = JDBCManager.getHolderJdbcInfo().getDatabase();
		}
		String sqlTables = sql.getSQLTables(database);
		return SQLRunner.executeQuery(sqlTables, Table.class);
	}
	
	/**
	 * 获取表中所有的列
	 * @param database 数据库
	 * @param tablename 表名
	 * @return
	 */
	public List<Column> columns(String database, String tablename){
		String sqlColumns = SQLManager.getSQL().getSQLColumns(database, tablename);
		return SQLRunner.executeQuery(sqlColumns, Column.class);
	}
	
	/**
	 * 执行查询语句
	 * @param sql
	 * @return
	 */
	public QueryResult executeQuery(String sql) {
		return SQLRunner.executeMapQuery(sql);
	}

	/**
	 * DML:影响行数; DDL:0,-1
	 * @param sql
	 * @return
	 */
	public UpdateResult executeUpdate(String sql) {
		return SQLRunner.executeUpdate(sql);
	}
	
	/**
	 * 执行SQL，多条以分号分隔
	 * @param sql
	 * @return
	 */
	public List<SQLResult> execute(String sql) {
		List<SQLResult> results = new ArrayList<SQLResult>();
		String[] sqls = SQLCloudUtils.splitSQL(sql);
		for(String single:sqls) {
			if(StringUtils.isBlank(single)) {
				continue;
			}
			if(SQLCloudUtils.isQuerySQL(single)) {
				ISQL mysql = SQLManager.getSQL();
				long total = 0;
				if(SQLCloudUtils.startsWithSelect(single)) {
					single = mysql.pageSQL(single, 1);
					String countSQL = SQLCloudUtils.parseCountSQL(single);
					total = SQLRunner.executeQuery(countSQL, Long.class).get(0);
				}
				QueryResult queryResult = executeQuery(single);
				queryResult.setTotal(total);
				results.add(queryResult);
			}else {
				results.add(executeUpdate(single));
			}
		}
		return results;
	}
}
