package com.github.wephotos.sqlcloud.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wephotos.sqlcloud.entity.JDBC;
import com.github.wephotos.sqlcloud.entity.User;
import com.github.wephotos.sqlcloud.entity.vo.SQLResponse;
import com.github.wephotos.sqlcloud.service.JDBCService;
import com.github.wephotos.sqlcloud.web.WEBUtils;

/**
 * JDBC连接相关操作
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/jdbc")
public class JDBCController {
	//log
	final static Logger logger = LoggerFactory.getLogger(JDBCController.class);
	@Resource
	private JDBCService jdbcService;

	/**
	 * 添加一个JDBC连接信息
	 * @param jdbc
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/add")
	public SQLResponse add(JDBC jdbc, HttpSession session) {
		User user = WEBUtils.getSessionUser(session);
		logger.debug("add user:{},jdbc:{}",user.getUsername(),jdbc.getName());
		jdbcService.add(jdbc, user.getUsername());
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
		logger.debug("remove user:{},jdbc:{}",user.getUsername(),jdbcName);
		boolean bool = jdbcService.remove(jdbcName, user.getUsername());
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
		User user = WEBUtils.getSessionUser(session);
		logger.debug("holding user:{},jdbc:{}",user.getUsername(),jdbcName);
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
		logger.debug("list user:{}",username);
		return SQLResponse.build(jdbcService.list(username));
	}
	
	/**
	 * 使用指定数据库
	 * @param database
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/useDatabase")
	public SQLResponse useDatabase(String database, HttpSession session) {
		User user = WEBUtils.getSessionUser(session);
		logger.debug("useDatabase user:{}, database:{}",user.getUsername(), database);
		jdbcService.useDatabase(database);
		return SQLResponse.build();
	}
}
