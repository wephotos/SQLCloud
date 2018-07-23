package cn.sql.cloud.entity.resp;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 更新结果
 * @author TQ
 *
 */
public class UpdateResult implements SQLResult {
	
	/**
	 * 影响行数
	 */
	private int updateCount;

	@Override
	public String getType() {
		return UPDATE;
	}
	
	public int getUpdateCount() {
		return updateCount;
	}
	
	public UpdateResult setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}
