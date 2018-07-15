package cn.sql.cloud.sql;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.jdbc.JDBCManager;

/**
 * Service AOP
 * @author TQ
 *
 */
@Aspect
public class ServiceAspectj {
	//log
	private final static Logger logger = LoggerFactory.getLogger(ServiceAspectj.class);

	/**
	 * 名字以Service结束类下的所有方法
	 */
	@Pointcut("execution(* cn.sql.cloud.sql.*Service.*(..))")
	public void service() {
		
	}
	
	@After("service()")
	public void after() {
		JDBCManager.closeConnection();
		logger.info("closed jdbc Connection.");
	}
}
