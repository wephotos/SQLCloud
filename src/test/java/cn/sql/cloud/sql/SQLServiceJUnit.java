package cn.sql.cloud.sql;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.sql.cloud.entity.JDBCInfo;
import cn.sql.cloud.entity.QueryResult;
import cn.sql.cloud.entity.UpdateResult;
import cn.sql.cloud.exception.JDBCNotFoundException;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.jdbc.SQLRunner;
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
	public void initJdbcInfo() throws JDBCNotFoundException {
		String username = "TianQi", jdbcName = "mysql";
		JDBCInfo jdbcInfo = new JDBCInfo();
		jdbcInfo.setDatabase("jchc_supplier");
		jdbcInfo.setHost("172.20.1.199");
		jdbcInfo.setName(jdbcName);
		jdbcInfo.setUsername("jchctest");
		jdbcInfo.setPassword("Jchc20170321");
		jdbcInfo.setPort(3306);
		jdbcInfo.setSqlType(SQLType.MYSQL);
		JDBCManager.addJdbcInfo(jdbcInfo, username);
		JDBCManager.holderJdbcInfo(username, jdbcName);
	}

	@Test
	public void executeQuery() {
		//show databases
		System.err.println("=======================================");
		UpdateResult updateResult = SQLRunner.executeUpdate("use jchc_oa");
		System.out.println(updateResult);
		QueryResult queryResult = SQLRunner.executeMapQuery("select * from b_jchc_file");
		System.err.println(queryResult);
		System.err.println("=======================================");
	}

}
