package com.github.wephotos.sqlcloud.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 同步信息类
 * @author TQ
 *
 */
public class Sync {
	
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 来源 - 连接名
	 */
	private String src;
	/**
	 * 目标 - 连接名
	 */
	private String dest;
	
	/**
	 * true -> 删除已存在的对象
	 * false -> 同步不存在的对象和数据
	 */
	private boolean force = false;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	public boolean isForce() {
		return force;
	}
	public void setForce(boolean force) {
		this.force = force;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
