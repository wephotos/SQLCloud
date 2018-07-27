package cn.sql.cloud.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.entity.resp.QueryResult;
import cn.sql.cloud.entity.resp.UpdateResult;
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
		return executeQuery(sql, beanClass, conn);
	}
	/**
	 * 执行查询语句，返回多条记录
	 * @param sql
	 * @param beanClass
	 * @param conn
	 * @return
	 */
	public static <E> List<E> executeQuery(String sql, Class<E> beanClass, Connection conn){
		logger.info("executeQuery SQL -> {}", sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			return JDBCMapper.resultSet2List(rs, beanClass);
		} catch (SQLException e) {
			logger.error(e.getMessage() + ". SQL->" + sql);
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
		return executeMapQuery(sql, conn);
	}
	
	/**
	 * 执行查询语句，返回多条记录
	 * @param sql
	 * @param conn
	 * @return
	 */
	public static QueryResult executeMapQuery(String sql, Connection conn){
		logger.info("executeMapQuery SQL -> {}", sql);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			statement.setFetchSize(MAX_FETCH_SIZE);
			rs = statement.executeQuery();
			return JDBCMapper.resultSet2QueryResult(rs);
		} catch (SQLException e) {
			logger.error(e.getMessage() + ". SQL->" + sql);
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
		return executeUpdate(sql, conn);
	}
	
	/**
	 * 查询以外的操作 DML
	 * @param sql
	 * @param conn 连接
	 * @return
	 */
	public static UpdateResult executeUpdate(String sql, Connection conn) {
		logger.info("executeUpdate SQL -> {}", sql);
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			int updateCount = stmt.executeUpdate();
			return new UpdateResult().setUpdateCount(updateCount);
		} catch (SQLException e) {
			logger.error(e.getMessage() + ". SQL->" + sql);
			throw new SQLCloudException(e);
		}finally {//关闭 statement
			if(stmt != null) {
				try {stmt.close();} catch (SQLException e) {}
			}
		}
	}

}
