package cn.sql.cloud.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.entity.MapQuery;
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
	public static MapQuery executeMapQuery(String sql){
		Connection conn = JDBCManager.getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			return JDBCMapper.resultSet2MapQuery(rs);
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
	public static int executeUpdate(String sql) {
		Connection conn = JDBCManager.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			return stmt.executeUpdate();
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
