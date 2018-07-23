package cn.sql.cloud.entity.meta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Schema
 * @author TQ
 *
 */
public class Schema implements IMetaData {
	
	/**
	 * 名称
	 */
	private String name;
	
	@Override
	public String getType() {
		return SCHEMA;
	}
	
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
