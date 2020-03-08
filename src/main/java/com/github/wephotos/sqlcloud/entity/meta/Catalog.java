package com.github.wephotos.sqlcloud.entity.meta;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 数据库对象
 * @author TQ
 *
 */
public class Catalog implements IMetaData {

	/**
	 * 类别名称
	 */
	private String tableCat;

	@Override
	public String getType() {
		return CATALOG;
	}
	
	public String getName() {
		return tableCat;
	}
	
	public String getTableCat() {
		return tableCat;
	}
	
	public void setTableCat(String tableCat) {
		this.tableCat = tableCat;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
