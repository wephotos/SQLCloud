package cn.sql.cloud.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import cn.sql.cloud.sql.SQLType;

/**
 * 连接信息
 * @author TQ
 *
 */
public class JDBC {
	
	/**
	 * 连接名
	 */
	private String name;
	/**
	 * 主机
	 */
	private String host;
	/**
	 * 端口
	 */
	private int port;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 数据库
	 */
	private String database;
	/**
	 * SQL类型
	 */
	private SQLType sqlType;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	
	public void setSqlType(SQLType sqlType) {
		this.sqlType = sqlType;
	}
	
	public SQLType getSqlType() {
		return sqlType;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
