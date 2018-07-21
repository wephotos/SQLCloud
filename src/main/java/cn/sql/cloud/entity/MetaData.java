package cn.sql.cloud.entity;

/**
 * 元数据接口;  Database,Table,Column...
 * @author TQ
 *
 */
public interface MetaData {
	
	/**
	 * 数据库对象类型
	 */
	String DATABASE = "database";
	/**
	 * 表对象类型
	 */
	String TABLE = "table";
	/**
	 * 列对象类型
	 */
	String COLUMN = "column";
	
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
