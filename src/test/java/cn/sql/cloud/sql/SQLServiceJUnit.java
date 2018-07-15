package cn.sql.cloud.sql;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

	@Test
	public void tables() {
		System.out.println(sqlService.tables());
	}
}
