package com.github.wephotos.sqlcloud.entity.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 响应数据结构
 * @author TQ
 *
 */
public class SQLResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 错误代码
	 */
	private int code = 200;
	/**
	 * 错误消息
	 */
	private String message = "OK";
	
	/**
	 * 返回值
	 */
	private Object value;

	public int getCode() {
		return code;
	}

	public SQLResponse setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public SQLResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public SQLResponse setValue(Object value) {
		this.value = value;
		return this;
	}
	
	public static SQLResponse build() {
		return new SQLResponse();
	}
	
	public static SQLResponse build(Object value) {
		return new SQLResponse().setValue(value);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
