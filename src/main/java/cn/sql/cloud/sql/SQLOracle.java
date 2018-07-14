package cn.sql.cloud.sql;

import oracle.jdbc.driver.OracleDriver;

/**
 * Oracle
 * 
 * @author TQ
 *
 */
public class SQLOracle implements ISQL {

	private SQLOracle() {

	}

	static class InstanceProvider {
		static SQLOracle oracle = new SQLOracle();
	}

	public static SQLOracle build() {
		return InstanceProvider.oracle;
	}

	@Override
	public String getDirverClass() {
		return OracleDriver.class.getName();
	}

	@Override
	public String getJdbcURL(String host, int port, String database) {
		return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
	}

}
