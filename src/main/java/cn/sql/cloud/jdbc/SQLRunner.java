package cn.sql.cloud.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.entity.QueryResult;
import cn.sql.cloud.entity.UpdateResult;
import cn.sql.cloud.exception.SQLCloudException;

/**
 * SQL执行类
 * 
 * @author TQ
 *
 */
public class SQLRunner {
	//log
	private static final Logger logger = LoggerFactory.getLogger(SQLRunner.class);
	//指示从数据库中提取最大行数
	public static final int MAX_FETCH_SIZE = 100;
	/**
	 * 执行查询语句，返回多条记录
	 * @param sql
	 * @param beanClass
	 * @return
	 */
	public static <E> List<E> executeQuery(String sql, Class<E> beanClass){
		Connection conn = JDBCManager.getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			return JDBCMapper.resultSet2List(rs, beanClass);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}finally {//关闭 ResultSet,statement
			if(rs != null) {
				try {rs.close();} catch (SQLException e) {}
			}
			if(statement != null) {
				try {statement.close();} catch (SQLException e) {}
			}
		}
	}
	
	/**
	 * 执行查询语句，返回多条记录
	 * @param sql
	 * @param beanClass
	 * @return
	 */
	public static QueryResult executeMapQuery(String sql){
		Connection conn = JDBCManager.getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rs.setFetchSize(MAX_FETCH_SIZE);
			return JDBCMapper.resultSet2QueryResult(rs);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}finally {//关闭 ResultSet,statement
			if(rs != null) {
				try {rs.close();} catch (SQLException e) {}
			}
			if(statement != null) {
				try {statement.close();} catch (SQLException e) {}
			}
		}
	}
	
	/**
	 * 查询以外的操作 DML
	 * @param sql
	 * @return
	 */
	public static UpdateResult executeUpdate(String sql) {
		Connection conn = JDBCManager.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			int updateCount = stmt.executeUpdate();
			return new UpdateResult().setUpdateCount(updateCount);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}finally {//关闭 statement
			if(stmt != null) {
				try {stmt.close();} catch (SQLException e) {}
			}
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	@Deprecated //暂时不要用
	public static boolean execute(String sql) {
		Connection conn = JDBCManager.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			return stmt.execute();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}finally {//关闭 statement
			if(stmt != null) {
				try {stmt.close();} catch (SQLException e) {}
			}
		}
	}
}
