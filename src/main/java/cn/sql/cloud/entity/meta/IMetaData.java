package cn.sql.cloud.entity.meta;

/**
 * 元数据接口;  Catalog,Schema,Table,Column...
 * @author TQ
 *
 */
public interface IMetaData {
	
	/**
	 * CATALOG
	 */
	String CATALOG = "catalog";
	/**
	 * SCHEMA
	 */
	String SCHEMA = "schema";
	/**
	 * TABLE
	 */
	String TABLE = "table";
	/**
	 * COLUMN
	 */
	String COLUMN = "column";
	/**
	 * primaryKey
	 */
	String PRIMARY_KEY = "primaryKey";
	
	/**
	 * 名称
	 * @return
	 */
	String getName();

	/**
	 * 类型
	 * @return 
	 */
	String getType();
}
