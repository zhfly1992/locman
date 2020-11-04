/*
 * File name: FacilitiesCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 heyuan 2017年10月20日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.base.query.FacilitesService;
import com.run.locman.api.crud.repository.AlarmOrderCrudRepository;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.repository.FacilityRenovationCrudRepository;
import com.run.locman.api.crud.service.FacilitiesCrudService;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.query.service.FacilitiesRenovationQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 设施cud实现类
 * @author: heyuan
 * @version: 1.0, 2018年1月26日
 */
@Transactional(rollbackFor = Exception.class)
public class FacilitiesCrudServiceImpl implements FacilitiesCrudService {
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilitiesCrudRepository	facilitiesCrudRepository;

	@Autowired
	private FacilitesService			facilitesService;

	@Autowired
	private FacilitiesRenovationQueryService	facilitiesRenovationQueryService;
	
	@Autowired
	private FacilityRenovationCrudRepository   facilityRenovationCrudRepository;
	
	@Autowired
	private AlarmOrderCrudRepository	alarmOrderCrudRepository;
	
	@Autowired
	private UpdateRedisCrudService		updateRedisCrudService;
	
	@Value("${api.host}")
	private String						ip;
	/**
	 * 专用变量,不能他用
	 */
	//private Object						rollBackPoint;



	@Override
	public RpcResponse<Facilities> addFacilities(Facilities facilities) {
		try {
			if (StringUtils.isBlank(facilities.getAccessSecret())
					|| StringUtils.isBlank(facilities.getFacilitiesTypeId())
					|| StringUtils.isBlank(facilities.getFacilitiesCode())
					|| StringUtils.isBlank(facilities.getLatitude()) || StringUtils.isBlank(facilities.getLongitude())
					|| StringUtils.isBlank(facilities.getOrganizationId())
					|| StringUtils.isBlank(facilities.getAddress()) || StringUtils.isBlank(facilities.getAreaId())) {
				logger.error("[addFacilities()->error: 新增设施失败，基础参数不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("新增设施失败，基础参数不能为空");
			}
			// 新增时id为空,传null
			RpcResponse<Boolean> checkFacilitiesCode = facilitesService
					.checkFacilitiesCode(facilities.getFacilitiesCode(), facilities.getAccessSecret(), null);
			if (null == checkFacilitiesCode || null == checkFacilitiesCode.getSuccessValue()) {
				logger.info("[addFacilities()->error: 新增设施失败,校验设施序列号是否重复失败,返回参数为null]");
				return RpcResponseBuilder.buildErrorRpcResp("新增设施失败,校验设施序列号是否重复失败");
			} else if (!checkFacilitiesCode.isSuccess() || !checkFacilitiesCode.getSuccessValue()) {
				logger.info("[addFacilities()->error: 新增设施失败,校验设施序列号是否重复失败]");
				return RpcResponseBuilder.buildErrorRpcResp("新增设施失败," + checkFacilitiesCode.getMessage());
			}

			facilities.setManageState(FacilitiesContants.ENABLE);
			facilities.setCreationTime(DateUtils.formatDate(new Date()));
			logger.info(String.format("[addFacilities()->进入方法,参数:%s]", facilities));
			int num = facilitiesCrudRepository.insertModel(facilities);
			if (num > 0) {
				//查询设施ID,根据设施序列号和密钥
				HashMap<String, Object> queryMap = new HashMap<String,Object>();
				queryMap.put("facilitiesCode", facilities.getFacilitiesCode());
				queryMap.put("accessSecret", facilities.getAccessSecret());
				String findIdByCodeAndAcc = facilitiesCrudRepository.findIdByCodeAndAcc(queryMap);
				if(null !=findIdByCodeAndAcc && findIdByCodeAndAcc!="") {
					//放入Redis缓存
					queryMap.put("id", findIdByCodeAndAcc);
					RpcResponse<String> updateFacMapCache = updateRedisCrudService.updateFacMapCache(queryMap);
					if(!updateFacMapCache.isSuccess()) {
						logger.error("[addFacilities()->error: 新增设施加入缓存失败！]");
					}
				}
				
				logger.info("[addFacilities()->info: 新增设施成功" + JSON.toJSONString(facilities) + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_ADD_SUCCESS, facilities);
			}
			logger.error("[addFacilities()->error: 新增设施失败]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.FACILITIES_ADD_FAIL);
		} catch (Exception e) {
			logger.error("addFacilities()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Facilities> updateFacilities(Facilities facilities) {
		try {
			if (StringUtils.isBlank(facilities.getId())) {
				logger.error("[updateFacilities()->error: 修改设施失败，设施id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("修改设施失败，设施id不能为空");
			}
			facilities.setEditorTime(DateUtils.formatDate(new Date()));
			logger.info(String.format("[updateFacilities()->进入方法,参数:%s]", facilities));
			RpcResponse<Boolean> checkFacilitiesCode = facilitesService.checkFacilitiesCode(
					facilities.getFacilitiesCode(), facilities.getAccessSecret(), facilities.getId());
			if (null == checkFacilitiesCode || null == checkFacilitiesCode.getSuccessValue()) {
				logger.info("[updateFacilities()->error: 修改设施失败,校验设施序列号是否重复失败,返回参数为null]");
				return RpcResponseBuilder.buildErrorRpcResp("修改设施失败,校验设施序列号是否重复失败");
			} else if (!checkFacilitiesCode.isSuccess() || !checkFacilitiesCode.getSuccessValue()) {
				logger.info("[updateFacilities()->error: 修改设施失败,校验设施序列号是否重复失败]");
				return RpcResponseBuilder.buildErrorRpcResp("修改设施失败," + checkFacilitiesCode.getMessage());
			}

			int num = facilitiesCrudRepository.updatePart(facilities);
			if (num > 0) {
				//放入Redis缓存
				HashMap<String, Object> queryMap = new HashMap<String,Object>();
				queryMap.put("id", facilities.getId());
				queryMap.put("accessSecret", facilities.getAccessSecret());
				RpcResponse<String> updateFacMapCache = updateRedisCrudService.updateFacMapCache(queryMap);
				if(!updateFacMapCache.isSuccess()) {
					logger.error("[addFacilities()->error: 修改设施加入缓存失败！]");
				}
				
				logger.info("[updateFacilities()->error: 修改设施成功" + JSON.toJSONString(facilities) + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_OPERATE_SUCCESS, facilities);
			}
			logger.error("[updateFacilities()->error: 修改设施失败]");
			return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.FACILITIES_OPERATE_FAIL);
		} catch (Exception e) {
			logger.error("updateFacilities()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesCrudService#addFacilitiesAndDeviceId(java.util.List)
	 */
	@Override
	public RpcResponse<String> addFacilitiesAndDeviceId(List<Map<String, Object>> list,String accessSecret) {
		logger.info(String.format("[addFacilitiesAndDeviceId()->进入方法,参数:%s]", list));
		int addFacilitiesAndDeviceId = facilitiesCrudRepository.addFacilitiesAndDeviceId(list);
		if (addFacilitiesAndDeviceId > 0) {
			List<String> facIds=new ArrayList<>();
			for(Map<String, Object> map:list) {
				String id=map.get("facilityId")+"";
				facIds.add(id);
			}
			//放入Redis缓存
			updateRedisCrudService.batchUpdateFacMapCache(facIds, accessSecret);
			
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_OPERATE_SUCCESS,
					addFacilitiesAndDeviceId + "");
		}
		return RpcResponseBuilder.buildErrorRpcResp("操作失败!");

	}



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesCrudService#batchInsertFacilities(java.util.List)
	 */
	@Override
	public RpcResponse<Integer> batchInsertFacilities(List<Facilities> sheet) {

		logger.info(String.format("[batchInsertFacilities()->rpc params :%s]", sheet));
		try {

			if (sheet == null || sheet.size() == 0) {
				logger.error("[batchInsertFacilities()->error:sheet集合为null或者不存在!]");
				return RpcResponseBuilder.buildErrorRpcResp("[batchInsertFacilities()->error:sheet集合为null或者不存在!]");
			}

			int batchInsertFacilities = facilitiesCrudRepository.batchInsertFacilities(sheet);
			if (batchInsertFacilities > 0) {
				//放入Redis缓存
				for(Facilities facilities:sheet) {
					HashMap<String, Object> queryMap = new HashMap<String,Object>();
					queryMap.put("id", facilities.getId());
					queryMap.put("accessSecret", facilities.getAccessSecret());
					
					updateRedisCrudService.updateFacMapCache(queryMap);
				}
				logger.error(String.format("[batchInsertFacilities()->error:%s,%s]", PublicConstants.PARAM_SUCCESS,
						batchInsertFacilities));
				return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, batchInsertFacilities);
			}

			return RpcResponseBuilder.buildErrorRpcResp(PublicConstants.PARAM_ERROR);

		} catch (Exception e) {
			logger.error("batchInsertFacilities()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesCrudService#batchInsertFacAndDevice(java.util.List)
	 */
	@Override
	public RpcResponse<Integer> batchInsertFacAndDevice(List<Facilities> sheet) {
		logger.info(String.format("[batchInsertFacilities()->rpc params :%s]", sheet));
		try {

			if (sheet == null || sheet.size() == 0) {
				logger.error("[batchInsertFacAndDevice()->error:sheet集合为null或者不存在!]");
				return RpcResponseBuilder.buildErrorRpcResp("[batchInsertFacilities()->error:sheet集合为null或者不存在!]");
			}

			int batchInsertFacAndDevice = facilitiesCrudRepository.batchInsertFacAndDevice(sheet);
			if (batchInsertFacAndDevice > 0) {
				//放入Redis缓存
				for(Facilities facilities:sheet) {
					HashMap<String, Object> queryMap = new HashMap<String,Object>();
					queryMap.put("id", facilities.getId());
					queryMap.put("accessSecret", facilities.getAccessSecret());
					updateRedisCrudService.updateFacMapCache(queryMap);
				}
				logger.error(String.format("[batchInsertFacilities()->error:%s,%s]", PublicConstants.PARAM_SUCCESS,
						batchInsertFacAndDevice));
				return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, batchInsertFacAndDevice);
			}

			return RpcResponseBuilder.buildErrorRpcResp(PublicConstants.PARAM_ERROR);

		} catch (Exception e) {
			logger.error("updateFacilities()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesCrudService#updateFacilitiesDenfenseState(java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public RpcResponse<Integer> updateFacilitiesDenfenseState(String organizationId, List<String> facIds, String token,
			String denfenseState, String accessSecret) {
		// TODO Auto-generated method stub
		logger.info(String.format(
				"[updateFacilitiesDenfenseState()->进入方法,organizationId--%s,facIds--%s,denfenseState--%s,accessSecret--%s]",
				organizationId, facIds, denfenseState, accessSecret));

		/*try {
			// 事务回滚点,如果出现异常,据此回滚到指定点,避免次要数据异常导致主要数据全部回滚;
			rollBackPoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
		} catch (NoTransactionException e) {
			logger.error("updateFacilitiesDenfenseState()->exception", e);
		}*/
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，接入方秘钥为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥为空");
			}
			if (facIds == null) {
				facIds = new ArrayList<>();
			}
			if (StringUtils.isBlank(organizationId) && facIds.isEmpty()) {
				logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，organzationId和facIds不能同时为空]");
				return RpcResponseBuilder.buildErrorRpcResp("organzationId和facIds不能同时为空");
			}
			if (!StringUtils.isBlank(organizationId) && !facIds.isEmpty()) {
				logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，organzationId和facIds不能同时有值]");
				return RpcResponseBuilder.buildErrorRpcResp("organzationId和facIds不能同时有值");
			}
            
			// 屏蔽状态必须为1或者0或者2，0为手动维护，1为未屏蔽,2为自动维护,3待整治审核,4待整治,5维修中
			if (!denfenseState.equals("1") && !denfenseState.equals("0") && !denfenseState.equals("2") && !denfenseState.equals("5") ) {
				logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，defensenState错误]");
				return RpcResponseBuilder.buildErrorRpcResp("defensenState参数错误");
			}

			List<String> organizationIdList = new ArrayList<String>();
			if (!StringUtils.isBlank(organizationId)) {
				//若是组织id不为空，则需要查询组织和子组织id
				queryOrganzationInfoByOrgId(organizationId, ip, token, organizationIdList);
			}

			if (denfenseState.equals("1") || "5".equals(denfenseState)) {
				// denfenseState值为1，则是将设施改为未屏蔽，不需要处理工单
				logger.info("[updateFacilitiesDenfenseState()->修改设施状态为未屏蔽/维修中,无需进行工单相关操作]");
				int facRes = facilitiesCrudRepository.updateFacilitiesDefenseState(facIds, organizationIdList,
						denfenseState, accessSecret);
				if (facRes > 0) {
					updateRedisCrudService.batchUpdateFacMapCache(facIds, accessSecret);
					logger.info("[updateFacilitiesDenfenseState->设施状态更改为未屏蔽成功]");
					return RpcResponseBuilder.buildSuccessRpcResp("设施状态更改为未屏蔽成功", facRes);
				}
				logger.error("[updateFacilitiesDenfenseState->设施状态更改为未屏蔽失败]");
				return RpcResponseBuilder.buildErrorRpcResp("设施状态更改为未屏蔽失败");
			}
			
			else {
				// denfenseState值为0或者2，则是将设施屏蔽，需要处理工单,将设施下的告警工单状态更改为已完成,告警信息状态更改为已处理完成
				logger.info("[updateFacilitiesDenfenseState()->修改设施状态为屏蔽,需进行工单告警相关操作]");
				

				// 先获取alarmOrderid
				List<String> alarmOrderId = facilitiesCrudRepository.getAlarmOrderId(facIds, organizationIdList, accessSecret);
				// 处理所有告警信息
				int updateAlarmInfoState = facilitiesCrudRepository.updateAlarmInfoState(facIds, organizationIdList, accessSecret);
				logger.info(String.format("[updateFacilitiesDenfenseState->相关告警状态修改成功，数量%s]", updateAlarmInfoState));
				// 处理alarmOrder
				if(alarmOrderId.size() > 0){
					int updateAlarmOrderState = facilitiesCrudRepository.updateAlarmOrderState(alarmOrderId);
					logger.info(String.format("[updateFacilitiesDenfenseState->相关工单处理成功，数量%s]", updateAlarmOrderState));
				}
				else {
					logger.info("[updateFacilitiesDenfenseState->相关工单数量为0，无需处理工单]");
				}
//				int alarmOrderRes = alarmOrderCrudRepository.updateAlarmOrderStateAndAlarmInfoIsDel(facIds, organizationIdList,
//						accessSecret);
//				logger.info(String.format("[updateFacilitiesDenfenseState->相关告警工单和告警状态修改成功，数量%s]", alarmOrderRes));
				int facRes = facilitiesCrudRepository.updateFacilitiesDefenseState(facIds, organizationIdList,
						denfenseState, accessSecret);
				if (facRes > 0) {
					logger.info("[updateFacilitiesDenfenseState->设施状态更改为屏蔽成功]");
					return RpcResponseBuilder.buildSuccessRpcResp("设施状态更改为屏蔽成功", facRes);
				}
				logger.error("[updateFacilitiesDenfenseState->设施状态更改为屏蔽失败]");
				return RpcResponseBuilder.buildErrorRpcResp("设施状态更改为屏蔽失败");
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("[updateFacilitiesDenfenseState()->exception]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}finally {
			//放入Redis缓存
			updateRedisCrudService.batchUpdateFacMapCache(facIds, accessSecret);
		}
	}



	@SuppressWarnings("rawtypes")
	private void queryOrganzationInfoByOrgId(String organizationId, String ip, String token,
			List<String> organizationIdList) {
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



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesCrudService#examineRenovationFacility(com.alibaba.fastjson.JSONObject)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public RpcResponse<Integer> examineRenovationFacility(JSONObject json) {
		try {
			logger.info(String.format(
					"[updateFacilitiesDenfenseState()->进入方法,accessSecret--%s]",json));
			String facilityId = json.getString(FacilitiesContants.FACILITY_ID);
			String whetherPass = json.getString("whetherPass");
			if (!"yes".equals(whetherPass) && !"no".equals(whetherPass)) {
				logger.error("[examineRenovationFacility->error:传入参数值非法");
				return RpcResponseBuilder.buildErrorRpcResp("传入参数值非法");
			}
			if ("no".equals(whetherPass)) {
				return rejectExamine(json, facilityId);
				
			} else{
				return passExamine(json, facilityId);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("[updateFacilitiesDenfenseState()->exception]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}finally {
			//放入Redis缓存
			Map<String, Object> map = new HashMap<String ,Object>();
			String accessSecret = json.getString("accessSecret");
			map.put("accessSecret",accessSecret);
			map.put("id",json.getString(FacilitiesContants.FACILITY_ID));
			updateRedisCrudService.updateFacMapCache(map);
		}
		

	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<Integer> rejectExamine(JSONObject json, String facilityId) {
		//审批拒绝
		String operatorId = json.getString("userId");
		String accessSecret = json.getString(CommonConstants.ACCESS_SECRET);
		HashMap<String, Object> params = Maps.newHashMap();
		params.put("userId", operatorId);
		params.put("facilityId", facilityId);
		params.put("manageState", "2");
		//修改设施为未屏蔽正常状态:1
		List<String> facIds = Lists.newArrayList(facilityId);
		facilitiesCrudRepository.updateFacilitiesDefenseState(facIds, null, "1", accessSecret);
		int num = facilityRenovationCrudRepository.updateFacRenovationManageState(params);
		if (num > 0) {
			logger.info("[updateFacilitiesDenfenseState->设施状态更改为待整治审批拒绝操作成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("审批已拒绝", num);
		} else {
			logger.error("[updateFacilitiesDenfenseState->设施状态更改为待整治审批拒绝操作失败]");
			return RpcResponseBuilder.buildErrorRpcResp("审批拒绝操作失败");
		}
	}



	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	private RpcResponse<Integer> passExamine(JSONObject json, String facilityId) {
		RpcResponse<JSONObject> info = facilitiesRenovationQueryService.findInfoByFacId(facilityId);
		if (!info.isSuccess()) {
			logger.error("[examineRenovationFacility->error,设施状态更改为待整治失败]");
			return RpcResponseBuilder.buildErrorRpcResp("查询设施待整治信息失败");
		}
		JSONObject successValue = info.getSuccessValue();
		if (null == successValue || successValue.isEmpty()) {
			logger.error("[examineRenovationFacility->error,设施状态更改为待整治失败,findInfoByFacId返回值为空]");
			return RpcResponseBuilder.buildErrorRpcResp("查询设施待整治信息失败");
		}
		
		String accessSecret = json.getString(CommonConstants.ACCESS_SECRET);
		
		JSONObject savedJson = successValue.getJSONObject("applicationInfo");
		String presentPic = savedJson.getString("presentPic");
		//String defenseState = savedJson.getString("defenseState");
		String hiddenTroubleDesc = savedJson.getString("hiddenTroubleDesc");
		String alarmOrderId = savedJson.getString("alarmOrderId");
		String alarmOrderPresentPic = savedJson.getString("alarmOrderPresentPic");
		String userId = savedJson.getString("userId");
		String marks = savedJson.getString("marks");
		
		
		if (StringUtils.isBlank(hiddenTroubleDesc)) {
			logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，隐患描述为空]");
			return RpcResponseBuilder.buildErrorRpcResp("隐患描述为空");
		}
		JSONArray parseArray = savedJson.getJSONArray("hiddenTroubleDesc");
		JSONObject hiddenTroubleDescJson = new JSONObject(true);
		if (parseArray.size() > 0) {
			
			List<JSONObject> javaList = parseArray.toJavaList(JSONObject.class);
			int num = 1;
			for (JSONObject jsonObject : javaList) {
				String hiddenTroubleType = jsonObject.getString("hiddenTroubleType");
				String hiddenTroubleValue = jsonObject.getString("hiddenTroubleValue");
				
				if (StringUtils.isNotBlank(hiddenTroubleValue)) {
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("值:").append(hiddenTroubleValue);
					if (hiddenTroubleDescJson.containsKey(hiddenTroubleType)) {
						hiddenTroubleDescJson.put(hiddenTroubleType + num, stringBuffer.toString());
						num++;
					} else {
						hiddenTroubleDescJson.put(hiddenTroubleType, stringBuffer.toString());
					}
				}
				
			}
			
			if (StringUtils.isBlank(marks)) {
				marks = " ";
			}
			
			hiddenTroubleDescJson.put("修改时间", DateUtils.formatDate(new Date()));
			hiddenTroubleDescJson.put("备注", marks);
			hiddenTroubleDesc = hiddenTroubleDescJson.toJSONString();
		} else {
			logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，隐患描述为空]");
			return RpcResponseBuilder.buildErrorRpcResp("隐患描述为空");
		}
		
		
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，接入方秘钥为空]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方秘钥为空");
		}
		if (StringUtils.isBlank(facilityId)) {
			logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，设施id为空]");
			return RpcResponseBuilder.buildErrorRpcResp("设施id为空");
		}
		if (StringUtils.isBlank(presentPic)) {
			logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，到场图片地址为空]");
			return RpcResponseBuilder.buildErrorRpcResp("到场图片地址为空");
		}
		
		if (presentPic.contains("\"")) {
			presentPic = presentPic.replace("\"", "");
		}
		
		if (StringUtils.isBlank(userId)) {
			logger.error("[updateFacilitiesDenfenseState->error:传入参数非法，用户id为空]");
			return RpcResponseBuilder.buildErrorRpcResp("用户id为空");
		}
		
		// 先获取alarmOrderid
		List<String> facIds = Lists.newArrayList(facilityId);
		
		List<String> alarmOrderIds = facilitiesCrudRepository.getAlarmOrderId(facIds, null, accessSecret);
		// 处理所有告警信息
		int updateAlarmInfoState = facilitiesCrudRepository.updateAlarmInfoState(facIds, null, accessSecret);
		logger.info(String.format("[updateFacilitiesDenfenseState->相关告警状态修改成功，数量%s]", updateAlarmInfoState));
		// 处理alarmOrder
		if(alarmOrderIds.size() > 0){
			int updateAlarmOrderState = facilitiesCrudRepository.updateAlarmOrderState(alarmOrderIds);
			logger.info(String.format("[updateFacilitiesDenfenseState->相关工单处理成功，数量%s]", updateAlarmOrderState));
		} else {
			logger.info(String.format("[updateFacilitiesDenfenseState->设施%s相关工单数量为0，无需处理工单]", facilityId));
		}
		
		facilitiesCrudRepository.updateAlarmOrderPresentPic(alarmOrderId, alarmOrderPresentPic, userId);
		
		int facRes = facilitiesCrudRepository.updateFacDefenseStateRenovation(facilityId, presentPic,
				"4", accessSecret, hiddenTroubleDesc);
		
		HashMap<String, Object> params = Maps.newHashMap();
		String operatorId = json.getString("userId");
		params.put("userId", operatorId);
		params.put("facilityId", facilityId);
		//3,已通过未处理
		params.put("manageState", "3");
		
		facilityRenovationCrudRepository.updateFacRenovationManageState(params);
		
		if (facRes > 0) {
			logger.info("[updateFacilitiesDenfenseState->设施状态更改为待整治成功]");
			return RpcResponseBuilder.buildSuccessRpcResp("设施状态更改为待整治成功", facRes);
		} else {
			logger.error("[updateFacilitiesDenfenseState->设施状态更改为待整治失败]");
			return RpcResponseBuilder.buildErrorRpcResp("设施状态更改为待整治失败");
		}
		
	}

}
