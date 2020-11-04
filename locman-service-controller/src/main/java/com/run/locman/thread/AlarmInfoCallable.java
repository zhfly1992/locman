/*
 * File name: MyCallable.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年11月16日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.thread;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:线程实现类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年11月16日
 */
public class AlarmInfoCallable implements Callable<List<Map<String, Object>>> {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	private AlarmInfoQueryService	alarmInfoQueryService;

	private JSONObject				jsonObject;

	private int						pageNo;



	public AlarmInfoCallable(JSONObject jsonObject, int pageNo, AlarmInfoQueryService alarmInfoQueryService) {
		this.pageNo = pageNo;
		this.jsonObject = jsonObject;
		this.alarmInfoQueryService = alarmInfoQueryService;
	}



	/**
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public List<Map<String, Object>> call() throws Exception {
		RpcResponse<PageInfo<Map<String, Object>>> res = alarmInfoQueryService.statisticsAlarmInfo(jsonObject,
				String.valueOf(pageNo));
		if (!res.isSuccess()) {
			logger.error(String.format("[statisticsAlarmInfo()->error:%s]", res.getMessage()));
			return null;
		} else {
			logger.info(String.format("[statisticsAlarmInfo()->success:%s]", res.getMessage()));
			return res.getSuccessValue().getList();
		}

	}

}
