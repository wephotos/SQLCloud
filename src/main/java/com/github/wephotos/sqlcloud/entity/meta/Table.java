package com.github.wephotos.sqlcloud.entity.meta;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 表对象
 * @author TQ
 *
 */
public class Table implements IMetaData {
	
	/**
	 * 表类别
	 */
	private String tableCat;
	/**
	 * 表模式
	 */
	private String tableSchem;
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 表类型。典型的类型是 "TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"。 
	 */
	private String tableType;
	/**
	 * 注释
	 */
	private String remarks;
	/**
	 * 表下的列
	 */
	private List<Column> columns = Collections.emptyList();
	
	@Override
	public String getType() {
		return TABLE;
	}
	@Override
	public String getName() {
		return tableName;
	}
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
