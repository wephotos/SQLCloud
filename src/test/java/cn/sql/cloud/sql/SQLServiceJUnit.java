package cn.sql.cloud.sql;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.entity.Sync;
import cn.sql.cloud.exception.JDBCNotFoundException;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.service.MetaDataService;
import cn.sql.cloud.service.SQLService;
import cn.sql.cloud.service.SyncService;

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
	
	@Resource
	private MetaDataService metaDataService;
	
	@Resource
	private SyncService syncService;
	//用户名，连接名
	String username = "SQLCloud", jdbcName = "SQLCloud-MySQL";
	
	//设置数据源
	@org.junit.Before
	public void initMySQL() throws JDBCNotFoundException {
		JDBC src = new JDBC();
		src.setDatabase("sqlcloud");
		src.setHost("127.0.0.1");
		src.setName(jdbcName);
		src.setUsername("root");
		src.setPassword("root");
		src.setPort(3306);
		src.setSqlType(SQLType.MYSQL);
		JDBCManager.addJdbc(src, username);
		JDBCManager.holderJdbc(username, jdbcName);
		//目标库
		JDBC dest = new JDBC();
		dest.setDatabase("sqlcloud-bak");
		dest.setHost("127.0.0.1");
		dest.setName(jdbcName.concat("-bak"));
		dest.setUsername("root");
		dest.setPassword("root");
		dest.setPort(3306);
		dest.setSqlType(SQLType.MYSQL);
		JDBCManager.addJdbc(dest, username);
	}
	//打印
	static void println(Object value) {
		System.out.println(value);
	}
	
	@Test
	public void run() {
		Sync sync = new Sync();
		sync.setUsername(username);
		sync.setSrc(jdbcName);
		sync.setDest(jdbcName.concat("-bak"));
		sync.setForce(true);
		this.syncService.run(sync);
	}
}
