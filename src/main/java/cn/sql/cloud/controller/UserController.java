package cn.sql.cloud.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.SQLResponse;
import cn.sql.cloud.entity.User;

/**
 * 用户登录/登出
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/user")
public class UserController {

	/**
	 * 登录
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/login")
	public SQLResponse login(User user) {
		
		return SQLResponse.build();
	}
	
	/**
	 * 登出
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}
}
