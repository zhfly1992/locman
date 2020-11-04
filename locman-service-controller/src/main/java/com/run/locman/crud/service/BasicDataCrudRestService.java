/*
 * File name: BasicDataCrudRestService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年4月23日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.AlarmRuleService;
import com.run.locman.api.crud.service.BaseFacilitiesTypeCrudService;
import com.run.locman.api.crud.service.BasicDeviceAttributeTemplateCurdService;
import com.run.locman.api.crud.service.BasicDeviceInfoConvertCrudService;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.api.entity.DeviceInfoConvert;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.BasicDataConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 基础数据同步rest
 * @author: zhaoweizhi
 * @version: 1.0, 2018年4月23日
 */
@Service
public class BasicDataCrudRestService {

	private Logger									logger	= Logger.getLogger(com.run.locman.constants.CommonConstants.LOGKEY);

	@Autowired
	private BasicDeviceAttributeTemplateCurdService	basicDeviceAttributeTemplateCurdService;

	@Autowired
	AlarmRuleQueryService							alarmRuleQueryService;

	@Autowired
	AlarmRuleService								alarmRuleService;

	@Autowired
	private BaseFacilitiesTypeCrudService			baseFacilitiesTypeCrudService;

	@Autowired
	private BasicDeviceInfoConvertCrudService		basicDeviceInfoConvertCrudService;
	@Autowired
	private DeviceInfoConvertQueryService			deviceInfoConvertQueryService;



	/**
	 * 
	 * @Description:同步设施类型
	 * @param synchronousInfo
	 *            {accessSecret:接入方密钥;userId:用户id(用作创建人)}
	 * @return Boolean true同步成功 false同步失败
	 */
	public Result<Boolean> basicFacilitiesSynchronous(String synchronousInfo) {
		// 参数校验
		logger.info(String.format("[basicFacilitiesSynchronous()->request params:%s]", synchronousInfo));
		Result<String> result = ExceptionChecked.checkRequestParam(synchronousInfo);
		if (result != null) {
			logger.error(String.format("[basicFacilitiesSynchronous->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(synchronousInfo);
			String accessSecret = json.getString(BasicDataConstants.USC_ACCESS_SECRET);
			String userId = json.getString(BasicDataConstants.USER_ID);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("接入方密钥不能为空!!!");
				return ResultBuilder.invalidResult();
			}
			if (StringUtils.isBlank(userId)) {
				logger.error("创建人不能为空!!!");
				return ResultBuilder.invalidResult();
			}
			// 查询该接入方同步信息状态,true可以同步
			RpcResponse<BaseDataSynchronousState> synchronousState = baseFacilitiesTypeCrudService
					.getSynchronousStateByAS(accessSecret);
			if (!synchronousState.isSuccess() || synchronousState.getSuccessValue() == null) {
				logger.error("查询同步信息失败或同步信息不存在!!!");
				return ResultBuilder.failResult("查询同步信息失败或同步信息不存在");
			}
			BaseDataSynchronousState successValue = synchronousState.getSuccessValue();
			if (!successValue.getBaseFacilitiesType()) {
				logger.error(String.format("[basicFacilitiesSynchronous()-->%s]", "设施类型数据同步失败!该接入方已同步过设施类型数据"));
				return ResultBuilder.failResult("设施类型数据同步失败!该接入方已同步过设施类型数据");
			}

			RpcResponse<Boolean> bfsStateByAS = baseFacilitiesTypeCrudService.getBFSStateByAS(accessSecret);
			if (!bfsStateByAS.isSuccess() || bfsStateByAS.getSuccessValue() == null) {
				logger.error("查询是否存在设施类型基础数据失败!!!");
				return ResultBuilder.failResult("查询是否存在设施类型基础数据失败!!!");
			}
			if (!bfsStateByAS.getSuccessValue()) {
				logger.error("该接入方已存在remark为'设施类型根数据'的数据,不能同步!!!");
				return ResultBuilder.failResult("该接入方已存在设施类型同步数据,不能同步!!!");
			}

			RpcResponse<Boolean> basicFacilitiesSynchronous = baseFacilitiesTypeCrudService
					.basicFacilitiesSynchronous(accessSecret, userId);
			if (!basicFacilitiesSynchronous.isSuccess() || basicFacilitiesSynchronous.getSuccessValue() == null) {
				logger.error("设施类型数据同步失败!");
				return ResultBuilder.failResult("设施类型数据同步失败!");
			} else {
				logger.info("设施类型数据同步成功!");
				return ResultBuilder.successResult(basicFacilitiesSynchronous.getSuccessValue(), "设施类型数据同步成功!");
			}
		} catch (Exception e) {
			logger.error("basicFacilitiesSynchronous()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:同步设备属性模板表
	 * @param accessSecret
	 * @return
	 */
	public Result<Boolean> basicDeviceAttributeTemplate(String accessSecret) {
		try {
			logger.info(String.format("[basicDeviceAttributeTemplate()->request params:%s]", accessSecret));
			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[BasicDeviceAttributeTemplate()-->%s-->%s]", "accessSecret",
						PublicConstants.BUSINESS_INVALID));
				return ResultBuilder.noBusinessResult();
			}

			JSONObject accObj = JSONObject.parseObject(accessSecret);

			// 同步设备属性模板数据 rpc
			RpcResponse<Boolean> basicDeviceAttrbuteTemplate = basicDeviceAttributeTemplateCurdService
					.basicDeviceAttrbuteTemplate(accObj.getString(PublicConstants.ACCESSSECRET));

			if (!basicDeviceAttrbuteTemplate.isSuccess()) {
				logger.error(String.format("[BasicDeviceAttributeTemplate()-->%s]",
						basicDeviceAttrbuteTemplate.getMessage()));
				return ResultBuilder.failResult(basicDeviceAttrbuteTemplate.getMessage());
			} else {
				logger.error(String.format("[BasicDeviceAttributeTemplate()-->%s]",
						basicDeviceAttrbuteTemplate.getMessage()));
				return ResultBuilder.successResult(basicDeviceAttrbuteTemplate.getSuccessValue(),
						basicDeviceAttrbuteTemplate.getMessage());
			}

		} catch (Exception e) {
			logger.error("BasicDeviceAttributeTemplate()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:同步基础通用告警规则
	 * @param accessSecretParam
	 * @return
	 */
	public Result<Boolean> basicAlarmRuleAdd(String accessSecretParam) {
		try {
			logger.info(String.format("[basicAlarmRuleAdd()->request params:%s]", accessSecretParam));
			if (null == accessSecretParam || ParamChecker.isNotMatchJson(accessSecretParam)) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "参数为空或不是json格式!"));
				return ResultBuilder.failResult("参数为空或不是json格式!");
			}
			JSONObject parseObject = JSONObject.parseObject(accessSecretParam);
			String accessSecret = parseObject.getString(PublicConstants.ACCESSSECRET);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "接入方秘钥不能为空!"));
				return ResultBuilder.failResult("接入方秘钥不能为空!");
			}
			if (StringUtils.isBlank(parseObject.getString(CommonConstants.USERID))) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "用户userId不能为空!"));
				return ResultBuilder.failResult("用户userId不能为空!");
			}

			// 校验是否有告警规则
			RpcResponse<List<AlarmRule>> allAlarmRule = alarmRuleQueryService.getAllAlarmRule(accessSecret);
			if (null == allAlarmRule || !allAlarmRule.isSuccess()) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "校验原是否有告警数据失败!"));
				return ResultBuilder.failResult("校验原是否有告警数据失败!");
			} else if (allAlarmRule.getSuccessValue().size() > 0) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "已有告警规则,若需同步基础告警规则，需清除该接入方下原所有告警规则!"));
				return ResultBuilder.failResult("已有告警规则,若需同步基础告警规则，需清除该接入方下原所有告警规则!");
			}

			// 获取基础告警规则
			RpcResponse<List<Map<String, Object>>> allBasicAlarmRule = alarmRuleQueryService.getAllBasicAlarmRule();
			if (null == allBasicAlarmRule || !allBasicAlarmRule.isSuccess()) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "获取基础告警规则失败!"));
				return ResultBuilder.failResult(allBasicAlarmRule.getMessage());
			} else if (allBasicAlarmRule.getSuccessValue().size() == 0) {
				logger.error(String.format("[basicAlarmRuleAdd()-->%s]", "暂无基础告警规则!"));
				return ResultBuilder.failResult("暂无基础告警规则!");
			}

			String crateTime = DateUtils.formatDate(new Date());
			// 更新接入方秘钥和id
			List<Map<String, Object>> basicAlarmRule = allBasicAlarmRule.getSuccessValue();
			for (Map<String, Object> map : basicAlarmRule) {
				String id = UtilTool.getUuId();
				map.put(CommonConstants.ID, id);
				map.put(PublicConstants.ACCESSSECRET, accessSecret);
				map.put("userId", parseObject.getString("userId"));
				map.put("crateTime", crateTime);
				map.put("updateTime", crateTime);
			}

			RpcResponse<String> basicAlarmRuleAdd = alarmRuleService.basicAlarmRuleAdd(basicAlarmRule);
			if (null == basicAlarmRuleAdd) {
				logger.error("同步基础通用告警规则失败");
				return ResultBuilder.failResult("同步基础通用告警规则失败");
			}
			if (null != basicAlarmRuleAdd && basicAlarmRuleAdd.isSuccess()) {
				logger.info(String.format("[basicAlarmRuleAdd()success:-->%s]", basicAlarmRuleAdd.getMessage()));
				return ResultBuilder.successResult(Boolean.TRUE, basicAlarmRuleAdd.getMessage());
			}
			logger.error(String.format("[basicAlarmRuleAdd()fail:-->%s]", basicAlarmRuleAdd.getMessage()));
			return ResultBuilder.failResult(basicAlarmRuleAdd.getMessage());
		} catch (Exception e) {
			logger.error("basicAlarmRuleAdd()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description: 基础数据同步情况查询(第一次查询会自动创建相关信息)
	 * @param accessSecret:密钥
	 * @return BaseDataSynchronousState
	 */

	public Result<BaseDataSynchronousState> getSynchronousState(String accessSecret) {
		try {
			logger.info(String.format("[getSynchronousState()->request params:%s]", accessSecret));
			if (StringUtils.isBlank(accessSecret)) {
				logger.error(String.format("[getSynchronousState()-->%s]", "接入方密钥为空!"));
				return ResultBuilder.emptyResult();
			}
			RpcResponse<BaseDataSynchronousState> synchronousState = baseFacilitiesTypeCrudService
					.getSynchronousStateByAS(accessSecret);
			if (!synchronousState.isSuccess()) {
				logger.error(String.format("[getSynchronousState()-->%s]", "基础数据同步情况查询失败!"));
				return ResultBuilder.failResult("基础数据同步情况查询失败");
			} else {
				logger.info(String.format("[getSynchronousState()success:-->%s]", synchronousState.getMessage()));
				return ResultBuilder.successResult(synchronousState.getSuccessValue(), synchronousState.getMessage());
			}
		} catch (Exception e) {
			logger.error("getSynchronousState()-->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Boolean> basicDeviceInfoConvertAdd(String accessSecret) {

		try {
			logger.info(String.format("[basicDeviceInfoConvertAdd()->request params:%s]", accessSecret));
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("错误，传入参数不能为空");
				return ResultBuilder.emptyResult();
			}
			if (ParamChecker.isNotMatchJson(accessSecret)) {
				logger.error("传入参数必须为json格式");
				return ResultBuilder.invalidResult();
			}
			JSONObject json = JSONObject.parseObject(accessSecret);
			String accessSecretId = json.getString("accessSecret");
			// 参数密匙是否为空
			if (StringUtils.isBlank(accessSecretId)) {
				logger.error("错误，接入方密匙为空");
				return ResultBuilder.failResult("错误，接入方密匙为空");
			}
			// 检验是否有特殊值转换
			RpcResponse<List<DeviceInfoConvert>> basicDeviceInfoConvert = deviceInfoConvertQueryService
					.getDeviceInfoConvert(accessSecretId);
			if (null == basicDeviceInfoConvert || !basicDeviceInfoConvert.isSuccess()) {
				logger.error(String.format("[ basicDeviceInfoConvertAdd()-->%s]", "校验是否有特殊值失败!"));
				return ResultBuilder.failResult("校验是否有告警数据失败!");
			} else if (basicDeviceInfoConvert.getSuccessValue().size() > 0) {
				logger.error(String.format("[ basicDeviceInfoConvertAdd()-->%s]", "已有特殊值转换,若需同步，需清除该接入方下原所有特殊值转换!"));
				return ResultBuilder.failResult("已有特殊值转换,若需同步，需清除该接入方下原所有特殊值转换!");
			}
			// 同步特殊值转换
			RpcResponse<Boolean> res = basicDeviceInfoConvertCrudService.basicDeviceInfoConvertadd(accessSecretId);
			if (res.isSuccess()) {
				return ResultBuilder.successResult(res.getSuccessValue(), "特殊值转换同步成功");
			}
			return ResultBuilder.failResult("fail，特殊值转换同步失败");
		} catch (Exception e) {
			logger.error("convertInfoAdd()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
