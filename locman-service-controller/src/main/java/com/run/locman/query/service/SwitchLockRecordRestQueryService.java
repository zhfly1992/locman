/*
 * File name: SwitchLockRecordRestQueryService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.SwitchLockRecord;
import com.run.locman.api.query.service.SwitchLockRecordQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SwitchLockRecordConstants;
import com.run.locman.filetool.ExcelView;

/**
 * @Description: 开关锁记录rest
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月7日
 */
@Service
public class SwitchLockRecordRestQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Value("${api.host}")
	private String							ip;

	@Autowired
	private SwitchLockRecordQueryService	switchLockRecordQueryService;

	@Autowired
	private HttpServletRequest				request;



	public Result<PageInfo<Map<String, String>>> listSwitchLockPage(SwitchLockRecord switchLockRecord) {

		try {

			// 校验基础参数
			if (switchLockRecord == null || StringUtils.isBlank(switchLockRecord.getAccessSecret())) {
				logger.error(String.format("[listSwitchLockPage()->error:%s--%s]", PublicConstants.BUSINESS_INVALID,
						SwitchLockRecordConstants.COMMAND_ACCESSSECRET));
				return ResultBuilder.failResult(String.format("[listSwitchLockPage()->error:%s--%s]",
						PublicConstants.BUSINESS_INVALID, SwitchLockRecordConstants.COMMAND_ACCESSSECRET));
			}

			logger.info(String.format("[listSwitchLockPage()->request accessSecret:%s]",
					switchLockRecord.getAccessSecret()));

			// 判断keyword是否不为null
			if (!StringUtils.isBlank(switchLockRecord.getKeyWord())) {
				List<String> keyWordIds = keyWordIds(switchLockRecord);
				switchLockRecord.setIds(keyWordIds);
			}

			// http 查询所有子组织id
			if (!StringUtils.isBlank(switchLockRecord.getOrgId())) {
				List<String> orgIds = findOrgChildId(switchLockRecord.getOrgId());
				orgIds.add(switchLockRecord.getOrgId());
				switchLockRecord.setOrgIds(orgIds);
			}

			RpcResponse<PageInfo<Map<String, String>>> res = switchLockRecordQueryService
					.listSwitchLockPage(switchLockRecord);

			if (!res.isSuccess()) {
				logger.error(String.format("[listSwitchLockPage()->error:%s]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());

			} else {
				// 返回分页数据
				PageInfo<Map<String, String>> parsingData = parsingData(res.getSuccessValue());
				logger.info(String.format("[listSwitchLockPage()->suc:%s]", res.getMessage()));
				return ResultBuilder.successResult(parsingData, res.getMessage());
			}

		} catch (Exception e) {
			logger.error("listSwitchLockPage()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:解析分页数据
	 * @param successValue
	 * @return
	 * @throws Exception
	 */
	private PageInfo<Map<String, String>> parsingData(PageInfo<Map<String, String>> successValue) throws Exception {
		// 解析数据
		List<Map<String, String>> list = successValue.getList();

		// 获取人的id 以及组织id
		List<String> userIds = createList(list, true);
		List<String> orgIds = createList(list, false);

		// 获取人的名字以及组织的名称
		Map<String, String> userNames = getAttrName(userIds, true);
		Map<String, String> orgNames = getAttrName(orgIds, false);

		// 替换数据
		for (Map<String, String> switchMaps : list) {
			switchMaps.put(SwitchLockRecordConstants.COMMAND_ORG_ID,
					orgNames.get(switchMaps.get(SwitchLockRecordConstants.COMMAND_ORG_ID)));
			switchMaps.put(SwitchLockRecordConstants.COMMAND_USER_ID,
					userNames.get(switchMaps.get(SwitchLockRecordConstants.COMMAND_USER_ID)));
		}

		successValue.setList(list);

		return successValue;
	}



	/**
	 * 
	 * @Description:构建人的ids或者组织的ids
	 * @param list
	 * @param flag
	 * @return
	 */
	private List<String> createList(List<Map<String, String>> list, Boolean flag) throws Exception {
		List<String> ids = Lists.newArrayList();
		if (flag) {
			for (Map<String, String> map : list) {
				ids.add(map.get(SwitchLockRecordConstants.COMMAND_USER_ID));
			}
		} else {
			for (Map<String, String> map : list) {
				ids.add(map.get(SwitchLockRecordConstants.COMMAND_ORG_ID));
			}
		}
		return ids;
	}



	/**
	 * 
	 * @Description:通过feign调用获取数组中的信息 key 为id value为名称
	 * @param list
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getAttrName(List<String> list, Boolean flag) throws Exception {

		// 如果为0 说明没数据
		if (list.size() == 0) {
			return new HashMap<>(0);
		}

		JSONObject json = new JSONObject();
		String restValue = null;
		if (flag) {
			// 用户
			json.put(SwitchLockRecordConstants.FEIGN_USERIDS, list);
			restValue = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.FIND_USER_NAME, json, ip,
					request.getHeader(InterGatewayConstants.TOKEN));
		} else {
			// 组织
			json.put(SwitchLockRecordConstants.FEIGN_ORGIDS, list);
			restValue = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.FIND_ORG_NAME, json, ip,
					request.getHeader(InterGatewayConstants.TOKEN));
		}

		if (StringUtils.isBlank(restValue)) {
			logger.error(String.format("[getAttrName()->error:请检查http请求,id = %s]", json));
			return new HashMap<>(0);
		}
		logger.info("[findOrgChildId()->suc:查询http请求成功！]");
		return JSON.parseObject(restValue).toJavaObject(Map.class);
	}



	/**
	 * 
	 * @Description:通过用户名模糊匹配ids
	 * @param switchLockRecord
	 * @return
	 */
	private List<String> keyWordIds(SwitchLockRecord switchLockRecord) throws Exception {
		JSONObject userJson = new JSONObject();
		userJson.put(SwitchLockRecordConstants.COMMAND_ACCESSSECRET, switchLockRecord.getAccessSecret());
		userJson.put(SwitchLockRecordConstants.COMMAND_KEYWORD, switchLockRecord.getKeyWord());

		String restValue = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.FIND_USER_KEYWORD, userJson, ip,
				request.getHeader(InterGatewayConstants.TOKEN));

		if (StringUtils.isBlank(restValue)) {
			logger.error(String.format("[keyWordIds()->error:请检查http请求,请求参数 = %s]", userJson));
			return new ArrayList<>();
		}

		logger.info("[keyWordIds()->suc:查询http请求成功！]");
		return JSON.parseArray(restValue).toJavaList(String.class);

	}



	/**
	 * 
	 * @Description: 导出开关锁记录
	 * @param switchLockRecord
	 * @return
	 */
	public ModelAndView exprotSwitchLockInfo(SwitchLockRecord switchLockRecord, ModelMap model) {

		try {

			if (switchLockRecord == null) {
				logger.error("[exprotSwitchLockInfo()->error:查询业务对象为null！]");
				return null;
			}

			switchLockRecord.setPageSize("0");
			switchLockRecord.setPageNum("0");

			Result<PageInfo<Map<String, String>>> result = listSwitchLockPage(switchLockRecord);

			// 分装导出格式
			Map<String, Object> map = new LinkedHashMap<>();
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_FACILITIESCODE,
					SwitchLockRecordConstants.SWITCH_EXPROT_FACILITIESCODE_PAR);
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_FACILITYTYPEALIAS,
					SwitchLockRecordConstants.SWITCH_EXPROT_FACILITYTYPEALIAS_PAR);
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_AREANAME,
					SwitchLockRecordConstants.SWITCH_EXPROT_AREANAME_PAR);
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_ORGID, SwitchLockRecordConstants.SWITCH_EXPROT_ORGID_PAR);
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_LOCKSTATE,
					SwitchLockRecordConstants.SWITCH_EXPROT_LOCKSTATE_PAR);
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_REPORTTIME,
					SwitchLockRecordConstants.SWITCH_EXPROT_REPORTTIME_PAR);
			map.put(SwitchLockRecordConstants.SWITCH_EXPROT_ARRANGEUSERID,
					SwitchLockRecordConstants.SWITCH_EXPROT_ARRANGEUSERID_PAR);

			model.put(ExcelView.EXCEL_NAME, SwitchLockRecordConstants.SWITCH_EXPROT_NAME);
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);

			if (!SwitchLockRecordConstants.REST_CODE.equals(result.getResultStatus().getResultCode())) {
				logger.error(
						String.format("[listSwitchLockPage()->error:%s]", result.getResultStatus().getResultMessage()));
				return null;
			}

			// 成功封裝數據
			logger.info(String.format("[exprotSwitchLockInfo()->suc:%s]", result.getResultStatus().getResultMessage()));
			List<Map<String, String>> buildData = buildData(result.getValue().getList());
			model.put(ExcelView.EXCEL_DATASET, buildData);

			View excelView = new ExcelView();
			return new ModelAndView(excelView);

		} catch (Exception e) {
			logger.error("exportAlarmOrderCount()->exception", e);
			return null;
		}

	}



	/**
	 * 
	 * @Description:封装数据
	 * @param list
	 * @return
	 */
	private List<Map<String, String>> buildData(List<Map<String, String>> list) throws Exception {

		for (Map<String, String> map : list) {
			String lockState = map.get(SwitchLockRecordConstants.DEVICE_LOCKSTATE);
			if (SwitchLockRecordConstants.SWITCH_CLOSE.equals(lockState)) {
				map.put(SwitchLockRecordConstants.DEVICE_LOCKSTATE, SwitchLockRecordConstants.SWITCH_CLOSE_PAR);
			} else {
				map.put(SwitchLockRecordConstants.DEVICE_LOCKSTATE, SwitchLockRecordConstants.SWITCH_OPEN_PAR);
			}
		}

		return list;
	}



	/**
	 * 
	 * @Description:通过组织id查询子组织id
	 * @param orgId
	 * @return
	 * @throws Exception
	 */
	private List<String> findOrgChildId(String orgId) throws Exception {

		JSONObject orgJson = new JSONObject();
		orgJson.put(SwitchLockRecordConstants.COMMAND_ORG_ID_MONGO, orgId);
		String restValue = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.FIND_ORG_ID, orgJson, ip,
				request.getHeader(InterGatewayConstants.TOKEN));

		if (StringUtils.isBlank(restValue)) {
			logger.error(String.format("[findOrgChildId()->error:请检查http请求,组织id = %s]", orgId));
			return new ArrayList<>();
		}

		logger.info("[findOrgChildId()->suc:查询http请求成功！]");
		JSONArray jsonArray = JSON.parseArray(restValue);
		List<String> organizationInfoList = jsonArray.toJavaList(String.class);
		return organizationInfoList;
	}
	
	
	public Result<PageInfo<Map<String, Object>>> listManholeCoverSwitch(JSONObject json) {
		logger.info(String.format("[listManholeCoverSwitch()->request :%s]",json.toString()));
		try {
			RpcResponse<PageInfo<Map<String, Object>>> res = switchLockRecordQueryService.listManholeCoverSwitch(json);
			if (res == null || !res.isSuccess()) {
				logger.error("listManholeCoverSwitch-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[listManholeCoverSwitch()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("listManholeCoverSwitch()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	public Result<PageInfo<Map<String, Object>>> getManholeCoverSwitchInfo(JSONObject json) {
		logger.info(String.format("[getManholeCoverSwitchInfo()->request :%s]",json.toString()));
		try {
			RpcResponse<PageInfo<Map<String, Object>>> res = switchLockRecordQueryService.getManholeCoverSwitchInfo(json);
			if (res == null || !res.isSuccess()) {
				logger.error("getManholeCoverSwitchInfo-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getManholeCoverSwitchInfo()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getManholeCoverSwitchInfo()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
