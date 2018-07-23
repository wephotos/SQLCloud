package cn.sql.cloud.jdbc;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.entity.resp.QueryResult;
import cn.sql.cloud.exception.SQLCloudException;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * JDBC查询结果与实体类映射
 * 
 * @author TQ
 *
 */
public class JDBCMapper {

	static final Logger logger = LoggerFactory.getLogger(JDBCMapper.class);

	/**
	 * 将结果集转换成List
	 * 
	 * @param rs
	 * @param beanClass
	 * @return
	 */
	public static <T> List<T> resultSet2List(ResultSet rs, Class<T> beanClass) {
		try {
			List<T> beanList = new ArrayList<T>();
			while (rs.next()) {
				beanList.add(resultSet2Bean(rs, beanClass));
			}
			return beanList;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}
	}
	
	/**
	 * 将结果集转换为Map,返回的结果中包含列名
	 * @param rs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static QueryResult resultSet2QueryResult(ResultSet rs) {
		try {
			List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
			while(rs.next()) {
				results.add(resultSet2Bean(rs, LinkedHashMap.class));
			}
			List<String> columnNames = new ArrayList<String>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String[] fullName = { rsmd.getTableName(i), rsmd.getColumnName(i), rsmd.getColumnLabel(i) };
				columnNames.add(StringUtils.join(fullName, "."));
			}
			
			QueryResult queryResult = new QueryResult();
			queryResult.setResults(results);
			queryResult.setColumnNames(columnNames);
			return queryResult;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}
	}

	/**
	 * 将结果集转换成Bean
	 * 
	 * @param rs
	 * @param beanClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T resultSet2Bean(ResultSet rs, Class<T> beanClass) {
		try {
			//数字类型直接返回第一列
			if(SQLCloudUtils.isNumberClass(beanClass)) {
				Number number = (Number)rs.getObject(1);
				return (T)SQLCloudUtils.castNumberByClass(number, beanClass);
			}
			//基本类型返回第一列
			if (beanClass.isPrimitive()) {
				if(boolean.class == beanClass || Boolean.class == beanClass) {
					return (T)rs.getObject(1);
				}
				if(char.class == beanClass || Character.class == beanClass) {
					String chars = rs.getString(1);
					if(chars == null) {
						return null;
					}else if(chars.length() > 0) {
						return (T)(Object)chars.toCharArray()[0];
					}else {
						return (T)(Object)'\0';
					}
				}
			}
			T bean;
			Field[] fields = null;
			if(Map.class == beanClass) {
				bean = (T)new HashMap<Object,Object>();
			} else {
				bean = beanClass.newInstance();
				if(!(bean instanceof Map)) {
					fields = beanClass.getDeclaredFields();
				}
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = rsmd.getColumnLabel(i);
				if(bean instanceof Map) {
					Object value;
					if("[B".equals(rsmd.getColumnClassName(i))) {
						value = "byte[]";
					}else {
						value = rs.getObject(i);
					}
					String[] fullName = { rsmd.getTableName(i), rsmd.getColumnName(i), columnName };
					String key = StringUtils.join(fullName, ".");
					((Map<String, Object>) bean).put(key, value);
					continue;
				}
				for (Field field : fields) {
					String fieldName = field.getName();
					if (!columnFieldNameEq(columnName, fieldName)) {
						continue;
					}
					PropertyDescriptor descr;
					try {
						descr = new PropertyDescriptor(fieldName, beanClass);
					} catch (IntrospectionException e) {
						logger.warn(e.getMessage());
						continue;
					}
					Method writeMethod = descr.getWriteMethod();
					if(writeMethod.getParameterCount() != 1) {
						logger.error(writeMethod.getName()+"'s parameter count ne 1.");
						continue;
					}
					Class<?> parameterType = writeMethod.getParameterTypes()[0];
					Object fieldValue = getColumnValueByClass(rs, i, parameterType);
					//logger.debug("parameterType ->{}, fieldValue ->{}", parameterType, fieldValue);
					if (writeMethod != null) {
						writeMethod.invoke(bean, fieldValue);
						break;
					}
				}

			}
			return bean;
		} catch (SQLException | InstantiationException | IllegalAccessException |
				 IllegalArgumentException | InvocationTargetException e) {
			logger.error(e.getMessage());
			throw new SQLCloudException(e);
		}

	}
	
	/**
	 * 判断 列名和字段名是否相同，列名经过下划线转骆驼命名比较也算相同
	 * @param column
	 * @param field
	 * @return
	 */
	private static boolean columnFieldNameEq(String column, String field) {
		if(column.equalsIgnoreCase(field)) {
			return true;
		}
		if(SQLCloudUtils.underline2CamelCase(column).equalsIgnoreCase(field)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据类型获取ResultSet中的值
	 * @param rs
	 * @param columnIndex
	 * @param typeClass
	 * @return
	 * @throws SQLException
	 */
	private static Object getColumnValueByClass(ResultSet rs, int columnIndex, Class<?> typeClass) throws SQLException {
		if(boolean.class == typeClass || Boolean.class == typeClass) {
			return rs.getBoolean(columnIndex);
		}else if(byte.class == typeClass || Byte.class == typeClass) {
			return rs.getByte(columnIndex);
		}else if(short.class == typeClass || Short.class == typeClass) {
			return rs.getShort(columnIndex);
		}else if(int.class == typeClass || Integer.class == typeClass) {
			return rs.getInt(columnIndex);
		}else if(long.class == typeClass || Long.class == typeClass) {
			return rs.getLong(columnIndex);
		}else if(float.class == typeClass || Float.class == typeClass) {
			return rs.getFloat(columnIndex);
		}else if(double.class == typeClass || Double.class == typeClass) {
			return rs.getDouble(columnIndex);
		}else if(BigDecimal.class == typeClass) {
			return rs.getBigDecimal(columnIndex);
		}else if(char.class == typeClass || Character.class == typeClass) {
			String value = rs.getString(columnIndex);
			if(value == null) {
				return '\0';
			}
			if(value.length() > 1) {
				throw new ClassCastException("java.lang.String cannot be cast to java.lang.Character");
			}else {
				if(value.length() == 1) {
					return value.toCharArray()[0];
				}else {
					return '\0';
				}
			}
		}else if(String.class == typeClass) {
			return rs.getString(columnIndex);
		}else if(Date.class == typeClass) {
			java.sql.Date date = rs.getDate(columnIndex);
			return new Date(date.getTime());
		}else if(java.sql.Date.class == typeClass) {
			return rs.getDate(columnIndex);
		}else if(Timestamp.class == typeClass) {
			return rs.getTimestamp(columnIndex);
		}else if(Time.class == typeClass) {
			return rs.getTime(columnIndex);
		}
		return rs.getObject(columnIndex);
	}
	
}
