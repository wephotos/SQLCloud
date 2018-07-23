package cn.sql.cloud.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.sql.cloud.entity.resp.QueryResult;
import cn.sql.cloud.entity.resp.SQLResult;
import cn.sql.cloud.entity.resp.UpdateResult;
import cn.sql.cloud.jdbc.SQLRunner;
import cn.sql.cloud.sql.ISQL;
import cn.sql.cloud.sql.SQLManager;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * SQL业务
 * @author TQ
 *
 */
@Service("sqlService")
public class SQLService {
	
	/**
	 * 执行查询语句
	 * @param sql
	 * @return
	 */
	public QueryResult executeQuery(String sql) {
		return SQLRunner.executeMapQuery(sql);
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
				ISQL mysql = SQLManager.getSQL();
				long total = 0;
				if(SQLCloudUtils.startsWithSelect(single)) {
					String countSQL = SQLCloudUtils.parseCountSQL(single);
					total = SQLRunner.executeQuery(countSQL, Long.class).get(0);
					single = mysql.pageSQL(single, 1);
				}
				QueryResult queryResult = executeQuery(single);
				queryResult.setTotal(total);
				results.add(queryResult);
			}else {
				results.add(executeUpdate(single));
			}
		}
		return results;
	}
}
