/*
 * File name: FocusSecurityCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 钟滨远 2020年4月26日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FocusSecurityCrudRepository;
import com.run.locman.api.crud.service.FocusSecurityCrudService;
import com.run.locman.api.entity.FocusSecurity;
import com.run.locman.api.entity.FocusSecurityAndFac;
import com.run.locman.api.timer.crud.service.FocusSecuritysTimerCrudService;
import com.run.locman.api.timer.query.service.WingsIotConstansQuery;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.api.wingiot.service.WingIotCommand;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
 * @Description:
 * @author: 钟滨远
 * @version: 1.0, 2020年4月26日
 */

public class FocusSecurityCrudServiceImpl implements FocusSecurityCrudService {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FocusSecurityCrudRepository		focusSecurityCrudRepository;
	@Autowired
	private FocusSecuritysTimerCrudService	focusSecurityTimerCrudService;
	@Autowired
	private WingsIotConstansQuery           wingsIotConstansQuery;

	@Value("${api.host}")
	private String							ip;



	/**
	 * @see com.run.locman.api.crud.service.FocusSecurityCrudService#addFocusSecurity(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	@Transactional
	public RpcResponse<String> addFocusSecurity(JSONObject json) {

		logger.info(String.format("addFocusSecurity()->方法开始执行", json.toJSONString()));
		try {
			String securityName = json.getString("securityName") + "";
			String startTime = json.getString("startTime") + "";
			String endTime = json.getString("endTime") + "";
			String userId = json.getString("userId") + "";
			String status = json.getString("status") + "";
			String previewTime = json.getString("previewTime") + "";
			String organization = json.getString("organization") + "";
			String accessSecret=json.getString("accessSecret")+"";
			String personName=json.getString("personName");
			String personTel=json.getString("personTel");
			
			String org[] = organization.split(",");
			if (org != null && org.length > 0) {
				// 存储本次orgId
				List<String> organizationIdList = Lists.newArrayList();

				for (int i = 0; i < org.length; i++) {
					String httpValueByGet = InterGatewayUtil.getHttpValueByGet(
							InterGatewayConstants.U_OWN_AND_SON_INFO + org[i], ip, json.getString("token"));
					if (null == httpValueByGet) {
						logger.error("[addFocusSecurity()->invalid：查询该组织的子组织失败!]");
						return RpcResponseBuilder.buildErrorRpcResp("查询该组织的子组织失败!");
					} else {
						JSONArray jsonArray = JSONObject.parseArray(httpValueByGet);
						List<Map> organizationInfo = jsonArray.toJavaList(Map.class);
						for (Map map : organizationInfo) {
							organizationIdList.add(map.get(InterGatewayConstants.ORGANIZATION_ID) + "");
						}
					}
				}
				Map<String, Object> orgMap = new HashMap<String, Object>();
				orgMap.put("organizationIdList", organizationIdList);
				orgMap.put("accessSecret", json.getString("accessSecret") + "");

				// 查询数据库中组织id
				List<Map<String, Object>> findAllOrgId = focusSecurityCrudRepository.findAllOrgId();
				if (null != findAllOrgId && findAllOrgId.size() > 0) {
					// 库中所有的orgID（未查询子组织）
					List<String> oldOrgLists = new ArrayList<String>();
					for (Map<String, Object> oldMap : findAllOrgId) {
						String z = oldMap.get("organization") + "";
						String zz[] = z.split(",");

						List<String> oldOrgList = Arrays.asList(zz);
						oldOrgLists.addAll(oldOrgList);
					}
					// 重复的org
					List<String> repeatOrgIdList = Lists.newArrayList();

					for (String thisId : organizationIdList) {
						if (oldOrgLists.contains(thisId)) {
							repeatOrgIdList.add(thisId);
						}
					}
					if (null != repeatOrgIdList && repeatOrgIdList.size() > 0) {

						// 转中文
						JSONObject orgJson = new JSONObject();
						
						orgJson.put("organizationIds", organization);
						String httpValueByPost = InterGatewayUtil.getHttpValueByPost(
								InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgJson, ip, json.getString("token"));
						StringBuffer stringBuffer = new StringBuffer();
						if (null == httpValueByPost) {
							logger.error("通过interGateway查询组织名失败");
							return RpcResponseBuilder.buildErrorRpcResp("查询组织名称失败！");
						} else {
							JSONObject nameJson = JSONObject.parseObject(httpValueByPost);
							stringBuffer.append(nameJson.getString(organization));
							stringBuffer.append("组织");
						}

						logger.error(String.format("%s:组织已经存在保障工单！", repeatOrgIdList.toString()));
						return RpcResponseBuilder.buildErrorRpcResp(stringBuffer + "已经存在保障工单,请先停用！");
					}

				}

				// 设施ID
				List<Map<String, Object>> facInfo = focusSecurityCrudRepository.findFacIdByOrgIds(orgMap);

				Set<String> facilityIds = new HashSet<String>();
				if (null != facInfo && facInfo.size() > 0) {
					for (Map<String, Object> map : facInfo) {
						String facId = map.get("facilityId") + "";
						facilityIds.add(facId);
					}

					FocusSecurity focusSecurity = new FocusSecurity();
					focusSecurity.setId(UtilTool.getUuId());
					focusSecurity.setSecurityName(securityName);
					focusSecurity.setStartTime(startTime);
					focusSecurity.setEndTime(endTime);
					focusSecurity.setCreateTime(DateUtils.formatDate(new Date()));
					focusSecurity.setUserId(userId);
					focusSecurity.setAccessSecret(accessSecret);
					focusSecurity.setOrganization(organization);
					focusSecurity.setStatus(status);
					focusSecurity.setPreviewTime(previewTime);
					focusSecurity.setPersonName(personName);
					focusSecurity.setPersonTel(personTel);
					Integer addFocusSecurity = focusSecurityCrudRepository.addFocusSecurity(focusSecurity);
					// 中间表
					List<FocusSecurityAndFac> list = new ArrayList<FocusSecurityAndFac>();
					for (String facilityId : facilityIds) {
						FocusSecurityAndFac focusSecurityAndFac = new FocusSecurityAndFac();
						focusSecurityAndFac.setId(UtilTool.getUuId());
						focusSecurityAndFac.setFacilityId(facilityId);
						focusSecurityAndFac.setSecurityId(focusSecurity.getId());
						list.add(focusSecurityAndFac);
					}
					Integer addFocusSecurityAndFac = focusSecurityCrudRepository.addFocusSecurityAndFac(list);

					if (addFocusSecurity > 0 && addFocusSecurityAndFac > 0) {

						Map<String, Object> timerMap = new HashMap<String, Object>();
						String startTimeStamp = DateUtils.dateToStamp(startTime);
						if (null != previewTime && previewTime.length() > 0) {
							int hour = Integer.parseInt(previewTime);
							Long startTimeStampLong = Long.parseLong(startTimeStamp);
							String timerTimeStampLong = startTimeStampLong - hour * 60 * 60 * 1000L + "";
							timerMap.put("timerTimeStampLong", timerTimeStampLong);
						}
						timerMap.put("startTimeStamp", startTimeStamp);
						timerMap.put("facInfo", facInfo);
						timerMap.put("focusSecurityId", focusSecurity.getId());
						timerMap.put("accessSecret", accessSecret);
						// 开启定时器
						RpcResponse<String> focusSecurityIssued = focusSecurityTimerCrudService.focusSecurityIssued(timerMap);
						if(focusSecurityIssued.isSuccess()) {
							logger.info("addFocusSecurity()——>info：定时器开启成功！");
						}
						logger.info("addFocusSecurity()——>info：数据新增成功！");
						return RpcResponseBuilder.buildSuccessRpcResp("数据新增成功！", addFocusSecurity + "");

					} else {
						logger.error("addFocusSecurity()——>erro，数据插入失败！");
						return RpcResponseBuilder.buildErrorRpcResp("数据插入失败！");
					}
				}

				logger.error("addFocusSecurity()——>erro，数据插入失败，组织下没有绑定的设备！");
				return RpcResponseBuilder.buildErrorRpcResp("数据插入失败,组织下没有绑定的设备！");

			}
			logger.error("addFocusSecurity()——>参数错误");
			return RpcResponseBuilder.buildErrorRpcResp("参数错误！");
		} catch (Exception e) {
			logger.error("addFocusSecurity()->excpetion", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}
	}



	/**
	 * @see com.run.locman.api.crud.service.FocusSecurityCrudService#enabledFocusSecurity(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> enabledFocusSecurity(JSONObject json) {
		logger.info(String.format("enabledFocusSecurity()->方法开始执行", json.toJSONString()));

		try {
			if (json.isEmpty()) {
				return RpcResponseBuilder.buildErrorRpcResp("参数不能為空！");
			}
			if (StringUtils.isBlank(json.getString("securityId"))) {
				return RpcResponseBuilder.buildErrorRpcResp("id不能為空！");
			}
			if (StringUtils.isBlank(json.getString("status"))) {
				return RpcResponseBuilder.buildErrorRpcResp("状态不能為空！");
			}
			String securityId = json.getString("securityId");
			String status = json.getString("status");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("securityId", securityId);
			map.put("status", status);
			map.put("accessSecret",json.getString("accessSecret"));
			Integer enabledFocusSecurity = focusSecurityCrudRepository.enabledFocusSecurity(map);
			if (enabledFocusSecurity > 0) {
				//关闭定时器
				focusSecurityTimerCrudService.closeFocusSecurityIssued(securityId);
				
				//下发命令
				
				//根据重保工单id查询设施
				List<String> findFacIdListBySecurityId = focusSecurityCrudRepository.findFacIdListBySecurityId(securityId);
				FocusSecurityCrudServiceImpl focusSecurityCrudServiceImpl = new FocusSecurityCrudServiceImpl();
				JSONObject command=new JSONObject();
				command.put("defen_state", 0);
				for(String facilityId:findFacIdListBySecurityId) {
					
					focusSecurityCrudServiceImpl.operateLock(facilityId, command, securityId);
				}
				
				
				logger.info("enabledFocusSecurity()——>info，数据操作成功！");
				return RpcResponseBuilder.buildSuccessRpcResp("操作成功", status);
			}
			logger.error("enabledFocusSecurity()——>erro,数据操作失败！");
			return RpcResponseBuilder.buildErrorRpcResp("操作失败");
		} catch (Exception e) {

			logger.error("enabledFocusSecurity()->excpetion", e);

			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.FocusSecurityCrudService#operateLock(java.lang.String,
	 *      com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> operateLock(String facId, JSONObject command,String securityId) {
		// TODO Auto-generated method stub

		logger.info(String.format("[operateLock()->进入方法，facId:%s,command:%s]", facId, command.toString()));
		try {
			if (facId == null) {
				logger.error("[operateLock()->error：facId为空]");
				return RpcResponseBuilder.buildErrorRpcResp("设施id为空");
			}
			if (command.isEmpty()) {
				logger.error("[operateLock()->error：指令为空]");
				return RpcResponseBuilder.buildErrorRpcResp("指令为空");
			}
			if(null ==securityId || securityId=="") {
				logger.error("[operateLock()->error：securityId为空]");
				return RpcResponseBuilder.buildErrorRpcResp("重点保障id为空");
			}
			List<Map<String, Object>> deviceInfoList = focusSecurityCrudRepository.getDeviceInfoByFacId(facId);
			if (null == deviceInfoList || deviceInfoList.isEmpty()) {
				logger.error("[operateLock()->error：设施未绑定设备]");
				return RpcResponseBuilder.buildErrorRpcResp("该设施未绑定设备");
			}else {
				Set<String> set = new HashSet<>();
				ArrayList<String> arrayList = new ArrayList<>();
				for(Map<String,Object> deviceInfo:deviceInfoList) {
						String deviceId = (String) deviceInfo.get("deviceId");
						String deviceTypeId = (String) deviceInfo.get("deviceTypeId");
						
						Map<String, String> productApiKeyTable = wingsIotConstansQuery.getProductApiKeyTable();
						RpcResponse<JSONObject> sendCommand = WingIotCommand.sendCommandToSingleDevice(deviceId, deviceTypeId,
								command,productApiKeyTable,"defen_closeLock");
						String commandStatu = null;
						if (!sendCommand.isSuccess()) {
							logger.error(String.format("operateLock()->error：发送指令失败设备Id：%s", deviceId));
							logger.error(String.format("[operateLock()->error：发送指令失败---%s]", sendCommand.getMessage()));
							commandStatu = "0";
							set.add(commandStatu);
						}else {
							logger.info(String.format("operateLock()->error：发送指令成功设备Id：%s", deviceId));
							logger.info(String.format("[operateLock()->success：发送指令成功---%s]",
									sendCommand.getSuccessValue().toJSONString()));
							commandStatu = "1";
							set.add(commandStatu);
						}
					}
				arrayList.addAll(set);
				String commandStatus=null;
				String status=null;
				if(deviceInfoList.size() > 1) {
					if(null !=arrayList && arrayList.size() > 0) {
						if(!arrayList.contains("1")) {
							commandStatus="发送失败";
							status="0";
						}else {
							commandStatus="指令已发送";
							status="1";
						}
					}
				}else {
						String string=arrayList.get(0);
						if("0".equals(string)) {
							commandStatus="发送失败";
							status="0";
						}else {
							commandStatus="指令已发送";
							status="1";
						}
					}
					
					String str = command.getString("defen_state");
					if("1".equals(str)) {
						int res = focusSecurityCrudRepository.updateIotReceivingStatus(facId, commandStatus,securityId);
						if (res > 0) {
							logger.info("[operateLock()->success：指令状态保存成功]");
							return RpcResponseBuilder.buildSuccessRpcResp("指令发送成功", "success");
						}else {
							logger.error("[operateLock()->fail：指令状态保存失败]");
							return RpcResponseBuilder.buildErrorRpcResp("指令发送成功，状态更新失败");
						}
					}else if("0".equals(str)){
						int res = focusSecurityCrudRepository.updateEndStatus(facId, status,securityId);
						if (res > 0) {
							logger.info("[operateLock()->success：指令状态保存成功]");
							return RpcResponseBuilder.buildSuccessRpcResp("指令发送成功", "success");
						}else {
							logger.error("[operateLock()->fail：指令状态保存失败]");
							return RpcResponseBuilder.buildErrorRpcResp("指令发送成功，状态更新失败");
						}
				}
				
				return RpcResponseBuilder.buildErrorRpcResp("指令发送异常！");
			}
		} catch (Exception e) {
			logger.error("operateLock()->excpetion", e);

			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}
	}



	/**
	 * @see com.run.locman.api.crud.service.FocusSecurityCrudService#querySecurityFacilitiesOrders()
	 */
	@Override
	public RpcResponse<Integer> querySecurityFacilitiesOrders() {
		
		try {
			JSONObject command = new JSONObject();
			command.put("defen_state", 0);
			List<Map<String, Object>> querySecurityFacIdList = focusSecurityCrudRepository.querySecurityFacIdList();
			int querySecurityFacilitiesOrders = focusSecurityCrudRepository.querySecurityFacilitiesOrders();
			if(querySecurityFacilitiesOrders < 0) {
				logger.info("重保时间结束，更改工单状态失败！");
				return RpcResponseBuilder.buildErrorRpcResp("重保时间结束，更改工单状态失败");
			}
			if(null !=querySecurityFacIdList && querySecurityFacIdList.size()>0) {
				for(Map<String,Object> facInfo:querySecurityFacIdList) {
					String facId =facInfo.get("facilityId")+"";
					String securityId=facInfo.get("securityId")+"";
					operateLock(facId, command,securityId);
				}
			}
			logger.info("重保时间结束，更改工单状态成功！");
			return RpcResponseBuilder.buildErrorRpcResp("重保时间结束，更改工单状态成功！");
			
		}catch(Exception e) {
			logger.error("querySecurityFacilitiesOrders()->excpetion", e);

			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
