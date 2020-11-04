/*
 * File name: FocusSecurityRestCrudService.java
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

package com.run.locman.crud.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.FocusSecurityCrudService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: 钟滨远
 * @version: 1.0, 2020年4月26日
 */
@Service
public class FocusSecurityRestCrudService {

	private Logger logger = Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FocusSecurityCrudService focusSecurityCrudService;
	@Autowired
	private HttpServletRequest		request;


	public Result<String> addFocusSecurity(String params) {
		logger.info(String.format("addFocusSecurity()——>方法开始执行params： %s", params));
		try {
			if(StringUtils.isBlank(params)) {
				return ResultBuilder.failResult("params不能为空！");
			}
			JSONObject json=JSONObject.parseObject(params);
			String securityName=json.getString("securityName")+"";
			String startTime=json.getString("startTime")+"";
			String endTime=json.getString("endTime")+"";
			String orgId=json.getString("organization")+"";
			String personName=json.getString("personName");
			String personTel=json.getString("personTel");
			
//			String createTime=json.getString("createTime")+"";
//			String userId=json.getString("userId")+"";
//			String organization=json.getString("organization")+"";
//			String status=json.getString("status")+"";
			String previewTime=json.getString("previewTime")+"";
			
			if(StringUtils.isBlank(securityName)) {
				return ResultBuilder.failResult("保障名称不能为空！");
			}
			if(StringUtils.isBlank(startTime)) {
				return ResultBuilder.failResult("开始时间不能为空！");
			}
			if(StringUtils.isBlank(endTime)) {
				return ResultBuilder.failResult("保障结束时间不能为空！");
			}
			if(StringUtils.isBlank(orgId)) {
				return ResultBuilder.failResult("保障道路不能为空！");
			}
			if(StringUtils.isBlank(personName)) {
				return ResultBuilder.failResult("负责人不能为空！");
			}
			if(StringUtils.isBlank(personTel)) {
				return ResultBuilder.failResult("负责人手机不能为空！");
			}
			//时间判断
			String checkFocusSecurityStartTimeAndEndTime = UtilTool.checkFocusSecurityStartTimeAndEndTime(startTime, endTime);

			if (null != checkFocusSecurityStartTimeAndEndTime) {
				return ResultBuilder.failResult(checkFocusSecurityStartTimeAndEndTime);
			}
			if(null != previewTime && "" != previewTime) {
				if(!StringUtils.isInteger(previewTime)) {
					return ResultBuilder.failResult("预演时间必须是正整数！");
				}else {
					if(Integer.valueOf(previewTime) <0) {
						return ResultBuilder.failResult("预演时间必须是正整数！");
					}
				}
				
			}
			
			json.put("token", request.getHeader("Token"));
			
			RpcResponse<String> addFocusSecurity = focusSecurityCrudService.addFocusSecurity(json);
			if(addFocusSecurity.isSuccess()) {
				logger.info("addFocusSecurity()——>新增成功！"+addFocusSecurity.getMessage());
				return ResultBuilder.successResult(addFocusSecurity.getMessage(), "新增成功");
			}
			logger.error("addFocusSecurity()——>新增失败！"+addFocusSecurity.getMessage());
			return ResultBuilder.failResult("新增失败"+","+addFocusSecurity.getMessage());
		}catch(Exception e) {
			logger.error("addFocusSecurity()->exception", e);
			return	ResultBuilder.exceptionResult(e);
		}
		

	}
	
	public Result<String>  enabledFocusSecurity(JSONObject json){
		
		try {
			if(StringUtils.isBlank(json.toJSONString())) {
				return ResultBuilder.failResult("保障ID不能为空！");
			}
			RpcResponse<String> enabledFocusSecurity = focusSecurityCrudService.enabledFocusSecurity(json);
			if(enabledFocusSecurity.isSuccess()) {
				logger.info("addFocusSecurity()——>成功！"+enabledFocusSecurity.getMessage());
				return ResultBuilder.successResult(enabledFocusSecurity.getMessage(), "停用成功");
			}
			logger.error("addFocusSecurity()——>失败！"+enabledFocusSecurity.getMessage());
			return ResultBuilder.failResult("停用失败");
			
		}catch(Exception e) {
			logger.error("addFocusSecurity()->exception", e);
			return	ResultBuilder.exceptionResult(e);
		}
		
	}
	
	
	public  Result<String> testSendCommand(JSONObject jsonObject){
		String facId = jsonObject.getString("facId");
		JSONObject command = jsonObject.getJSONObject("command");
		String securityId =jsonObject.getString("securityId");
		RpcResponse<String> operateLock = focusSecurityCrudService.operateLock(facId, command,securityId);
		if (operateLock.isSuccess()) {
			return ResultBuilder.successResult("success", operateLock.getSuccessValue());
		}
		return ResultBuilder.failResult(operateLock.getMessage());
	}

}
