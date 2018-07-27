package cn.sql.cloud.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.resp.QueryResult;
import cn.sql.cloud.entity.resp.SQLResult;
import cn.sql.cloud.entity.resp.UpdateResult;
import cn.sql.cloud.jdbc.SQLRunner;
import cn.sql.cloud.sql.SQLManager;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * SQL业务
 * @author TQ
 *
 */
@Service("sqlService")
public class SQLService {
	//页大小
	public final static int PAGE_SIZE = 100;
	
	/**
	 * 执行查询语句
	 * @param sql
	 * @return
	 */
	public QueryResult executeQuery(String sql) {
		long total = 0;
		String querySQL = sql;
		if(SQLCloudUtils.startsWithSelect(sql)) {
			String countSQL = SQLCloudUtils.parseCountSQL(sql);
			if(countSQL != null) {
				total = SQLRunner.executeQuery(countSQL, Long.class).get(0);
				querySQL = SQLManager.getSQL().pageSQL(sql, 1, PAGE_SIZE);
			}
		}
		QueryResult queryResult = SQLRunner.executeMapQuery(querySQL);
		queryResult.setTotal(total);
		return queryResult;
	}

	/**
	 * DML:影响行数; DDL:0,-1
	 * @param sql
	 * @return
	 */
	public UpdateResult executeUpdate(String sql) {
		return SQLRunner.executeUpdate(sql);
	}
	
	/**
	 * 执行SQL，多条以分号分隔
	 * @param sql
	 * @return
	 */
	public List<SQLResult> execute(String sql) {
		List<SQLResult> results = new ArrayList<SQLResult>();
		String[] sqls = SQLCloudUtils.splitSQL(sql);
		for(String single:sqls) {
			if(StringUtils.isBlank(single)) {
				continue;
			}
			if(SQLCloudUtils.isQuerySQL(single)) {
				results.add(executeQuery(single));
			}else {
				results.add(executeUpdate(single));
			}
		}
		return results;
	}
}
