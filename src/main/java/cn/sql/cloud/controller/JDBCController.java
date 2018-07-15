package cn.sql.cloud.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.entity.User;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.web.WEBUtils;

/**
 * JDBC连接相关操作
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/jdbc")
public class JDBCController {

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
		boolean success = JDBCManager.addJdbcInfo(jdbc, user.getUsername());
		WEBUtils.setSessionJdbcName(session, jdbc.getName());
		return SQLResponse.build().setValue(success);
	}
	
	/**
	 * 将指定的JDBC连接信息存入当前会话
	 * @param session
	 * @param jdbcName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/holder")
	public SQLResponse holder(HttpSession session, String jdbcName) {
		WEBUtils.setSessionJdbcName(session, jdbcName);
		return SQLResponse.build();
	}
	
	/**
	 * 获取当前用户的所有JDBC连接
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getList")
	public SQLResponse getList(HttpSession session) {
		String username = WEBUtils.getSessionUser(session).getUsername();
		List<JDBCInfo> jdbcList = JDBCManager.getJdbcInfoList(username);
		return SQLResponse.build(jdbcList);
	}
}
