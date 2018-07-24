package cn.sql.cloud.sql;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.sql.cloud.entity.JDBC;
import cn.sql.cloud.entity.meta.Column;
import cn.sql.cloud.entity.meta.IMetaData;
import cn.sql.cloud.entity.meta.Table;
import cn.sql.cloud.exception.JDBCNotFoundException;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.service.MetaDataService;
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
	
	@Resource
	private MetaDataService metaDataService;
	
	//设置数据源
	@org.junit.Before
	public void initMySQL() throws JDBCNotFoundException {
		String username = "SQLCloud", jdbcName = "SQLCloud-MySQL";
		JDBC jdbcInfo = new JDBC();
		jdbcInfo.setDatabase("sqlcloud");
		jdbcInfo.setHost("127.0.0.1");
		jdbcInfo.setName(jdbcName);
		jdbcInfo.setUsername("root");
		jdbcInfo.setPassword("root");
		jdbcInfo.setPort(3306);
		jdbcInfo.setSqlType(SQLType.MYSQL);
		JDBCManager.addJdbc(jdbcInfo, username);
		JDBCManager.holderJdbc(username, jdbcName);
	}

	@Test
	public void getDatabases() throws SQLException {
		List<? extends IMetaData> list = metaDataService.topTreeNodes();
		for(IMetaData database:list) {
			System.out.println(database.getName());
		}
	}
	
	@Test
	public void getTables() throws SQLException {
		List<Table> list = metaDataService.tables("%");
		for(Table item:list) {
			System.out.println(item);
		}
	}
	
	@Test
	public void getColumns() throws SQLException {
		List<Column> list = metaDataService.columns("jchc_supplier", "b_jchc_supplier_baseinfo");
		for(Column item:list) {
			System.out.println(item);
		}
	}
	
	//打印
	static void println(Object value) {
		System.out.println(value);
	}
}
