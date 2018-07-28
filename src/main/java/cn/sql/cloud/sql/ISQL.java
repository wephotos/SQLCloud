package cn.sql.cloud.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.IMetaData;
import cn.sql.cloud.entity.meta.PrimaryKey;
import cn.sql.cloud.entity.meta.Table;
import cn.sql.cloud.entity.meta.TypeInfo;

/**
 * SQL接口，提供一个统一标准，屏蔽各数据库差别
 * @author TQ
 *
 */
public interface ISQL {

	/**
	 * 获取驱动类
	 * @return
	 */
	String getDirverClass();
	
	/**
	 * 获取JDBC-URL
	 * @return
	 */
	String getURL(String host, int port, String database);
	
	/**
	 * 获取查询数据库的语句
	 * @param conn 数据库连接
	 * @return
	 * @throws SQLException 
	 */
	List<? extends IMetaData> getDatabases(Connection conn) throws SQLException;
	
	/**
	 * 获取数据库中所有的表
	 * @param database 数据库
	 * @param conn 连接信息
	 * @return
	 * @throws SQLException 
	 */
	List<Table> getTables(String database, Connection conn) throws SQLException;
	
	/**
	 * 获取表中所有的列信息
	 * @param database 数据库
	 * @param tableName 表名
	 * @param conn 连接信息
	 * @return
	 * @throws SQLException 
	 */
	List<Column> getColumns(String database, String tableName, Connection conn) throws SQLException;
	
	/**
	 * 获取主键信息
	 * @param database 数据库
	 * @param tableName 表名
	 * @param conn 连接
	 * @return
	 * @throws SQLException
	 */
	List<PrimaryKey> getPrimaryKeys(String database, String tableName, Connection conn) throws SQLException;
	
	/**
	 * 获取列信息，并在列中标记主键信息
	 * @param database
	 * @param tableName
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	default List<Column> getColumnsPrimaryKey(String database, String tableName, Connection conn) throws SQLException{
		List<Column> columns = getColumns(database, tableName, conn);
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
	
	/**
	 * 将SQL添加分页语句
	 * @param sql
	 * @param pageNo 页码 [1,2,3,...]
	 * @param pageSize 每页大小
	 * @return
	 */
	default String pageSQL(String sql, int pageNo, int pageSize) {
		throw new java.lang.UnsupportedOperationException("分页方法未实现");
	}

	/**
	 * 根据表名生成 查询语句
	 * @param tableName 表名
	 * @return select * from tableName
	 */
	default String selectSQL(String tableName) {
		return new StringBuilder("SELECT * FROM ").append(tableName).toString();
	}
	/**
	 * 创建 insert 语句
	 * @param tableName 表名
	 * @param columns 列信息
	 * @return insert 语句
	 */
	default String insertSQL(String tableName, List<Column> columns) {
		return new StringBuilder("INSERT INTO ").append(tableName).append("(")
				.append(String.join(",", columns.stream().map(c -> c.getName()).collect(Collectors.toList())))
				.append(") ").append("VALUES(").append(String.join(",", Collections.nCopies(columns.size(), "?")))
				.append(")").toString();
	}
	
	/**
	 * DDL DROP
	 * @param tableName
	 * @return
	 */
	default String dropTableSQL(String tableName) {
		return new StringBuilder("DROP TABLE ").append(tableName).toString();
	}
	
	/**
	 * DDL CREATE TABLE
	 * @param tableName 表名
	 * @param columns 列集合
	 * @param typeInfoMap 数据库类型信息
	 * @return create table 语句
	 */
	default String createTableSQL(String tableName, List<Column> columns, Map<Integer, TypeInfo> typeInfoMap) {
		StringBuilder createDDL = new StringBuilder("CREATE TABLE ").append(tableName).append("(")
				.append(String.join(",", columns.stream().map(c -> {
					StringBuilder fieldSql = new StringBuilder();
					fieldSql.append(c.getName());
					fieldSql.append(" ");
					TypeInfo typeInfo = typeInfoMap.get(c.getDataType());
					if (typeInfo == null) {
						fieldSql.append("BLOB");
					} else {
						fieldSql.append(typeInfo.getLocalTypeName());
						// [(M[,D])] [UNSIGNED] [ZEROFILL]
						String createParams = typeInfo.getCreateParams();
						if (StringUtils.isNotBlank(createParams)) {
							int M = c.getColumnSize();// 精度
							int D = c.getDecimalDigits();// 标度
							if (createParams.contains("M")) {
								if (M == 0) {
									M = typeInfo.getPrecision();
								}
								createParams = createParams.replace("M", String.valueOf(M));
							}
							if (createParams.contains(",D")) {
								if (D == 0) {
									D = typeInfo.getMinimumScale();
								}
								createParams = createParams.replace(",D", "," + D);
							}
							createParams = createParams.replaceAll("[^0-9,()]", "");
							fieldSql.append(createParams);
						}
					}
					// not null
					if (c.getNullable() == DatabaseMetaData.columnNoNulls) {
						fieldSql.append(" NOT NULL ");
					}
					// default value
					if (c.getColumnDef() != null) {
						fieldSql.append("DEFAULT ");
						fieldSql.append(c.getColumnDef());
					}
					return fieldSql.toString();
				}).collect(Collectors.toList())));
		// 添加主键约束
		List<String> pKeys = columns.stream().filter(c -> c.isPrimaryKey()).map(c -> c.getColumnName())
				.collect(Collectors.toList());
		if (pKeys.size() > 0) {
			createDDL.append(",CONSTRAINT PK_").append(String.join("_", pKeys)).append(" PRIMARY KEY (")
					.append(String.join(",", pKeys)).append(")");
		}
		return createDDL.append(")").toString();
	}
}
