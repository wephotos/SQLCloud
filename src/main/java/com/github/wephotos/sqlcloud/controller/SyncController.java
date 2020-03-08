package com.github.wephotos.sqlcloud.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.wephotos.sqlcloud.entity.Sync;
import com.github.wephotos.sqlcloud.entity.User;
import com.github.wephotos.sqlcloud.entity.vo.SQLResponse;
import com.github.wephotos.sqlcloud.service.SyncService;
import com.github.wephotos.sqlcloud.web.WEBUtils;

/**
 * 同步数据库对象接口
 * @author TQ
 *
 */
@Controller
@RequestMapping(value="/sync")
public class SyncController {
	
	@Resource
	private SyncService syncService;

	/**
	 * 执行数据同步
	 * @param sync
	 * @param session
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/run")
	public SQLResponse run(Sync sync, HttpSession session) {
		User user = WEBUtils.getSessionUser(session);
		sync.setUsername(user.getUsername());
		syncService.run(sync);
		return SQLResponse.build();
	}
}
