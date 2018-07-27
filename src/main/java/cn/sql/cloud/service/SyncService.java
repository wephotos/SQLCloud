package cn.sql.cloud.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.entity.Sync;
import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.TypeInfo;
import cn.sql.cloud.entity.meta.Table;
import cn.sql.cloud.exception.SQLCloudException;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.jdbc.JDBCMapper;
import cn.sql.cloud.jdbc.SQLRunner;
import cn.sql.cloud.sql.ISQL;
import cn.sql.cloud.sql.SQLManager;

/**
 * 数据库同步 Service
 * 
 * @author TQ
 *
 */
@Service("syncService")
public class SyncService {
	// log
	final static Logger logger = LoggerFactory.getLogger(SyncService.class);
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
	 * 目标连接的数据类型信息
	 */
	private List<TypeInfo> typeInfosDest = Collections.emptyList();

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
				typeInfosDest = JDBCMapper.resultSet2List(rs, TypeInfo.class);
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
		typeInfosDest = null;
		connsrc = null;
		conndes = null;
	}

	/**
	 * 同步数据库对象
	 * 
	 * @param sync
	 *            数据库同步信息
	 */
	public void run(Sync sync) {
		initConn(sync);
		try {
			List<Table> tables = sqlsrc.getTables(jdbcsrc.getDatabase(), connsrc);
			for (Table table : tables) {
				String tableName = table.getName();

				List<Column> columns = sqldes.getColumns(jdbcdes.getDatabase(), tableName, conndes);
				// 如果表存在，强制同步表结构为true，删除表
				if (columns.isEmpty() == false && sync.isForce()) {
					// drop table
					SQLRunner.executeUpdate(dropTableSQL(tableName));
				}
				// 如果列为空或强制同步表结构，新建表
				if (columns.isEmpty() || sync.isForce()) {
					columns = sqlsrc.getColumns(jdbcsrc.getDatabase(), tableName, connsrc);
					// 创建表
					try {
						SQLRunner.executeUpdate(createTableSQL(tableName, columns));
					} catch (Exception e) {
						logger.error("创建表失败:tableName:{}, errmsg:{}", tableName, e.getMessage());
					}
				}
				String insertSQL = insertSQL(tableName, columns);
				try (PreparedStatement prest = conndes.prepareStatement(insertSQL)) {
					String selectTable = selectSQL(tableName);
					try (Statement stmt = connsrc.createStatement()) {
						try (ResultSet rs = stmt.executeQuery(selectTable)) {
							while (rs.next()) {
								for (int i = 1, l = columns.size(); i <= l; i++) {
									String columnName = columns.get(i).getName();
									prest.setObject(i, rs.getObject(columnName));
								}
								prest.addBatch();
								prest.executeBatch();
								conndes.commit();
							}
						}
					}
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
	 * 根据jdbcType获取数据库中类型信息
	 * @param jdbcType
	 * @return
	 */
	TypeInfo getTypeInfoByJdbcType(int jdbcType) {
		for(TypeInfo typeInfo:typeInfosDest) {
			if(typeInfo.getDataType() == jdbcType) {
				return typeInfo;
			}
		}
		return null;
	}

	/**
	 * 根据表名生成 查询语句
	 * @param tableName 表名
	 * @return select * from tableName
	 */
	String selectSQL(String tableName) {
		String selectSQL = new StringBuilder("SELECT * FROM ").append(tableName).toString();
		logger.debug("selectSQL:{}", selectSQL);
		return selectSQL;
	}
	
	/**
	 * 删除表
	 * @param tableName
	 * @return
	 */
	String dropTableSQL(String tableName) {
		String dropTableSQL = new StringBuilder("DROP TABLE ").append(tableName).toString();
		logger.debug("dropTableSQL:{}", dropTableSQL);
		return dropTableSQL;
	}

	/**
	 * 构建 create table 语句
	 * @param tableName 表名
	 * @param columns 列集合
	 * @return create table 语句
	 */
	String createTableSQL(String tableName, List<Column> columns) {
		String createTableSQL = new StringBuilder("CREATE TABLE ").append(tableName).append("(")
				.append(String.join(",", columns.stream().map(c -> {
					StringBuilder fieldSql = new StringBuilder();
					fieldSql.append(c.getName());
					fieldSql.append(" ");
					TypeInfo typeInfo = getTypeInfoByJdbcType(c.getDataType());
					if (typeInfo == null) {
						fieldSql.append("BLOB");
					} else {
						fieldSql.append(typeInfo.getTypeName());
						// [(M[,D])] [UNSIGNED] [ZEROFILL]
						String createParams = typeInfo.getCreateParams();
						if (StringUtils.isNotBlank(createParams)) {
							int m = c.getColumnSize();// 精度
							int s = c.getDecimalDigits();// 标度
							if (createParams.contains("M")) {
								if (m == 0) {
									m = typeInfo.getPrecision();
								}
								createParams = createParams.replace("M", String.valueOf(m));
							}
							if (createParams.contains("D")) {
								if (s == 0) {
									s = typeInfo.getMinimumScale();
								}
								createParams = createParams.replace("D", String.valueOf(s));
							}
							createParams = createParams.replaceAll("[^0-9,()]", "");
							fieldSql.append(createParams);
						}
					}

					return fieldSql.toString();
				}).collect(Collectors.toList()))).append(")").toString();
		logger.debug("createTableSQL:{}", createTableSQL);
		return createTableSQL;
	}

	/**
	 * 创建 insert 语句
	 * @param tableName 表名
	 * @param columns 列信息
	 * @return insert 语句
	 */
	String insertSQL(String tableName, List<Column> columns) {
		String insertSQL = new StringBuilder("INSERT INTO ").append(tableName).append("(")
				.append(String.join(",", columns.stream().map(c -> c.getName()).collect(Collectors.toList())))
				.append(") ").append("VALUES(").append(String.join(",", Collections.nCopies(columns.size(), "?")))
				.append(")").toString();
		logger.debug("insertSQL:{}", insertSQL);
		return insertSQL;
	}

}
