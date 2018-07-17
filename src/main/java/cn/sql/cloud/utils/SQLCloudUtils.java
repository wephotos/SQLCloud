package cn.sql.cloud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	//查询开头关键字
	public static final String SELECT = "SELECT";
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
	
	/**
	 * 从查询语句中解析出字段 如果使用  <code>*</code> 查询返回空集合
	 * @param querySQL 查询SQL
	 * @return
	 */
	public static List<String> parseColumnNames(String querySQL){
		Objects.requireNonNull(querySQL);
		List<String> columnNames = new ArrayList<String>();
		String SQL = querySQL.toUpperCase();
		if(SQL.startsWith("SELECT")) {
			int fromIndex = SQL.indexOf(" FROM ");
			String trimNames = SQL.substring(7, fromIndex).trim();
			if(trimNames.contains("*")) {
				return columnNames;
			}else {
				String[] names = trimNames.split(",");
				for(String name:names) {
					String trimName = name.trim();
					if(trimName.contains(" ")) {
						columnNames.add(trimName.split("\\s+")[1]);
					}else {
						columnNames.add(trimName);
					}
				}
			}
		}else {
			throw new SQLCloudException("querySQL 必须以 SELECT 开头. ->" + querySQL);
		}
		return columnNames;
	}
	
	/**
	 * 分割多条SQL语句
	 * @param sql
	 * @return
	 */
	public static String[] splitSQL(String sql) {
		return sql.split(";");
	}

	/**
	 * 判断SQL是不是查询语句
	 * @param sql
	 * @return
	 */
	public static boolean isQuerySQL(String sql) {
		return sql.trim().toUpperCase().startsWith(SELECT);
	}
}
