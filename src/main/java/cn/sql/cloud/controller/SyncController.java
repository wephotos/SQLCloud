package cn.sql.cloud.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.sql.cloud.entity.Sync;
import cn.sql.cloud.entity.resp.SQLResponse;
import cn.sql.cloud.service.SyncService;

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

	@ResponseBody
    @RequestMapping("/run")
	public SQLResponse run(Sync sync) {
		syncService.run(sync);
		return SQLResponse.build();
	}
}
