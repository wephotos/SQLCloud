package cn.sql.cloud.exception;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * 统一异常处理
 * 
 * @author TQ
 *
 */
@Component
public class SQLCloudExceptionResolver extends SimpleMappingExceptionResolver {

	static final Logger logger = LoggerFactory.getLogger(SQLCloudExceptionResolver.class);
	
	public SQLCloudExceptionResolver(){
		addStatusCode("error/404", 404);
		addStatusCode("error/500", 500);
		setDefaultErrorView("error/500");
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		String xrw = request.getHeader("X-Requested-With");//X-Requested-With: XMLHttpRequest
		if ("XMLHttpRequest".equals(xrw)) {
			SQLResponse error = SQLResponse.build().setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).setMessage(ex.getMessage());
			try {
				PrintWriter writer = response.getWriter();
				writer.println(SQLCloudUtils.object2JSON(error));
				writer.flush();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			return null;
		} else {
			return super.doResolveException(request, response, handler, ex);
		}
	}
}
