package cn.sql.cloud.entity;

/**
 * 执行SQL的返回结果
 * @author Administrator
 *
 */
public interface SQLResult {
	
	/**
	 * 查询类型
	 */
	String QUERY = "query";
	/**
	 * 更新类型
	 */
	String UPDATE = "update";

	/**
	 * 获取返回结果类型,分为查询和更新
	 * @return {@link #QUERY} {@link #UPDATE}
	 */
	String getType();
}
