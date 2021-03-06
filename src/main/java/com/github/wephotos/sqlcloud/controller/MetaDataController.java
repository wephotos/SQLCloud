package com.github.wephotos.sqlcloud.controller;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wephotos.sqlcloud.entity.vo.SQLResponse;
import com.github.wephotos.sqlcloud.service.MetaDataService;

/**
 * 元数据接口，数据库，表，字段，索引等信息
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/meta")
public class MetaDataController {
	//log
	final static Logger logger = LoggerFactory.getLogger(MetaDataController.class);
		
	@Resource
	private MetaDataService metaDataService;
	
    
    /**
     * 获取SQL对象树
     * @param name
     * @param type
     * @return
     * @throws SQLException 
     */
    @ResponseBody
    @RequestMapping("/topTreeNodes")
    public SQLResponse topTreeNodes() throws SQLException {
    	logger.debug("sql/topTreeNodes");
    	return SQLResponse.build(this.metaDataService.topTreeNodes());
    }
    /**
     * 获取数据库中的所有表
     * @param database 数据库名
     * @return
     * @throws SQLException 
     */
    @ResponseBody
    @RequestMapping("/tables")
    public SQLResponse tables(String database) throws SQLException {
        logger.debug("sql/tables");
        return SQLResponse.build(this.metaDataService.tables(database));
    }

    /**
     * 获取表的列信息
     *
     * @param database 数据库名
     * @param tableName 表名
     * @return
     * @throws SQLException 
     */
    @ResponseBody
    @RequestMapping("/columns")
    public SQLResponse columns(String database, String tableName) throws SQLException {
        logger.debug("sql/columns tableName -> {}", tableName);
        return SQLResponse.build(this.metaDataService.columns(database, tableName));
    }
    
    /**
	 * 获取此数据库受支持的数据类型信息
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/getTypeInfo")
	public SQLResponse getTypeInfo(){
    	return SQLResponse.build(metaDataService.getTypeInfo());
	}
}
