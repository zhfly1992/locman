package com.run.locman.query.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.SmsQueryRepository;
import com.run.locman.api.query.service.SmsQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: Sms地址查询接口impl
 * @author: 张贺
 * @version: 1.0, 2018年6月26日
 */

public class SmsQueryServiceImpl implements SmsQueryService {
	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private SmsQueryRepository	smsQueryRepository;



	@Override
	public RpcResponse<String> getSmsUrl(String accessSecret) {
		logger.info(String.format("[getSmsUrl()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (accessSecret == null || StringUtils.isBlank(accessSecret)) {
				logger.error("getSmsUrl()-->error,参数为空");
				return RpcResponseBuilder.buildErrorRpcResp("错误，参数为空");
			}
			// 不论是否为空都返回，在外层判断
			String url = smsQueryRepository.getUrl(accessSecret);
			logger.info(String.format("[getSmsUrl()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询完成", url);
		} catch (Exception e) {
			logger.error("getSmsUrl()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
