package cn.sql.cloud.entity;

import java.io.Serializable;

/**
 * 用户
 * @author TQ
 *
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 账户
	 */
	private String account;
	/**
	 * 密码
	 */
	private String password;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
