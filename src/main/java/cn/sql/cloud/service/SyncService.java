package cn.sql.cloud.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.entity.Sync;
import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.Table;
import cn.sql.cloud.entity.meta.TypeInfo;
import cn.sql.cloud.exception.SQLCloudException;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.jdbc.JDBCMapper;
import cn.sql.cloud.jdbc.SQLRunner;
import cn.sql.cloud.sql.ISQL;
import cn.sql.cloud.sql.SQLManager;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * 数据库同步 Service
 * 
 * @author TQ
 *
 */
@Service("syncService")
//使用到实例对象,需要考虑线程安全问题，不使用单例
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SyncService {
	// log
	final static Logger logger = LoggerFactory.getLogger(SyncService.class);
	/**
	 * 批量同步的数据量
	 */
	public final static int SYNC_BATCH_SIZE = 1000;
	/**
	 * 源连接
	 */
	private Connection connsrc = null;
	/**
	 * 目标连接
	 */
	private Connection conndes = null;

	private JDBC jdbcsrc;
	private JDBC jdbcdes;

	private ISQL sqlsrc;
	private ISQL sqldes;
	/**
	 *  {@link Types}与目标库的数据类型信息的映射
	 */
	private Map<Integer, TypeInfo> typeInfoMap = new HashMap<Integer, TypeInfo>(100);

	/**
	 * 初始化连接信息
	 * 
	 * @param sync
	 */
	private void initConn(Sync sync) {
		String username = sync.getUsername(), src = sync.getSrc(), dest = sync.getDest();
		connsrc = JDBCManager.getConnection(username, src);
		conndes = JDBCManager.getConnection(username, dest);
		jdbcsrc = JDBCManager.getJdbcByName(username, src);
		jdbcdes = JDBCManager.getJdbcByName(username, dest);

		sqlsrc = SQLManager.getSQL(jdbcsrc.getSqlType());
		sqldes = SQLManager.getSQL(jdbcdes.getSqlType());
		
		try {
			try(ResultSet rs = conndes.getMetaData().getTypeInfo()){
				List<TypeInfo> typeInfos = JDBCMapper.resultSet2List(rs, TypeInfo.class);
				for(TypeInfo type:typeInfos) {
					//优先匹配首选项
					if(!typeInfoMap.containsKey(type.getDataType())) {
						typeInfoMap.put(type.getDataType(), type);
					}
				}
			}
			//设置为手动提交
			conndes.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 释放连接
	 */
	private void release() {
		JDBCManager.close(connsrc);
		JDBCManager.close(conndes);
		typeInfoMap = null;
		connsrc = null;
		conndes = null;
	}

	/**
	 * 同步数据库对象
	 * 
	 * @param sync 数据库同步信息
	 */
	public void run(Sync sync) {
		try {
			initConn(sync);
			List<Table> tables = sqlsrc.getTables(jdbcsrc.getDatabase(), connsrc);
			for (Table table : tables) {
				String tableName = table.getName();
				List<Column> columns = syncStruct(tableName, sync);
				if(!columns.isEmpty()) {
					syncData(tableName, columns);
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		} finally {
			release();
		}
	}
	
	/**
	 * 同步表数据
	 * @param tableName
	 * @param columns
	 * @throws SQLException
	 */
	private void syncData(String tableName, List<Column> columns) throws SQLException {
		String insertSQL = sqldes.insertSQL(tableName, columns);
		//预编译INSERT语句
		try (PreparedStatement prest = conndes.prepareStatement(insertSQL)) {
			try (Statement stmt = connsrc.createStatement()) {
				String select = sqlsrc.selectSQL(tableName);
				int totalPN = getTotalPageNo(select);//分页同步
				for (int pageNo = 1; pageNo <= totalPN; pageNo++) {
					String pageSQL = sqlsrc.pageSQL(select, pageNo, SYNC_BATCH_SIZE);
					try (ResultSet rs = stmt.executeQuery(pageSQL)) {
						while (rs.next()) {
							for (int i = 0, l = columns.size(); i < l; i++) {
								String columnName = columns.get(i).getName();
								prest.setObject(i + 1, rs.getObject(columnName));
							}
							prest.addBatch();
							prest.executeBatch();
							conndes.commit();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 同步表结构，返回列信息，创建失败返回空集合
	 * @param tableName
	 * @return
	 * @throws SQLException 
	 */
	private List<Column> syncStruct(String tableName, Sync sync) throws SQLException{
		List<Column> columns = sqldes.getColumns(jdbcdes.getDatabase(), tableName, conndes);
		// 如果表存在，强制同步表结构为true，删除表
		if (columns.isEmpty() == false && sync.isForce()) {
			SQLRunner.executeUpdate(sqldes.dropTableSQL(tableName), conndes);
		}
		// 如果列为空或强制同步表结构，新建表
		if (columns.isEmpty() || sync.isForce()) {
			columns = sqlsrc.getColumnsPrimaryKey(jdbcsrc.getDatabase(), tableName, connsrc);
			try {
				SQLRunner.executeUpdate(sqldes.createTableSQL(tableName, columns, typeInfoMap), conndes);
			} catch (Exception e) {
				logger.error("创建表失败:tableName:{}, errmsg:{}", tableName, e.getMessage());
				return Collections.emptyList();
			}
		}
		return columns;
	}
	
	/**
	 * 获取源数据总页数
	 * @param sql
	 * @return
	 */
	private int getTotalPageNo(String sql) {
		String countSQL = SQLCloudUtils.parseCountSQL(sql);
		int total = SQLRunner.executeQuery(countSQL, Integer.class, connsrc).get(0);
		if(total < SYNC_BATCH_SIZE) {
			return 1;
		}
		int totalPN = total / SYNC_BATCH_SIZE;
		return totalPN % SYNC_BATCH_SIZE == 0 ? totalPN : totalPN + 1;
	}

}
