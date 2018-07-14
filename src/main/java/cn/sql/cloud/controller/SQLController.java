package cn.sql.cloud.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.SQLResponse;


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
	
	@ResponseBody
	@RequestMapping("/listTables")
	public SQLResponse listTables(HttpSession session) {
		logger.info(new Date().toString());
		return SQLResponse.build().setValue(new Date().toString());
	}

}
