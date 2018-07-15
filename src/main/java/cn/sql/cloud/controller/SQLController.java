package cn.sql.cloud.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.sql.SQLService;


/***
 * SQL Controller
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/sql")
public class SQLController {
	
	//log
	static final Logger logger = LoggerFactory.getLogger(SQLController.class);
	
	@Resource
	private SQLService sqlService;
	
	/**
	 * 获取数据库中的所有表
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/tables")
	public SQLResponse tables(HttpSession session) {
		logger.debug("tables()");
		return SQLResponse.build(this.sqlService.tables());
	}

	/**
	 * 获取表的列信息
	 * @param session
	 * @param tableName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/columns")
	public SQLResponse columns(HttpSession session, String tableName) {
		logger.debug("columns()");
		return SQLResponse.build(this.sqlService.columns(tableName));
	}
}
