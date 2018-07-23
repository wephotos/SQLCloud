package cn.sql.cloud.entity.resp;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 查询结果集
 * @author TQ
 *
 */
public class QueryResult implements SQLResult {
	/**
	 * 查询记录总条数
	 */
	private long total;
	/**
	 * 列名
	 */
	private List<String> columnNames = Collections.emptyList();
	/**
	 * 数据
	 */
	private List<Map<String, Object>> results = Collections.emptyList();
	
	@Override
	public String getType() {
		return QUERY;
	}
	
	public long getTotal() {
		if(total > 0) {
			return total;
		}else {
			return results.size();
		}
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	public List<String> getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	public List<Map<String, Object>> getResults() {
		return results;
	}
	public void setResults(List<Map<String, Object>> results) {
		this.results = results;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
