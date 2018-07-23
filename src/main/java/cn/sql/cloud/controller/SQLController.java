package cn.sql.cloud.controller;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.resp.QueryResult;
import cn.sql.cloud.entity.resp.SQLResponse;
import cn.sql.cloud.entity.resp.SQLResult;
import cn.sql.cloud.entity.resp.UpdateResult;
import cn.sql.cloud.service.SQLService;


/***
 * SQL Controller
 * @author TQ
 *
 */
@Controller
@RequestMapping(value = "/sql")
public class SQLController {

    //log
    static final Logger logger = LoggerFactory.getLogger(SQLController.class);

    @Resource
    private SQLService sqlService;

    /**
     * 执行 SELECT 语句
     *
     * @param sql
     * @return
     */
    @ResponseBody
    @RequestMapping("/executeQuery")
    public SQLResponse executeQuery(String sql) {
        logger.debug("sql/executeQuery -> {}", sql);
        QueryResult queryResult = this.sqlService.executeQuery(sql);
        return SQLResponse.build(queryResult);
    }

    /**
     * 执行DDL,DML语句
     *
     * @param sql
     * @return
     */
    @ResponseBody
    @RequestMapping("/executeUpdate")
    public SQLResponse executeUpdate(String sql) {
        logger.debug("sql/executeUpdate -> {}", sql);
        UpdateResult updateResult = this.sqlService.executeUpdate(sql);
        return SQLResponse.build(updateResult);
    }

    /**
     * 执行SQL语句
     *
     * @param sql
     * @return
     */
    @ResponseBody
    @RequestMapping("/execute")
    public SQLResponse execute(String sql) {
        Objects.requireNonNull(sql, "sql语句不能为空");
        logger.debug("sql/execute -> {}", sql);
        List<SQLResult> results = this.sqlService.execute(sql);
        return SQLResponse.build(results);
    }

}
