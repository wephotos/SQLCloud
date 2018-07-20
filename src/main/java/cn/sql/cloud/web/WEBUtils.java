package cn.sql.cloud.web;

import javax.servlet.http.HttpSession;

import cn.sql.cloud.entity.User;

/**
 * WEB工具类
 * @author TQ
 *
 */
public class WEBUtils {
	
	/**
	 * 用户信息 Session Key
	 */
	private static final String SESSION_KEY_USER = "session:key:user";
	/**
	 * JDBC连接名 Session Key
	 */
	private static final String SESSION_KEY_JDBC_NAME = "session:key:jdbc:name";
	/**
	 * 将用户信息设置到Session中
	 * @param session
	 * @param user
	 */
	public static void setSessionUser(HttpSession session, User user) {
		session.setAttribute(SESSION_KEY_USER, user);
	}
	
	/**
	 * 获取 Session 中的用户信息
	 * @param session
	 * @return
	 */
	public static User getSessionUser(HttpSession session) {
		return (User) session.getAttribute(SESSION_KEY_USER);
	}
	
	/**
	 * 设置JDBC连接名到Session中
	 * @param session
	 * @param jdbcName
	 */
	public static void setSessionJdbcName(HttpSession session, String jdbcName) {
		session.setAttribute(SESSION_KEY_JDBC_NAME, jdbcName);
	}
	
	/**
	 * 获取 Session中的JDBC连接名
	 * @param session
	 * @return
	 */
	public static String getSessionJdbcName(HttpSession session) {
		return (String)session.getAttribute(SESSION_KEY_JDBC_NAME);
	}
	
	/**
	 * 清空Session属性并使Session失效
	 * @param session
	 */
	public static void sessionInvalidate(HttpSession session) {
		session.removeAttribute(SESSION_KEY_USER);
		session.removeAttribute(SESSION_KEY_JDBC_NAME);
	}
}
