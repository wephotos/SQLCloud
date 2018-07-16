package cn.sql.cloud.sql;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.service.SQLService;

/**
 * JUnit 测试类
 * @author TQ
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"classpath:config/spring/spring-mvc.xml"})
public class SQLServiceJUnit {
	
	
	@Resource
	private SQLService sqlService;
	
	//设置数据源
	@org.junit.Before
	public void initJdbcInfo() {
		String username = "root", jdbcName = "mysql";
		JDBCInfo jdbcInfo = new JDBCInfo();
		jdbcInfo.setDatabase("sqlcloud");
		jdbcInfo.setHost("localhost");
		jdbcInfo.setName(jdbcName);
		jdbcInfo.setUsername("root");
		jdbcInfo.setPassword("root");
		jdbcInfo.setPort(3306);
		jdbcInfo.setSqlType(SQLType.MYSQL);
		JDBCManager.addJdbcInfo(jdbcInfo, username);
		JDBCManager.holderJdbcInfo(username, jdbcName);
	}

	@Test
	public void executeQuery() {
		System.out.println(sqlService.executeQuery("select * from user"));
	}
}
