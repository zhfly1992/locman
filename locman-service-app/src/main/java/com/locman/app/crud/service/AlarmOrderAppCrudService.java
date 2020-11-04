package com.locman.app.crud.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.locman.app.query.service.UscQueryService;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.constants.CommonConstants;

@Service
public class AlarmOrderAppCrudService extends BaseAppController{

	private static final Logger	logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	CommonService				commonService;
	
	@Autowired
	private UscQueryService					uscQueryService;

	private String				path	= "alarmOrder/";



	/**
	 * 生成告警工单 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result<String> saveAlarmOrder(String param) {

		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSON.parseObject(param);
			if (!json.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			json.put("accessSecret", Constant.GETACCESSSECRET(json.getString("userId")));
			String url = path + "saveAlarmOrder";
			logger.info("生成告警工单完成");
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, json.toString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			String value = "";
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "操作成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 处理告警工单 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result<String> updateAlarmOrderState(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject parseObject = JSON.parseObject(param);
			if (!parseObject.containsKey("apply_userId")) {
				return ResultBuilder.noBusinessResult();
			}
			parseObject.put("accessSecret", Constant.GETACCESSSECRET(parseObject.getString("apply_userId")));
			String type = parseObject.getString("oper");
			if (Constant.ALAOPERATIONTYPE.COMPLATE.equals(type)) {// 正常完成告警工单
				String url = path + "complete";
				parseObject.remove("oper");
				parseObject.put("orderPic", parseObject.get("orderImg"));
				parseObject.remove("orderImg");
				parseObject.put("orderType", "0");
				parseObject.put("tId", Constant.ALARM.ALARM_ORDETYPE_ID);
				logger.info("完成告警工单==》 " + parseObject.toJSONString());
				logger.info("告警工单处理完成");
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, parseObject.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "操作成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			} else if (Constant.ALAOPERATIONTYPE.POWERLESS.equals(type)) {// 无法修复
				String url = path + "powerless";
				parseObject.remove("oper");
				parseObject.put("powerless_orderType", "0");
				parseObject.put("powerless_remark", parseObject.get("remark"));
				parseObject.put("powerless_userId", parseObject.get("apply_userId"));
				parseObject.remove("apply_userId");
				parseObject.put("powerless_orderImg", parseObject.get("orderImg"));
				parseObject.remove("orderImg");
				logger.info("告警工单无法修复");
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, parseObject.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "操作成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			} else if (Constant.ALAOPERATIONTYPE.REJECT.equals(type)) {// 拒绝
				String url = path + "reject";
				parseObject.put("reject_remark", parseObject.get("remark"));
				parseObject.put("reject_userId", parseObject.get("apply_userId"));
				parseObject.remove("apply_userId");
				logger.info("告警工单审批拒绝");
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, parseObject.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "操作成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			} else if (Constant.ALAOPERATIONTYPE.APPROVE.equals(type)) {// 通过
				if (!parseObject.containsKey("organizeId")) {
					return ResultBuilder.noBusinessResult();
				}
				String url = path + "approve";
				parseObject.put("approve_userId", parseObject.get("apply_userId"));
				parseObject.remove("apply_userId");
				logger.info("告警工单审批通过");
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, parseObject.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(value, "操作成功");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			}
			return ResultBuilder.failResult("未选择处理动作");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 接受告警工单 <method description>
	 *
	 * @param param
	 * @return
	 */
	public Result<String> acceptOrder(String param) {
		try {
			String url = path + "acceptAlarmOrder";
			JSONObject parseObject = JSONObject.parseObject(param);
			if (!parseObject.containsKey("userId")) {
				return ResultBuilder.noBusinessResult();
			}
			String accessSecret = Constant.GETACCESSSECRET(parseObject.getString("userId"));
			parseObject.put("accessSecret", accessSecret);
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, parseObject.toString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			String value = "";
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "操作成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 告警工单上传到场图片 <method description>
	 *
	 * @param ordId
	 * @param files
	 * @return
	 */
	public Result<String> uploadAlarmOrderPic(String ordId, MultipartFile[] files) {
		try {
			if (StringUtils.isEmpty(ordId)) {
				return ResultBuilder.noBusinessResult();
			}
			if (files == null) {
				return ResultBuilder.noBusinessResult();
			}
			if (files.length > 1) {
				return ResultBuilder.failResult("只能上传一张图片！");
			}
			Result<List<String>> picUrls = commonService.uploadFile(files);
			if (picUrls == null || picUrls.getValue() == null || picUrls.getValue().size() == 0) {
				return ResultBuilder.failResult("图片上传失败！");
			}
			String picUrl = picUrls.getValue().get(0);
			String url = path + "addAppPic";
			JSONObject parseObject = new JSONObject();
			parseObject.put("id", ordId);
			parseObject.put("picUrl", picUrl);
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, parseObject.toString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			String value = "";
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "操作成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 告警工单审批操作 
	 *告警工单完工审批  通过或拒绝
	 * @param orderId
	 * @param param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Result<Boolean> approvalAlarmOrder(String orderId, String param) {
		try {
			if (StringUtils.isEmpty(orderId)) {
				return ResultBuilder.noBusinessResult();
			}
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJSON = JSONObject.parseObject(param);
			if(!paramJSON.containsKey("approvalState")) {
				return ResultBuilder.noBusinessResult();
			}
			String approvalState = paramJSON.getString("approvalState");
			if(!paramJSON.containsKey("processId")) {
				return ResultBuilder.noBusinessResult();
			}
			if(!paramJSON.containsKey("processState")) {
				return ResultBuilder.noBusinessResult();
			}
			if(StringUtils.isEquals(approvalState, "reject")) {
				if(!paramJSON.containsKey("reject_remark")) {
					return ResultBuilder.noBusinessResult();
				}
			}
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			paramJSON.put("orderId", orderId);
			paramJSON.put("accessSecret", accessSecret);
			paramJSON.remove("approvalState");
			if(StringUtils.isEquals(approvalState, "reject")) {
				paramJSON.put("rejectCompleteUserId", userId);
				String url = path + "alarmOrderRejectBeforeEnd";
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, paramJSON.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				logger.info("拒绝告警工单完成");
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(true, "拒绝完成");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			}else {
				paramJSON.put("approveCompleteUserId", userId);
				String url = path + "alarmOrderApprovalBeforeEnd";
				String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, paramJSON.toString());
				Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
				String value = "";
				logger.info("审核告警工单完成");
				if (resultValue.containsKey("success")) {
					value = resultValue.get("success");
					return ResultBuilder.successResult(true, "审核通过");
				} else {
					value = resultValue.get("error");
					return ResultBuilder.failResult(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	
	
	
	/**
	 * 
	* 告警工单设备整治
	*
	* @param param
	* @return
	 */
	public Result<?> updateFacilitiesDenfenseState(String orderId, String param){
		try {
			if (StringUtils.isEmpty(orderId)) {
				return ResultBuilder.noBusinessResult();
			}
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject paramJSON = JSONObject.parseObject(param);
			String token = request.getHeader("token");
			RpcResponse userRpc = uscQueryService.getUserByToken(token);
			if (!userRpc.isSuccess() || userRpc.getSuccessValue() == null || userRpc.getSuccessValue().equals("")) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = JSONObject.parseObject(userRpc.getSuccessValue().toString());
			String userId = userJson.getString("_id");
			String accessSecret = Constant.GETACCESSSECRET(userId);
			paramJSON.put("userId", userId);
			paramJSON.put("accessSecret", accessSecret);
			paramJSON.put("defenseState", 3);
			paramJSON.put("alarmOrderId", orderId);
			String url = "facilities/updateFacilitiesDenfenseState";
			String jsonResult = commonService.requestRest(Constant.LOCMAN_PORT, url, paramJSON.toString());
			Map<String, String> resultValue = commonService.checkResult(JSONObject.parseObject(jsonResult));
			String value = "";
			logger.info("设施待整治项提交完成");
			if (resultValue.containsKey("success")) {
				value = resultValue.get("success");
				return ResultBuilder.successResult(value, "提交成功");
			} else {
				value = resultValue.get("error");
				return ResultBuilder.failResult(value);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}
	
}
