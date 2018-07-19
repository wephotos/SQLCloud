package cn.sql.cloud.entity;

/**
 * 数据库对象接口 Database,Table,Column...
 * @author TQ
 *
 */
public interface SQLObject {
	
	/**
	 * 数据库对象类型
	 */
	String DATABASE = "DATABASE";
	/**
	 * 表对象类型
	 */
	String TABLE = "TABLE";
	/**
	 * 列对象类型
	 */
	String COLUMN = "COLUMN";
	
	/**
	 * 数据库对象名
	 * @return
	 */
	String getName();

	/**
	 * 获取数据库对象类型
	 * @return 
	 */
	String getType();
}
