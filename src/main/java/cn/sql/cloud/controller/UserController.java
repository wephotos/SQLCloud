package cn.sql.cloud.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.sql.cloud.entity.User;
import cn.sql.cloud.utils.SQLCloudUtils;
import cn.sql.cloud.web.WEBUtils;

/**
 * 用户登录/登出
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/user")
public class UserController {
	//log
	private final static Logger logger = LoggerFactory.getLogger(UserController.class);
	/**
	 * 登录
	 * @param user
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(HttpSession session, User user) {
		logger.info("login -> {}",  user.toString());
		if(SQLCloudUtils.verifyUser(user)) {
			WEBUtils.setSessionUser(session, user);
			return "redirect:/";
		}else {
			return "error/login_failed";
		}
	}
	
	/**
	 * 登出
	 * @param session
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/views/login.jsp";
	}

}
