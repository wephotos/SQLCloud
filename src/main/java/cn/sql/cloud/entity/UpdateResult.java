package cn.sql.cloud.entity;

/**
 * 更新结果
 * @author Administrator
 *
 */
public class UpdateResult implements SQLResult {
	
	/**
	 * 影响行数
	 */
	private int updateCount;
	
	public int getUpdateCount() {
		return updateCount;
	}
	
	public UpdateResult setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
		return this;
	}

	@Override
	public String getType() {
		return UPDATE;
	}

}
