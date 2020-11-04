/*
 * File name: NewFaultOrderQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年12月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.NewFaultOrderQueryRepository;
import com.run.locman.api.query.service.NewFaultOrderQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年12月7日
 */

public class NewFaultOrderQueryServiceImpl implements NewFaultOrderQueryService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private NewFaultOrderQueryRepository	newFaultOrderQueryRepository;
	@Autowired
	private MongoTemplate				mongoTemplate;


	/**
	 * @see com.run.locman.api.query.service.NewFaultOrderQueryService#getFaultListInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFaultListInfo(JSONObject pageObj) {
		logger.info(String.format("[getFaultListInfo()方法执行开始...,参数：【%s】]", pageObj));
		try {
			if (pageObj == null) {
				logger.error(String.format("[getFaultListInfo()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.USC_ACCESS_SECRET)) {
				logger.error(String.format("[getFaultListInfo()->error：%s%s", FacilitiesContants.USC_ACCESS_SECRET,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder
						.buildErrorRpcResp(FacilitiesContants.USC_ACCESS_SECRET + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.PAGE_SIZE)) {
				logger.error(String.format("[getFaultListInfo()->error：%s%s", FacilitiesContants.PAGE_SIZE,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder
						.buildErrorRpcResp(FacilitiesContants.PAGE_SIZE + MessageConstant.PARAM_IS_NULL);
			}
			if (!pageObj.containsKey(FacilitiesContants.PAGE_NO)) {
				logger.error(String.format("[getFaultListInfo()->error：%s%s", FacilitiesContants.PAGE_NO,
						MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(FacilitiesContants.PAGE_NO + MessageConstant.PARAM_IS_NULL);
			}
			if (!StringUtils.isNumeric(pageObj.getString(FacilitiesContants.PAGE_SIZE))
					|| !StringUtils.isNumeric(pageObj.getString(FacilitiesContants.PAGE_NO))) {
				logger.error(String.format("[getFaultListInfo()->error：%s", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.PARAM_NOT_IS_NUM);
			}
			if (!pageObj.containsKey("orderState")) {
				logger.error(
						String.format("[getFaultListInfo()->error：%s%s", "orderState", MessageConstant.PARAM_IS_NULL));
				return RpcResponseBuilder.buildErrorRpcResp("orderState" + MessageConstant.PARAM_IS_NULL);
			}

			String accessSecret = pageObj.getString(FacilitiesContants.USC_ACCESS_SECRET);
			int pageNo = pageObj.getIntValue(FacilitiesContants.PAGE_NO);
			int pageSize = pageObj.getIntValue(FacilitiesContants.PAGE_SIZE);

			PageHelper.startPage(pageNo, pageSize);
			Map<String, Object> params = new HashMap<>(16);
			params.put("accessSecret", accessSecret);
			params.put("orderState", pageObj.getString("orderState"));

			String ip = pageObj.getString(InterGatewayConstants.IP);
			String token = pageObj.getString(InterGatewayConstants.TOKEN);
			// 按组织搜索时
			// 存储组织id,name
			// Map<String, String> organizationInfo = Maps.newHashMap();
			// 存储组织id
			List<String> organizationIdList = Lists.newArrayList();
			if (!StringUtils.isBlank(pageObj.getString(FacilitiesContants.ORGANIZATION_ID))) {
				queryOrganzationInfoByOrgId(pageObj, ip, token, organizationIdList);
			}
			// 有组织id,封装搜索参数
			if (organizationIdList.size() != 0) {
				params.put(AlarmInfoConstants.ORG_ID, organizationIdList);
			}
			List<Map<String, Object>> page = newFaultOrderQueryRepository.getFaultListInfo(params);
			PageInfo<Map<String, Object>> info = new PageInfo<>(page);
			// 封装组织信息
			pageOrg(page, ip, accessSecret, token);

			logger.info(String.format("[getFaultListInfo()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, info);

		} catch (Exception e) {
			logger.error("getFaultListInfo()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@SuppressWarnings("rawtypes")
	private void queryOrganzationInfoByOrgId(JSONObject pageObj, String ip, String token,
			List<String> organizationIdList) {
		String organizationId = pageObj.getString(FacilitiesContants.ORGANIZATION_ID);
		if (!StringUtils.isBlank(ip)) {
			String httpValueByGet = InterGatewayUtil
					.getHttpValueByGet(InterGatewayConstants.U_OWN_AND_SON_INFO + organizationId, ip, token);
			if (null == httpValueByGet) {
				logger.error("[getAlarmInfoList()->invalid：组织查询失败!]");
				organizationIdList.add(organizationId);
			} else {
				JSONArray jsonArray = JSON.parseArray(httpValueByGet);
				List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
				for (Map map : organizationInfoList) {
					organizationIdList.add("" + map.get(InterGatewayConstants.ORGANIZATION_ID));
				}
			}
		} else {
			// 获取IP失败或者为null则不访问
			logger.error("[getAlarmInfoList()->invalid：IP获取失败,组织查询失败!]");
			organizationIdList.add(organizationId);
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void pageOrg(List<Map<String, Object>> facInfo, String ip, String accessSecret, String token) {
		logger.info(String.format("[pageOrg()方法执行开始...,参数：【%s】【%s】【%s】【%s】]", facInfo, ip, accessSecret, token));
		// if (!StringUtils.isBlank(ip)) {
		// 查询所有组织信息
		String httpValueByGet = InterGatewayUtil.getHttpValueByGet(InterGatewayConstants.U_ORGANIZATIONS + accessSecret,
				ip, token);
		// 如果查询失败,组织信息为空
		if (null == httpValueByGet) {
			logger.error("[getAlarmInfoList()->invalid：组织查询失败!]");
			for (Map<String, Object> map : facInfo) {
				if (map.containsKey(FacilitiesContants.ORGANIZATION_ID)) {
					map.put(FacilitiesContants.ORG_INFO, "");
				}
			}
		} else {
			JSONArray jsonArray = JSON.parseArray(httpValueByGet);
			List<Map> organizationInfoList = jsonArray.toJavaList(Map.class);
			// 参数名转换
			for (Map organizationInfo : organizationInfoList) {
				organizationInfo.put(FacilitiesContants.ORGANIZATION_NAME,
						organizationInfo.remove(InterGatewayConstants.ORGANIZATION_NAME));
			}
			// 封装对应的组织信息
			for (Map<String, Object> organizationInfo : facInfo) {
				String orgId = "" + organizationInfo.get(FacilitiesContants.ORGANIZATION_ID);
				Object organizationInfoStr = UtilTool.getStringByKey(organizationInfoList,
						InterGatewayConstants.ORGANIZATION_ID, orgId);
				if (organizationInfoStr != null && !StringUtils.isBlank("" + organizationInfoStr)) {
					organizationInfo.put(FacilitiesContants.ORG_INFO, organizationInfoStr);
				} else {
					organizationInfo.put(FacilitiesContants.ORG_INFO, "");
				}
			}
		}
		logger.info(String.format("[pageOrg()方法执行结束!]"));
	}



	/**
	 * @see com.run.locman.api.query.service.NewFaultOrderQueryService#countCountDay(java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	@Override
	public RpcResponse<Map<String, Object>> countCountDay(JSONObject param) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String s=DateUtils.formatDate(sdf.parse("2020-02-28 15:15:15"), DateUtils.DATE_START);
			System.out.println(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String timeInt1=param.getString("timeInt1");
		String timeStamp1=param.getString("timeStamp1");
		SimpleDateFormat tempDate1 = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> timeMap = new LinkedHashMap<String, Object>(16);
		BasicDBObject fieldsObject = new BasicDBObject();
		fieldsObject.put("deviceId", 1);
		DBObject dbObjectFv = new BasicDBObject("$gt", 1);
		DBObject dbObject = new BasicDBObject("$gte", timeInt1).append("$lte", timeStamp1 + "");
		DBObject ageCompare = new BasicDBObject("timestamp", dbObject);
		ageCompare.put("attributeInfo.attributeUnit", "");
		ageCompare.put("attributeInfo", new BasicDBObject("$elemMatch",
				new BasicDBObject("attributeName", "fv").append("attributeReportedValue", dbObjectFv)));
		Query query = new BasicQuery(ageCompare, fieldsObject);
		List<Map> deviceHistoryStateMap = mongoTemplate.find(query, Map.class, "deviceHistoryState");
		ArrayList<String> list = new ArrayList<String>();
		
		for (Map<String, Object> map : deviceHistoryStateMap) {
			String deviceId = map.get("deviceId") + "";
			list.add(deviceId);
		}
		Set<String> set = new HashSet<String>();
		set.addAll(list);
		logger.info("countCountDay()->上报设备set集合：" + set);
		String dateNum = tempDate1.format(new Date(Long.parseLong(timeInt1)));
		logger.info(String.format("%s--countCountDay（）历史表查询结果集大小为：%s", dateNum, list.size()));
		timeMap.put(dateNum, set.size());
		logger.info("countCountDay()->查询成功");
		return RpcResponseBuilder.buildSuccessRpcResp("统计设备上报数量成功!", timeMap);
	}

}
