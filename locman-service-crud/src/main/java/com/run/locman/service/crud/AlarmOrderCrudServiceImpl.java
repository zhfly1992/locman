/*
 * File name: AlarmOrderCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月4日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.AlarmOrderCrudRepository;
import com.run.locman.api.crud.service.AlarmOrderCrudService;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.timer.crud.service.AlramOrderRemindTimerService;
import com.run.locman.constants.AlarmOrderConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderProcessContants;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月4日
 */
@Transactional(rollbackFor = Exception.class)
public class AlarmOrderCrudServiceImpl implements AlarmOrderCrudService {

	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private AlarmOrderCrudRepository	alarmOrderCrudRepository;
	@Autowired
	private AlramOrderRemindTimerService  alramOrderRemindTimerService;

	@Override
	public RpcResponse<String> saveAlarmOrder(AlarmOrder alarmOrder) {
		if (alarmOrder == null) {
			logger.error("[AlarmOrderCrudServiceImpl --> saveAlarmOrder()-->invalid：告警工单对象不能为空！]");
			return RpcResponseBuilder.buildErrorRpcResp("告警工单对象不能为空");
		}
		try {
			logger.info("saveAlarmOrder()-->即将保存告警工单信息:" + alarmOrder);
			int result = alarmOrderCrudRepository.insertModel(alarmOrder);
			if (result > 0) {
				logger.info("[AlarmOrderCrudServiceImpl --> saveAlarmOrder(): success: 告警工单信息保存成功！工单id "
						+ alarmOrder.getId() + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存成功", alarmOrder.getId());
			}
			logger.error("[AlarmOrderCrudServiceImpl --> saveAlarmOrder(): fail：保存失败！]");
			return RpcResponseBuilder.buildErrorRpcResp("保存失败");
		} catch (Exception e) {
			logger.error("AlarmOrderCrudServiceImpl()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> updateAlarmOrder(AlarmOrder alarmOrder) {
		try {
			if (alarmOrder == null) {
				logger.error("[updateAlarmOrder --> updateAlarmOrder(): invalid：告警工单修改参数不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("告警工单修改参数不能为空");
			}
			if (StringUtils.isBlank(alarmOrder.getId())) {
				logger.error("[updateAlarmOrder --> updateAlarmOrder(): invalid：告警工单id不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("告警工单id不能为空！");
			}

			logger.info("updateAlarmOrder() -->即将更新告警工单信息:" + alarmOrder);
			//接收工单开启定时器
			RpcResponse<String> alarmOrderNotPresentPic = alramOrderRemindTimerService.AlarmOrderNotPresentPic(alarmOrder);
			if(!alarmOrderNotPresentPic.isSuccess()) {
				logger.error(String.format("[startTimer()->error:%s]", alarmOrderNotPresentPic.getMessage()));
			}
			logger.info(String.format("[startTimer()->info:%s]", alarmOrderNotPresentPic.getMessage()));
			String orderId=alarmOrder.getId();
			String presentPic=alarmOrderCrudRepository.IsTherePresentPic(orderId);
			int updatePart = alarmOrderCrudRepository.updatePart(alarmOrder);
			if (updatePart > 0) {
				if(!"".equals(presentPic)) {
					RpcResponse<Boolean> closeScheduleJob = alramOrderRemindTimerService.closeScheduleJob(orderId+"NotPresent");
					logger.info("updateAlarmOrder()-->关闭定时器:" + closeScheduleJob.getSuccessValue());
				}
				logger.info("[updateAlarmOrder --> updateAlarmOrder(): invalid：工单更新成功！参数:" + alarmOrder + "]");
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.UPDATE_SUCCESS, true);
			} else {
				logger.error("[updateAlarmOrder --> updateAlarmOrder(): invalid：工单更新失败！]");
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.UPDATE_FAIL);
			}
		} catch (Exception e) {
			logger.error("updateAlarmOrder()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmOrderCrudService#updateOrderAlarmId(java.util.Map)
	 */
	@Override
	public RpcResponse<String> updateOrderAlarmId(Map<String, Object> map) {
		if (map == null) {
			return RpcResponseBuilder.buildErrorRpcResp("更新参数不能为空");
		}
		if (/* !map.containsKey("alarmDesc") || !map.containsKey("rule") || */ !map.containsKey(AlarmOrderConstants.DEVICE_ID)
				|| !map.containsKey(AlarmOrderConstants.ALARM_ID) /*|| !map.containsKey(OrderProcessContants.CREATE_TIME)*/) {
			return RpcResponseBuilder.buildErrorRpcResp("参数非法");
		}
		try {
			logger.info("updateOrderAlarmId()-->即将更新告警工单告警id!参数:" + map);
			map.put("alarmOrderId", "");
			int result = alarmOrderCrudRepository.updateOrderAlarmId(map);
			if (result == 0) {
				logger.error("updateOrderAlarmId()-->更新失败");
				return RpcResponseBuilder.buildSuccessRpcResp("更新失败", null);
			} else {
				logger.info("updateOrderAlarmId()-->更新成功");
				logger.info(map);
				// 如果更新成功,mybatis会自动把告警工单id封装到传入参数mapzhong,所以可以直接获取对应字段
				return RpcResponseBuilder.buildSuccessRpcResp("更新成功", map.get("alarmOrderId") + "");
				// return RpcResponseBuilder.buildSuccessRpcResp("更新成功",
				// Boolean.TRUE);
			}
		} catch (Exception e) {
			logger.error("updateOrderAlarmId()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmOrderCrudService#saveAlarm_Order(java.util.Map)
	 */
	@Override
	public RpcResponse<Map<String, Object>> saveAlarmOrderandInfo(JSONObject jsonParam) {
		try {
			if (jsonParam == null) {
				logger.error("[saveAlarm_Order: invalid：参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			if (StringUtils.isBlank(jsonParam.getString(AlarmOrderConstants.ALARM_ID))) {
				logger.error("[saveAlarm_Order: invalid：告警alarmId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("告警alarmId不能为空！");
			}
			logger.info(String.format("[saveAlarmOrderandInfo()->进入方法,参数:%s]", jsonParam));

			List<Map<String, Object>> ids = alarmOrderCrudRepository
					.getAlarmIdAndOrderIdByAlarmId(jsonParam.getString("alarmId"));
			if (null == ids || ids.size() == 0) {
				logger.error("[getAlarmIdAndOrderIdByAlarmId(): invalid：查询工单跟告警信息关系失败！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询工单跟告警信息关系失败！");
			}

			int saveAlarmOrder = alarmOrderCrudRepository.saveAlarmOrderAndInfo(ids);
			if (saveAlarmOrder > 0) {
				logger.info("[saveAlarm_Order(): invalid：保存告警信息和告警工单关系成功！参数:" + ids + "]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存告警信息和告警工单关系成功！", jsonParam);
			}
			logger.error("[saveAlarm_Order(): invalid：保存告警信息和告警工单关系失败！参数:" + ids + "]");
			return RpcResponseBuilder.buildErrorRpcResp("保存告警信息和告警工单关系失败！");

		} catch (Exception e) {
			logger.error("saveAlarm_Order()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmOrderCrudService#addPresentPicAlarmOrder(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> addPresentPicAlarmOrder(String id ,String Url) {
		try {
			Map<String ,Object> map=new HashMap<String ,Object>();
			map.put("id", id);
			map.put("picUrl", Url);
			int b=alarmOrderCrudRepository.addPresentPic(map);
			if(b > 0) {
				logger.info("[addPresentPicAlarmOrder()-->上传到场图片成功！");
				return RpcResponseBuilder.buildSuccessRpcResp("上传到场图片成功！",String.valueOf(b));
			}
			else {
				logger.error("[addPresentPicAlarmOrder()->error:上传到场图片失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("上传到场图片失败");
			}
			} catch (Exception e) {
				return RpcResponseBuilder.buildExceptionRpcResp(e);
			}
	}



	/**
	 * @see com.run.locman.api.crud.service.AlarmOrderCrudService#addEndPicAlarmOrder(java.lang.String, java.util.List)
	 */
	@Override
	public RpcResponse<String> addEndPicAlarmOrder(String id, List<Object> picUrl) {

		try {
			if (id == null) {
				logger.error("[addEndPicAlarmOrder: invalid：参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			if (picUrl == null) {
				logger.error("[addEndPicAlarmOrder: invalid：完成处理图片参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("完成处理图片参数不能为空!");
			}
			Map<String ,Object> map=new HashMap<String ,Object>();
			map.put("id", id);
			
			//String Url=JSON.toJSONString(picUrl);
			String Url=StringUtils.join("", picUrl);
			map.put("Url",Url);
			String presentPic=alarmOrderCrudRepository.IsTherePresentPic(id);
			if(!presentPic.equals("")) {
				int a=alarmOrderCrudRepository.addEndPic(map);
				if(a > 0) {
					logger.info("[addEndPicAlarmOrder()-->上传完成处理图片成功！");
					return RpcResponseBuilder.buildSuccessRpcResp("上传完成处理图片成功！",String.valueOf(a));
				}
				else {
					logger.error("[addEndPicAlarmOrder()->error:上传完成处理图片失败!]");
					return RpcResponseBuilder.buildErrorRpcResp("上传完成处理图片失败");
				}
			}else {
				logger.error("[addEndPicAlarmOrder()->error:上传完成处理图片失败,请先上传到场图片!]");
				return RpcResponseBuilder.buildErrorRpcResp("上传完成处理图片失败");
			}
			
		}catch(Exception e) {
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
