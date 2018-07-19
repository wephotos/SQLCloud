package cn.sql.cloud.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.entity.User;
import cn.sql.cloud.service.JDBCInfoService;
import cn.sql.cloud.web.WEBUtils;

/**
 * JDBC连接相关操作
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/jdbc")
public class JDBCController {
	
	@Resource
	private JDBCInfoService jdbcInfoService;

	/**
	 * 添加一个JDBC连接信息
	 * @param jdbc
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/add")
	public SQLResponse add(JDBCInfo jdbc, HttpSession session) {
		User user = WEBUtils.getSessionUser(session);
		jdbcInfoService.add(jdbc, user.getUsername());
		WEBUtils.setSessionJdbcName(session, jdbc.getName());
		return SQLResponse.build();
	}
	
	/**
	 * 移除数据库连接
	 * @param session 会话
	 * @param jdbcName JDBC名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/remove")
	public SQLResponse remove(HttpSession session, String jdbcName) {
		User user = WEBUtils.getSessionUser(session);
		boolean bool = jdbcInfoService.remove(jdbcName, user.getUsername());
		return SQLResponse.build(bool);
	}
	
	/**
	 * 将指定的JDBC连接信息存入当前会话
	 * @param session
	 * @param jdbcName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/holding")
	public SQLResponse holding(HttpSession session, String jdbcName) {
		WEBUtils.setSessionJdbcName(session, jdbcName);
		return SQLResponse.build();
	}
	
	/**
	 * 获取当前用户的所有JDBC连接
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/list")
	public SQLResponse list(HttpSession session) {
		String username = WEBUtils.getSessionUser(session).getUsername();
		return SQLResponse.build(jdbcInfoService.list(username));
	}
	
	/**
	 * 使用指定数据库
	 * @param database
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/useDatabase")
	public SQLResponse useDatabase(String database) {
		jdbcInfoService.useDatabase(database);
		return SQLResponse.build();
	}
}
