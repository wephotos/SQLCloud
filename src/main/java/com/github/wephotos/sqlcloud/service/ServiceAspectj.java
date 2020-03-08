package com.github.wephotos.sqlcloud.service;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wephotos.sqlcloud.jdbc.JDBCManager;

/**
 * Service AOP
 * @author TQ
 *
 */
@Aspect
public class ServiceAspectj {
	//log
	final static Logger logger = LoggerFactory.getLogger(ServiceAspectj.class);

	/**
	 * 名字以Service结束类下的所有方法
	 */
	@Pointcut("execution(* cn.sql.cloud.service.*Service.*(..))")
	public void service() {
		
	}
	
	/**
	 * 在方法后关闭连接
	 */
	@After("service()")
	public void after() {
		JDBCManager.closeConnection();
	}
}
