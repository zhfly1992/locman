/*
 * File name: OrderProcessRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月02日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.activity.api.crud.service.ProcessFileCurdService;
import com.run.common.util.ExceptionChecked;
import com.run.common.util.ParamChecker;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.OrderProcessCudService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.OrderProcessContants;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
@Service
public class OrderProcessRestCudService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private OrderProcessCudService		orderProcessCudService;

	@Autowired
	private ProcessFileCurdService		processFileCurdService;

	@Autowired
	private OrderProcessQueryService	orderProcessQueryService;

	private static final String			XML		= "xml";

	private static final int			NUMBER	= 51200;



	public Result<String> addOrderProcess(String orderProcessInfo) {
		logger.info(String.format("[addOrderProcess()->request params:%s]", orderProcessInfo));
		try {
			// 参数格式校验
			Result<String> result = ExceptionChecked.checkRequestParam(orderProcessInfo);
			if (result != null) {
				logger.error("数据不满足json格式!");
				return ResultBuilder.invalidResult();
			}
			JSONObject orderInfo = JSONObject.parseObject(orderProcessInfo);
			RpcResponse<String> resultInfo = orderProcessCudService.addOrderProcess(orderInfo);
			if (resultInfo.isSuccess()) {
				logger.info("添加成功!");
				return ResultBuilder.successResult(resultInfo.getSuccessValue(), resultInfo.getMessage());
			}
			logger.error("添加失败");
			return ResultBuilder.failResult(resultInfo.getMessage());
		} catch (Exception e) {
			logger.error("addOrderProcess()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * <method description> 管理启用/停用状态
	 *
	 * @param updateInfo
	 *            { "id":"工单流程id", "updateBy":"修改人",
	 *            "manageState":"工单流程修改后的状态(只能是enabled启用 /disabled停用)"
	 *            "fileId":"xml文件id" }
	 *
	 * @return "id":"工单流程id"
	 */

	public Result<String> updateState(String updateInfo) {
		logger.info(String.format("[updateState()->request params:%s]", updateInfo));
		if (ParamChecker.isBlank(updateInfo) || ParamChecker.isNotMatchJson(updateInfo)) {
			logger.error(LogMessageContants.PARAMETERS_OF_ILLEGAL);
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject json = JSONObject.parseObject(updateInfo);
			String processId = json.getString(OrderProcessContants.PROCESS_ID);
			String managerState = json.getString(OrderProcessContants.MANAGER_STATE);
			String updateBy = json.getString(OrderProcessContants.UPDATE_BY);
			String fileId = json.getString(OrderProcessContants.FILE_ID);
			String processTypeId = json.getString(OrderProcessContants.PROCESS_TYPE_ID);
			String accessSecret = json.getString(OrderProcessContants.USC_ACCESS_SECRET);
			
			if (StringUtils.isBlank(processId) || StringUtils.isBlank(managerState) || StringUtils.isBlank(updateBy)
					|| StringUtils.isBlank(fileId) || StringUtils.isBlank(processTypeId)
					|| StringUtils.isBlank(accessSecret)) {
				logger.error(LogMessageContants.NO_PARAMETER_EXISTS);
				return ResultBuilder.emptyResult();
			}
			if (OrderProcessContants.STATE_DISABLED.equals(managerState)
					|| OrderProcessContants.STATE_ENABLED.equals(managerState)) {
				String activityManagerState = OrderProcessContants.INVALID;
				if (OrderProcessContants.STATE_ENABLED.equals(managerState)) {
					activityManagerState = OrderProcessContants.VALID;
					HashMap<String, Object> queryInfo = Maps.newHashMap();
					queryInfo.put(OrderProcessContants.MANAGER_STATE, OrderProcessContants.STATE_ENABLED);
					queryInfo.put(OrderProcessContants.PROCESS_TYPE_ID, processTypeId);
					queryInfo.put(OrderProcessContants.USC_ACCESS_SECRET, accessSecret);
					// 判断该工单流程类型当前启用状态的工单流程配置的数量（同时只能启用一个工单流程配置）
					RpcResponse<Integer> countResult = orderProcessQueryService.countManagerState(queryInfo);
					if (!countResult.isSuccess() || countResult.getSuccessValue() == null) {
						logger.error("查询当前启用的类型工单流程配置失败，流程类型可能不存在");
						return ResultBuilder.failResult("查询当前启用的类型工单流程配置失败");
					}
					if (countResult.getSuccessValue() > 0) {
						logger.info("此类型工单流程已经存在启用状态的配置，同时只能启用一个工单流程配置，请先禁用！");
						return ResultBuilder.failResult("此类型工单流程已经存在启用状态的配置，同时只能启用一个工单流程配置，请先禁用！");
					}
				}
				// 先更改工作流xml文件的管理状态
				RpcResponse<Boolean> manageProcessFile = processFileCurdService.manageProcessFile(fileId,
						activityManagerState);
				if (!manageProcessFile.isSuccess() || manageProcessFile.getSuccessValue() == null) {
					logger.error(LogMessageContants.SAVE_FAIL);
					return ResultBuilder.failResult(manageProcessFile.getMessage());
				}
				if (!manageProcessFile.getSuccessValue()) {
					logger.info(LogMessageContants.UPDATE_FAIL + "工作流管理状态更新失败");
					return ResultBuilder.failResult(manageProcessFile.getMessage());
				}
				// 工作流管理状态更新成功再修改工单流程管理状态
				RpcResponse<String> resultInfo = orderProcessCudService.updateState(processId, managerState, updateBy,
						fileId);
				if (resultInfo.isSuccess() && resultInfo.getSuccessValue() != null) {
					logger.info(LogMessageContants.UPDATE_SUCCESS);
					return ResultBuilder.successResult(resultInfo.getSuccessValue(), resultInfo.getMessage());
				} else {
					logger.error(LogMessageContants.UPDATE_FAIL);
					return ResultBuilder.failResult(resultInfo.getMessage());
				}

			} else {
				logger.error(LogMessageContants.PARAMETERS_OF_ILLEGAL);
				return ResultBuilder.invalidResult();
			}
		} catch (Exception e) {
			logger.error("updateState()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description 解析工单流程模版xml文件
	 * @param XmlFile
	 *            MultipartFile文件对象
	 * @return 节点信息
	 */

	public Result<List<JSONObject>> parseXmlTmplate(MultipartFile xmlFile) {
		try {
			if (xmlFile == null) {
				logger.error("文件对象不存在");
				return ResultBuilder.noBusinessResult();
			}
			logger.info(String.format("[parseXmlTmplate()->OriginalFilename:%s]", xmlFile.getOriginalFilename()));
			String originalFilename = xmlFile.getOriginalFilename();
			String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			// 只能上传xml格式的文件
			if (!suffix.equalsIgnoreCase(XML)) {
				logger.error("上传失败,请上传正确的xml格式模板!");
				return ResultBuilder.failResult("上传失败,请上传正确的xml格式模板!");
			}
			// 只能上传不超过50KB的文件
			if (xmlFile.getSize() > NUMBER) {
				logger.error("上传失败,上传文件不能大于50KB!");
				return ResultBuilder.failResult("上传失败,上传文件不能大于50KB!");
			}
			// 获取文件字节数组
			byte[] bytes = xmlFile.getBytes();
			// 调用工作流接口解析xml文件
			RpcResponse<List<JSONObject>> parseBpmn = processFileCurdService.parseBpmn(bytes);
			if (parseBpmn.isSuccess() && parseBpmn.getSuccessValue() != null) {
				logger.info("上传成功!");
				return ResultBuilder.successResult(parseBpmn.getSuccessValue(), parseBpmn.getMessage());
			} else {
				logger.error("上传失败!:解析xml失败!");
				return ResultBuilder.failResult(parseBpmn.getMessage());
			}
		} catch (Exception e) {
			logger.error("uploadXmlTmplate()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description
	 *
	 * @param orderProcessInfo
	 *            {id 工单流程id, processTypeId 工单流程类型, updateBy 修改人, accessSecret
	 *            接入方秘钥, fileId, manageState, nodeInfo 节点信息 : node1 节点1 personId
	 *            人员id, organizeId 组织id, node2 personId; organizeId }
	 * @return id 工单流程id
	 */

	public Result<String> updateOrderProcess(String orderProcessInfo) {
		try {
			logger.info(String.format("[updateOrderProcess()->request params:%s]", orderProcessInfo));
			// 参数格式校验
			Result<String> result = ExceptionChecked.checkRequestParam(orderProcessInfo);
			if (result != null) {
				logger.error("数据不满足json格式!");
				return ResultBuilder.invalidResult();
			}
			JSONObject orderInfo = JSONObject.parseObject(orderProcessInfo);
			RpcResponse<String> resultInfo = orderProcessCudService.updateOrderProcess(orderInfo);
			if (resultInfo.isSuccess() && resultInfo.getSuccessValue() != null) {
				logger.info("修改成功!");
				return ResultBuilder.successResult(resultInfo.getSuccessValue(), resultInfo.getMessage());
			} else {
				logger.error("修改失败!");
				return ResultBuilder.failResult(resultInfo.getMessage());
			}
		} catch (Exception e) {
			logger.error("updateOrderProcess()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description 保存xml文件
	 * @param XmlFile
	 *            xml文件对象, accessSecret 密钥
	 * @return fileId : 文件id(文件存储于工作流)
	 */

	public Result<String> saveXmlTmplate(MultipartFile xmlFile, String accessSecret) {
		try {
			if (xmlFile == null) {
				logger.error("文件对象不存在");
				return ResultBuilder.noBusinessResult();
			}
			logger.info(String.format("[saveXmlTmplate()->OriginalFilename:%s]", xmlFile.getOriginalFilename()));
			// 获取文件名及后缀名
			String originalFilename = xmlFile.getOriginalFilename();
			String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			// 只能上传xml格式的文件
			if (!suffix.equalsIgnoreCase(XML)) {
				logger.error("上传失败,请上传正确的xml格式模板!");
				return ResultBuilder.failResult("上传失败,请上传正确的xml格式模板!");
			}
			// 只能上传不超过50KB的文件
			if (xmlFile.getSize() > NUMBER) {
				logger.error("上传失败,上传文件不能大于50KB!");
				return ResultBuilder.failResult("上传失败,上传文件不能大于50KB!");
			}
			// 获取文件字节数组
			byte[] bytes = xmlFile.getBytes();
			// 调用工作流接口解析xml文件
			RpcResponse<String> result = processFileCurdService.saveProcessFile(bytes, accessSecret, originalFilename);
			if (result.isSuccess() && result.getSuccessValue() != null) {
				logger.info("上传成功!");
				return ResultBuilder.successResult(result.getSuccessValue(), result.getMessage());
			} else {
				logger.error("上传失败!");
				return ResultBuilder.failResult(result.getMessage());
			}
		} catch (Exception e) {
			logger.error("saveXmlTmplate()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
}
