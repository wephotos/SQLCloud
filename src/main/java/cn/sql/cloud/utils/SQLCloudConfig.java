package cn.sql.cloud.utils;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 项目配置文件
 * @author TQ
 *
 */
@XmlRootElement(name="sqlcloud")
@XmlAccessorType(XmlAccessType.FIELD)
public class SQLCloudConfig {

	@XmlElement(name="user")
	private User[] user = {};
	
	public User[] getUser() {
		return user;
	}
	
	public void setUser(User[] user) {
		this.user = user;
	}
	/**
	 * 用户对象
	 * @author TQ
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class User {
		@XmlAttribute(name="username")
		private String username = "root";
		@XmlAttribute(name="password")
		private String password = "windows";
		
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
		@Override
		public String toString() {
			return "User [username=" + username + ", password=" + password + "]";
		}
		
	}
	
	
	@Override
	public String toString() {
		return "SQLCloudConfig [user=" + Arrays.toString(user) + "]";
	}

}
