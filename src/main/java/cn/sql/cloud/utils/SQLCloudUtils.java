package cn.sql.cloud.utils;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.sql.cloud.exception.SQLCloudException;

/**
 * 工具类
 * @author TQ
 *
 */
public final class SQLCloudUtils {
	
	static final Logger logger = LoggerFactory.getLogger(SQLCloudUtils.class);
	
	static final ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 将字符串MD5
	 * @param value
	 * @return
	 */
	public static String md5(String value) {
		try {
			return DigestUtils.md5DigestAsHex(value.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e.getMessage());
		}
	}

	/**
	 * 对象转JSON
	 * @param object
	 * @return
	 */
	public static String object2JSON(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e.getMessage());
		}
	}
}
