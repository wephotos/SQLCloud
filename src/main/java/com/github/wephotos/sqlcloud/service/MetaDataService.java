package com.github.wephotos.sqlcloud.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.wephotos.sqlcloud.entity.meta.Column;
import com.github.wephotos.sqlcloud.entity.meta.IMetaData;
import com.github.wephotos.sqlcloud.entity.meta.Table;
import com.github.wephotos.sqlcloud.entity.meta.TypeInfo;
import com.github.wephotos.sqlcloud.exception.SQLCloudException;
import com.github.wephotos.sqlcloud.jdbc.JDBCManager;
import com.github.wephotos.sqlcloud.jdbc.JDBCMapper;
import com.github.wephotos.sqlcloud.sql.ISQL;
import com.github.wephotos.sqlcloud.sql.SQLManager;

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
	 * @throws SQLException 
	 */
	public List<? extends IMetaData> topTreeNodes() throws SQLException{
		ISQL sql = SQLManager.getSQL();
		Connection conn = JDBCManager.getConnection();
		List<? extends IMetaData> databases = sql.getDatabases(conn);
		if(databases.isEmpty()) {
			return tables(null);
		}else {
			return databases;
		}
	}
	
	/**
	 * 获取当前连接下的所有表
	 * @param database 数据库
	 * @return
	 * @throws SQLException 
	 */
	public List<Table> tables(String database) throws SQLException {
		Connection conn = JDBCManager.getConnection();
		return SQLManager.getSQL().getTables(database, conn);
	}
	
	/**
	 * 获取表中所有的列
	 * @param database 数据库
	 * @param tableName 表名
	 * @return
	 * @throws SQLException 
	 */
	public List<Column> columns(String database, String tableName) throws SQLException{
		Connection conn = JDBCManager.getConnection();
		return SQLManager.getSQL().getColumnsPrimaryKey(database, tableName, conn);
	}
	/**
	 * 获取此数据库受支持的数据类型信息
	 * @return
	 */
	public List<TypeInfo> getTypeInfo(){
		try {
			Connection conn = JDBCManager.getConnection();
			ResultSet rs = conn.getMetaData().getTypeInfo();
			return JDBCMapper.resultSet2List(rs, TypeInfo.class);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException("获取数据类型信息错误:" + e.getMessage());
		}
	}
}
