package com.github.wephotos.sqlcloud.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.wephotos.sqlcloud.entity.User;
import com.github.wephotos.sqlcloud.utils.SQLCloudUtils;
import com.github.wephotos.sqlcloud.web.WEBUtils;

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
		logger.info("login -> {}",  user.getUsername());
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
		User user = WEBUtils.getSessionUser(session);
		logger.info("logout -> {}", user.getUsername());
		WEBUtils.sessionInvalidate(session);
		return "redirect:/views/login.jsp";
	}

}
