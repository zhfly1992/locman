/*
 * File name: BalanceSwitchStateRecordCurdServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年5月15日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.BalanceSwitchStateRecordCurdRepository;
import com.run.locman.api.crud.service.BalanceSwitchStateRecordCurdService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.locman.constants.DeviceTypeContants;
import com.run.locman.constants.FacilityDeviceContants;
import com.run.locman.constants.PublicConstants;

/**
 * @Description:
 * @author: 王胜
 * @version: 1.0, 2018年5月15日
 */
@Transactional(rollbackFor = Exception.class)
public class BalanceSwitchStateRecordCurdServiceImpl implements BalanceSwitchStateRecordCurdService {

	private Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	BalanceSwitchStateRecordCurdRepository	stateRecordCurdRepository;



	/**
	 * @see com.run.locman.api.crud.service.BalanceSwitchStateRecordCurdService#openOrClose(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Boolean> openOrClose(JSONObject jsonParam) {
		try {
			if (null == jsonParam) {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，参数不能为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(DeviceContants.DEVICEID))) {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，设备deviceId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，设备deviceId不能为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(DeviceTypeContants.DEVICETYPEID))) {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，设备类型deviceTypeId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，设备类型deviceTypeId不能为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(FacilityDeviceContants.FACILITY_ID))) {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，设施facilityId不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，设施facilityId不能为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(DeviceContants.STATE))) {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，开启或关闭状态state不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，开启或关闭状态state不能为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(PublicConstants.ACCESSSECRET))) {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，接入方秘钥accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，接入方秘钥accessSecret不能为空!");
			}

			// 设置开启关闭时间
			if (DeviceContants.OPEN.equals(jsonParam.getString(DeviceContants.STATE)) || DeviceContants.CLOSE.equals(jsonParam.getString(DeviceContants.STATE))) {

			} else {
				logger.error("[openOrClose():valid--fail:保存平衡开关开启或关闭记录，state值只能是open或者colse!" + "状态:" + jsonParam.getString(DeviceContants.STATE) + "]");
				return RpcResponseBuilder.buildErrorRpcResp("保存平衡开关开启或关闭记录，state值只能是open或者colse!");
			}

			jsonParam.put("id", UtilTool.getUuId());
			jsonParam.put("operationTime", DateUtils.formatDate(new Date()));

			logger.info(String.format("[openOrClose()->info:参数:%s]",jsonParam));
			int save = stateRecordCurdRepository.openOrClose(jsonParam);
			if (save > 0) {
				logger.info("[openOrClose()--success:操作成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("操作成功!", Boolean.TRUE);
			}

			logger.error("[openOrClose()--fail:操作失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("操作失败!");

		} catch (Exception e) {
			logger.error("openOrClose()-->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
