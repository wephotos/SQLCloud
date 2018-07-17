package cn.sql.cloud.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.entity.User;
import cn.sql.cloud.jdbc.JDBCManager;
import cn.sql.cloud.utils.SQLCloudConfig;
import cn.sql.cloud.utils.SQLCloudUtils;

/**
 * 过滤器
 * @author TQ
 *
 */
@WebFilter(urlPatterns="/*")
public class SQLCloudFilter implements Filter {
	//log
	static final Logger logger = LoggerFactory.getLogger(SQLCloudFilter.class);
	
	//放行路径
	private static final String[] excludeMapping = {"/resources","/views/login.jsp","/user/login"};

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)rsp;
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		String servletPath = request.getServletPath();
		
		for(String mapping:excludeMapping) {
			if(servletPath.contains(mapping)) {
				chain.doFilter(req, rsp);
				return;
			}
		}
		User user = WEBUtils.getSessionUser(session);
		
		if(user == null) {
			String xrw = request.getHeader("X-Requested-With");//X-Requested-With: XMLHttpRequest
			if ("XMLHttpRequest".equals(xrw)) {
				try {
					response.setHeader("session-status", "timeout");
					SQLResponse timeout = SQLResponse.build().setCode(502).setMessage("会话超时");
					PrintWriter writer = response.getWriter();
					writer.println(SQLCloudUtils.object2JSON(timeout));
					writer.flush();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}else {
				response.sendRedirect(request.getContextPath() + "/views/login.jsp");
			}
			return;
		}
		String jdbcName = WEBUtils.getSessionJdbcName(session);
		if(jdbcName != null) {
			JDBCManager.holderJdbcInfo(user.getUsername(), jdbcName);
		}
		chain.doFilter(request, rsp);
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		SQLCloudConfig sqlcloudConfig = SQLCloudUtils.loadSQLCloudConfig();
		ServletContext context = config.getServletContext();
		config.getServletContext().setAttribute("path", context.getContextPath());
		logger.info(sqlcloudConfig.toString());
	}

}
