package com.run.locman.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.OpenRecordQueryDto;
import com.run.locman.api.query.repository.OpenRecordQueryRepository;
import com.run.locman.api.query.service.OpenRecordQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OpenRecordConstants;

/**
 * 
 * @Description: 开锁记录统计实现类
 * @author: zhanghe
 * @version: 1.0, 2018年8月15日
 */
public class OpenRecordQueryServiceImpl implements OpenRecordQueryService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private OpenRecordQueryRepository	openRecordQueryRepository;



	@Override
	public RpcResponse<PageInfo<OpenRecordQueryDto>> getOpenRecordByPage(JSONObject jsonObject) {
		logger.info(String.format("[rpc-getOpenRecordByPage()->request parm:%s]", jsonObject));
		try {
			if (jsonObject == null) {
				logger.error("[getOpenRecordBypage()->invalid：参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空");
			}
			if (!jsonObject.containsKey(CommonConstants.PAGE_SIZE)) {
				logger.error("[getOpenRecordBypage()->invalid：必须包含页面大小！]");
				return RpcResponseBuilder.buildErrorRpcResp("必须包含页面大小");
			}
			if (!StringUtils.isNumeric(jsonObject.getString(CommonConstants.PAGE_SIZE))) {
				logger.error("[getOpenRecordBypage()->invalid：页面大小必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页面大小必须为数字！");
			}
			if (!jsonObject.containsKey(CommonConstants.PAGE_NUM)) {
				logger.error("[getOpenRecordBypage()->invalid：必须包含页码！]");
				return RpcResponseBuilder.buildErrorRpcResp("必须包含页码");

			}
			if (!StringUtils.isNumeric(jsonObject.getString(CommonConstants.PAGE_NUM))) {
				logger.error("[getOpenRecordBypage()->invalid：页码必须为数字！]");
				return RpcResponseBuilder.buildErrorRpcResp("页码必须为数字！");
			}
			if (!jsonObject.containsKey(OpenRecordConstants.ACCESSSECRET)
					|| StringUtils.isBlank(jsonObject.getString(OpenRecordConstants.ACCESSSECRET))) {
				logger.error("[getAlarmInfoBypage()->invalid：接入方密钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}

			// 封装查询条件
			Map<String, Object> searchMap = new HashMap<>(16);
			searchMap.put(OpenRecordConstants.ACCESSSECRET, jsonObject.getString(OpenRecordConstants.ACCESSSECRET));
			searchMap.put(OpenRecordConstants.FACILITIES_TYPE_ID,
					jsonObject.getString(OpenRecordConstants.FACILITIES_TYPE_ID));
			searchMap.put(OpenRecordConstants.START_TIME, jsonObject.getString(OpenRecordConstants.START_TIME));
			searchMap.put(OpenRecordConstants.END_TIME, jsonObject.getString(OpenRecordConstants.END_TIME));
			searchMap.put(OpenRecordConstants.SEARCH_KEY, jsonObject.get(OpenRecordConstants.SEARCH_KEY));

			PageHelper.startPage(jsonObject.getIntValue(CommonConstants.PAGE_NUM),
					jsonObject.getIntValue(CommonConstants.PAGE_SIZE));
			List<OpenRecordQueryDto> searchRes = openRecordQueryRepository.getOpenRecord(searchMap);
			if (searchRes == null) {
				logger.error("[getOpenRecordByPage()->fail:查询数据库失败]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.SEARCH_FAIL);
			} else {
				PageInfo<OpenRecordQueryDto> info = new PageInfo<>(searchRes);
				logger.info("[getOpenRecordByPage->success :查询成功]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, info);
			}
		} catch (Exception e) {
			logger.error("getOpenRecordByPage->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
