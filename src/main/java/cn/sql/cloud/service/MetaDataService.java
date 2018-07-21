package cn.sql.cloud.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.Column;
import cn.sql.cloud.entity.Database;
import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.entity.MetaData;
import cn.sql.cloud.entity.SQLTypeInfo;
import cn.sql.cloud.entity.Table;
import cn.sql.cloud.exception.SQLCloudException;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.jdbc.JDBCMapper;
import cn.sql.cloud.jdbc.SQLRunner;
import cn.sql.cloud.sql.ISQL;
import cn.sql.cloud.sql.SQLManager;

/**
 * 元数据Service
 * @author TQ
 *
 */
@Service("metaDataService")
public class MetaDataService {
	//log
	static final Logger logger = LoggerFactory.getLogger(MetaDataService.class);
	/**
	 * 获取数据库对象树顶级节点
	 * @return
	 */
	public List<? extends MetaData> topTreeNodes(){
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
		JDBC jdbc = JDBCManager.getHolderJdbc();
		if(StringUtils.isBlank(database)) {
			database = JDBCManager.getHolderJdbc().getDatabase();
		}
		String sqlTables = SQLManager.getSQL().getSQLTables(database, jdbc.getUsername());
		return SQLRunner.executeQuery(sqlTables, Table.class);
	}
	
	/**
	 * 获取表中所有的列
	 * @param database 数据库
	 * @param tablename 表名
	 * @return
	 */
	public List<Column> columns(String database, String tablename){
		JDBC jdbc = JDBCManager.getHolderJdbc();
		String username = jdbc.getUsername();
		String sqlColumns = SQLManager.getSQL().getSQLColumns(database, username, tablename);
		return SQLRunner.executeQuery(sqlColumns, Column.class);
	}
	/**
	 * 获取此数据库受支持的数据类型信息
	 * @return
	 */
	public List<SQLTypeInfo> typeInfos(){
		Connection conn = JDBCManager.getConnection();
		try {
			ResultSet rs = conn.getMetaData().getTypeInfo();
			return JDBCMapper.resultSet2List(rs, SQLTypeInfo.class);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException("获取数据类型信息错误:" + e.getMessage());
		}
	}
}
