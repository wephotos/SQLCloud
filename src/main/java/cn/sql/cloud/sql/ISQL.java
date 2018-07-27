package cn.sql.cloud.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.IMetaData;
import cn.sql.cloud.entity.meta.PrimaryKey;
import cn.sql.cloud.entity.meta.Table;

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

}
