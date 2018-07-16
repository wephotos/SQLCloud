package cn.sql.cloud.sql;

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
		return "oracle.jdbc.driver.OracleDriver";
	}

	@Override
	public String getURL(String host, int port, String database) {
		return "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
	}

	@Override
	public String getSQLTables(String database) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSQLColumns(String database, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
