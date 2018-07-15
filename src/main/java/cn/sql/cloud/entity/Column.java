package cn.sql.cloud.entity;

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
	@Override
	public String toString() {
		return "Column [columnName=" + columnName + ", dataType=" + dataType + ", columnType=" + columnType
				+ ", columnComment=" + columnComment + "]";
	}
	
}
