package cn.sql.cloud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.sql.cloud.entity.User;
import cn.sql.cloud.exception.SQLCloudException;

/**
 * 工具类
 * @author TQ
 *
 */
public final class SQLCloudUtils {
	
	static final Logger logger = LoggerFactory.getLogger(SQLCloudUtils.class);
	
	static final ObjectMapper objectMapper = new ObjectMapper();
	//配置文件
	private static SQLCloudConfig SQL_CLOUD_CONFIG = null;
	//下划线
	public static final String UNDERLINE = "_";
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
	
	/**
	 * 加载配置文件并返回
	 * @return
	 */
	public static SQLCloudConfig loadSQLCloudConfig() {
		if(SQL_CLOUD_CONFIG != null) {
			return SQL_CLOUD_CONFIG;
		}
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream input = currentClassLoader.getResourceAsStream("config/sqlcloud/sqlcloud.xml")) {
			String xml = IOUtils.toString(input);
			JAXBContext context = JAXBContext.newInstance(SQLCloudConfig.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			SQL_CLOUD_CONFIG = (SQLCloudConfig)unmarshaller.unmarshal(new StringReader(xml));
			return SQL_CLOUD_CONFIG;
		} catch (IOException | JAXBException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}
	}
	
	/**
	 * 根据用户名获取用户
	 * @param username
	 * @return
	 */
	public static User getUserByUsername(String username) {
		for(SQLCloudConfig.User u:SQL_CLOUD_CONFIG.getUser()) {
			if(u.getUsername().equals(username)) {
				User user = new User();
				user.setUsername(u.getUsername());
				user.setPassword(u.getPassword());
				return user;
			}
		}
		return null;
	}
	
	/**
	 * 验证用户信息
	 * @param user
	 * @return
	 */
	public static boolean verifyUser(User user) {
		User _user = getUserByUsername(user.getUsername());
		if(_user == null) {
			return false;
		}
		if(_user.getPassword().equals(md5(user.getPassword()))) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 下划线命名转骆驼命名
	 * @param name
	 * @return
	 */
	public static String underline2CamelCase(String name) {
		if(name == null) {
			throw new NullPointerException();
		}
		if(!name.contains("_")) {
			return name;
		}
		String[] words = name.split("_");
		StringBuilder camelCase = new StringBuilder();
		camelCase.append(words[0].toLowerCase());
		for(int i = 1; i < words.length; i++) {
			if(StringUtils.isNotBlank(words[i])) {
				String lowerCase = words[i].toLowerCase();
				char first = lowerCase.charAt(0);
				if(first >= 97 && first <= 122) {
					first -= 32;
				}
				camelCase.append(first);
				camelCase.append(lowerCase.substring(1));
			}
		}
		return camelCase.toString();
	}

}
