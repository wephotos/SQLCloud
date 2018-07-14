package cn.sql.cloud.sql;

/**
 * SQL管理类
 * @author TQ
 *
 */
public final class SQLManager {

	/**
	 * 当前线程使用的SQL
	 */
	private static final ThreadLocal<SQLTypes> THREAD_LOCAL_SQL = new ThreadLocal<SQLTypes>();
	
	/**
	 * 设置SQLType
	 * @param sqlType
	 */
	public static void setSQLType(SQLTypes sqlType) {
		THREAD_LOCAL_SQL.remove();
		THREAD_LOCAL_SQL.set(sqlType);
	}
	/**
	 * 获取 SQLType
	 * @return
	 */
	public static SQLTypes getSQLType() {
		return THREAD_LOCAL_SQL.get();
	}
	
	/**
	 * 获取当前的SQL对象
	 * @return
	 */
	public static ISQL getSQL() {
		SQLTypes sqlType = getSQLType();
		if(sqlType == null) {
			throw new NullPointerException("sqlType can't be null.");
		}else {
			return getSQL(sqlType);
		}
	}
	
	/**
	 * 根据SQLTypes获取SQL对象
	 * @param sqlType
	 * @return
	 */
	public static ISQL getSQL(SQLTypes sqlType) {
		if(SQLTypes.ORACLE == sqlType) {
			return SQLOracle.build();
		}else if(SQLTypes.MYSQL == sqlType) {
			return SQLMysql.build();
		}else {
			throw new IllegalArgumentException(SQLManager.class.getName()+".getSQL(?) ->" + sqlType);
		}
	}
}
