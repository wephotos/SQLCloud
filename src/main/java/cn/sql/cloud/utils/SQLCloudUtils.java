package cn.sql.cloud.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.StringUtils;

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
	//SQL SELECT
	public static final String SQL_SELECT = "SELECT";
	//SQL FROM
	public static final String SQL_FROM = "FROM";
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
			if(!StringUtils.isEmptyOrWhitespaceOnly(words[i])) {
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
	 * 转换数字类型
	 * @param number 原始类型
	 * @param tc 目标类型
	 * @return
	 */
	public static Number castNumberByClass(Number number, Class<?> tc) {
		if(Byte.class == tc || byte.class == tc) {
			return number.byteValue();
		}
		if(Short.class == tc || short.class == tc) {
			return number.shortValue();
		}
		if(Integer.class == tc || int.class == tc) {
			return number.intValue();
		}
		if(Long.class == tc || long.class == tc) {
			return number.longValue();
		}
		if(Float.class == tc || float.class == tc) {
			return number.floatValue();
		}
		if(Double.class == tc || double.class == tc) {
			return number.doubleValue();
		}
		throw new ClassCastException("java.lang.Number Cannot cast " + tc.getName());
	}
	
	/**
	 * 判断是否是数字类型
	 * @param cls
	 * @return
	 */
	public static boolean isNumberClass(Class<?> cls) {
		return (Byte.class == cls || byte.class == cls || Short.class == cls || short.class == cls
				|| Integer.class == cls || int.class == cls || Long.class == cls || long.class == cls
				|| Float.class == cls || float.class == cls || Double.class == cls || double.class == cls);
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
		String noCommentSql = stripComments(sql);
		return StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, new String[] {"S","EXPLAIN"}) > -1;
	}
	
	/**
	 * 判断SQL是否以SELECT开头
	 * @param sql
	 * @return
	 */
	public static boolean startsWithSelect(String sql) {
		return StringUtils.startsWithIgnoreCaseAndWs(stripComments(sql), "SELECT");
	}
	
	/**
	 * 移除SQL中的注释
	 * @param sql
	 * @return
	 */
	private static String stripComments(String sql) {
		return StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
	}
	
	/**
	 * 将查询SQL解析成 count(1) .<br>
	 * select column1,column2,... from table,... select count(1) from table,...
	 * @param sql
	 * @return
	 */
	public static String parseCountSQL(String sql) {
		Stack<String> selectStack = new Stack<String>();
		char[] digits = sql.toCharArray();
		int fromIndex = 0;
		for (int i = 0; i < digits.length; i++) {
			char digit = digits[i];
			if (Character.isWhitespace(digit) || digit == ',' || digit == '(' || digit == ')') {
				String word = sql.substring(fromIndex, i);
				if (SQL_SELECT.equalsIgnoreCase(word)) {
					selectStack.push(word);
				}else if (SQL_FROM.equalsIgnoreCase(word)) {
					if (selectStack.size() == 1) {
						break;
					} else if (selectStack.size() > 1) {
						selectStack.pop();
					} else {
						throw new SQLCloudException("SQL语法错误 ->" + sql);
					}
				}
				fromIndex = i + 1;
			}
		}
		return SQL_SELECT.concat(" COUNT(1) TOTAL ").concat(sql.substring(fromIndex));
	}

}
