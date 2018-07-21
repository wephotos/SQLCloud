package cn.sql.cloud.entity;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 表对象
 * @author TQ
 *
 */
public class Table implements MetaData {

	//表名
	private String tableName;
	//创建时间
	private Timestamp createTime;
	//更新时间
	private Timestamp updateTime;
	//表注释
	private String tableComment;
	//数据库名
	private String database;
	//表类型
	private String tableType;
	//列集合
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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getTableComment() {
		return tableComment;
	}

	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
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
