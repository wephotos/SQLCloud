package cn.sql.cloud.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 列对象
 * @author TQ
 *
 */
public class Column {

	//列名
	private String columnName;
	//数据类型
	private String dataType;
	//列类型
	private String columnType;
	//列注释
	private String columnComment;
	//可空?
	private boolean nullable;
	//主键?
	private boolean keyPrimary;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getColumnComment() {
		return columnComment;
	}
	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public boolean isKeyPrimary() {
		return keyPrimary;
	}
	public void setKeyPrimary(boolean keyPrimary) {
		this.keyPrimary = keyPrimary;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}
