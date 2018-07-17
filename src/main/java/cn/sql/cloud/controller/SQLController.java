package cn.sql.cloud.controller;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.QueryResult;
import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.entity.SQLResult;
import cn.sql.cloud.entity.UpdateResult;
import cn.sql.cloud.service.SQLService;


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
		logger.debug("sql/tables");
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
		logger.debug("sql/columns tableName -> {}", tableName);
		return SQLResponse.build(this.sqlService.columns(tableName));
	}
	
	
	/**
	 * 执行 SELECT 语句
	 * @param sql
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/executeQuery")
	public SQLResponse executeQuery(String sql) {
		logger.debug("sql/executeQuery -> {}", sql);
		QueryResult queryResult = this.sqlService.executeQuery(sql);
		return SQLResponse.build(queryResult);
	}
	
	/**
	 * 执行DDL,DML语句
	 * @param sql
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/executeUpdate")
	public SQLResponse executeUpdate(String sql) {
		logger.debug("sql/ executeUpdate -> {}", sql);
		UpdateResult updateResult = this.sqlService.executeUpdate(sql);
		return SQLResponse.build(updateResult);
	}
	
	/**
	 * 执行SQL语句
	 * @param sql
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/execute")
	public SQLResponse execute(String sql) {
		Objects.requireNonNull(sql, "sql语句不能为空");
		logger.debug("sql/execute -> {}", sql);
		List<SQLResult> results = this.sqlService.execute(sql);
		return SQLResponse.build(results);
	}
}
