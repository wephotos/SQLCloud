package cn.sql.cloud.entity.meta;

/**
 * 主键
 * @author TQ
 *
 */
public class PrimaryKey implements IMetaData {
	/**
	 * 表类别（可为 null） 
	 */
	private String tableCat;
	/**
	 * 表模式（可为 null）
	 */
	private String tableSchem;
	/**
	 * 表名称
	 */
	private String tableName;
	/**
	 * 列名称
	 */
	private String columnName;
	/**
	 * 主键中的序列号（值 1 表示主键中的第一列，值 2 表示主键中的第二列）。 
	 */
	private short keySeq;
	/**
	 * 主键的名称（可为 null） 
	 */
	private String pkName;
	
	@Override
	public String getName() {
		return pkName;
	}
	@Override
	public String getType() {
		return PRIMARY_KEY;
	}
	
	public String getTableCat() {
		return tableCat;
	}
	public void setTableCat(String tableCat) {
		this.tableCat = tableCat;
	}
	public String getTableSchem() {
		return tableSchem;
	}
	public void setTableSchem(String tableSchem) {
		this.tableSchem = tableSchem;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public short getKeySeq() {
		return keySeq;
	}
	public void setKeySeq(short keySeq) {
		this.keySeq = keySeq;
	}
	public String getPkName() {
		return pkName;
	}
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

}
