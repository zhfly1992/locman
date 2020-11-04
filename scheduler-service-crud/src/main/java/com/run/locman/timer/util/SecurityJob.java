/*
* File name: SecurityJob.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			钟滨远		2020年4月28日
* ...			...			...
*
***************************************************/

package com.run.locman.timer.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.run.activity.api.crud.service.ProcessFileCurdService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.locman.api.crud.service.FocusSecurityCrudService;
import com.run.locman.api.dto.ProcessInfoListDto;
import com.run.locman.api.entity.ProcessInfo;
import com.run.locman.api.timer.crud.repository.ScheduledTasksCudRepository;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.LogMessageContants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.OrderProcessContants;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月28日
*/

public class SecurityJob implements Job{
	
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FocusSecurityCrudService			 focusSecurityCrudService;
	
	@Autowired
	private ScheduledTasksCudRepository 		 scheduledTasksCudRepository;
	
	@Autowired
	private ProcessFileCurdService				 processFileCurdService;
	
	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@SuppressWarnings({ "rawtypes", "serial" })
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDetail jobDetail = context.getJobDetail();
//			object传递之后变为了String所以该方法不能使用
//			Object object = jobDetail.getJobDataMap().get("facInfo");
//			String jsonString = JSONObject.toJSONString(object);
//			JSONArray parseArray = JSONObject.parseArray(jsonString);
//			List<Map> facList = parseArray.toJavaList(Map.class);
			
			//使用GSON解析字符串
			String  object = jobDetail.getJobDataMap().get("facInfo")+"";
			
			Gson gson = new Gson();
			List<Map<String,String>> facList =
			        gson.fromJson(object, new TypeToken<List<Map<String, String>>>() {}.getType());
//			System.out.println(facList.get(0));
			String focusSecurityId=jobDetail.getJobDataMap().get("focusSecurityId")+"";
			
			List<String> successList=new ArrayList<String>();
			List<String> failList=new ArrayList<String>();
			List<String> facilityIds = Lists.newArrayList();
			
			JSONObject  command=new JSONObject();
			command.put("defen_state", 1);
			String facilityId4Access = "";
			for(Map thisMap:facList) {
				String facilityId=thisMap.get("facilityId")+"";
				String deviceId=thisMap.get("deviceId")+"";
				logger.info(String.format("当前保障下发命令的设备：%s", deviceId));
				RpcResponse<String> operateLock = focusSecurityCrudService.operateLock(facilityId, command,focusSecurityId);
				if(operateLock.isSuccess()) {
					successList.add(facilityId);
				}else {
					failList.add(facilityId);
				}
				
				facilityId4Access = facilityId;
				facilityIds.add(facilityId);
				
			}
			List<String> securityId=new ArrayList<String>();
			securityId.add(focusSecurityId);
			Integer updateScheduledTasks = scheduledTasksCudRepository.updateScheduledTasks(3,securityId);
			if(updateScheduledTasks >0 ) {
				logger.info("重保任务完成，任务状态");
			}
			
			updateProcessState(facilityId4Access,facilityIds);
			logger.info("下发命令成功的设施ID："+successList);
			logger.info("下发命令失败的设施ID："+failList);
		}catch(Exception e) {
			logger.error(e);
			//System.out.println("====================" + e);
		}
		
	}
	
	
	public void updateProcessState(String facilityId4Access, List<String> facilityIds){
		String accessSecret = scheduledTasksCudRepository.facilityId4Access(facilityId4Access);
		logger.info("切换工作流程:accessSecret" + accessSecret);
		//System.out.println("切换工作流程===============:accessSecret" + accessSecret);
		Map<String, Object> queryInfo = Maps.newHashMap();
		queryInfo.put(OrderProcessContants.USC_ACCESS_SECRET, accessSecret);
		List<ProcessInfoListDto> processInfoList = scheduledTasksCudRepository.queryOrderProcessList(queryInfo);
		logger.info("切换工作流程:processInfoList" + processInfoList);
		//System.out.println("切换工作流程:processInfoList" + processInfoList);
		//先停用所有配置
		for (ProcessInfoListDto processInfoListDto : processInfoList) {
			if (OrderProcessContants.STATE_ENABLED.equals(processInfoListDto.getManageState()) && !"1".equals(processInfoListDto.getMark()) ) {
				// 先更改工作流xml文件的管理状态
				String activityManagerState = OrderProcessContants.INVALID;
				RpcResponse<Boolean> manageProcessFile = processFileCurdService.manageProcessFile(processInfoListDto.getFileId(),
						activityManagerState);
				if (!manageProcessFile.isSuccess() || manageProcessFile.getSuccessValue() == null) {
					logger.error(LogMessageContants.SAVE_FAIL);
				}
				if (!manageProcessFile.getSuccessValue()) {
					logger.info(LogMessageContants.UPDATE_FAIL + "工作流管理状态更新失败");
				}
				// 工作流管理状态更新成功再修改工单流程管理状态
				// 流程信息封装
				ProcessInfo processInfo = new ProcessInfo();
				processInfo.setId(processInfoListDto.getId());
				processInfo.setManageState(OrderProcessContants.STATE_DISABLED);
				processInfo.setUpdateBy("system");
				processInfo.setUpdateTime(DateUtils.formatDate(new Date()));
				processInfo.setFileId(processInfoListDto.getFileId());
				// 数据库更新
				logger.info(String.format("[updateState()->updatePart方法,参数:%s]", processInfo));
				int updatePart = scheduledTasksCudRepository.updatePart(processInfo);
				if (updatePart > 0) {
					processInfoListDto.setManageState( OrderProcessContants.STATE_DISABLED);
					logger.info("[updateState()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
				} else {
					logger.error(MessageConstant.UPDATE_FAIL);
				}
				
			}
			
			
		}
		
		if (null != facilityIds && !facilityIds .isEmpty()) {
			
			int num = scheduledTasksCudRepository.updateSimpleOrderProcessVoid(accessSecret, facilityIds);
			logger.error("作废" + num +"个未执行的作业工单流程");
			
			int updateAlarmOrderNum = scheduledTasksCudRepository.updateAlarmOrderVoid(accessSecret,facilityIds);
			logger.error("作废" + updateAlarmOrderNum +"个未执行的告警工单流程");

			int updateAlarmInfoNum = scheduledTasksCudRepository.updateAlarmInfoVoid(accessSecret,facilityIds);
			logger.error("作废" + updateAlarmInfoNum +"个未执行的告警信息");
			
		}

		
		//启用重保期间使用的配置
		for (ProcessInfoListDto processInfoListDto : processInfoList) {
			if (OrderProcessContants.STATE_DISABLED.equals(processInfoListDto.getManageState()) && "1".equals(processInfoListDto.getMark()) ) {
				// 先更改工作流xml文件的管理状态
				String activityManagerState = OrderProcessContants.VALID;
				RpcResponse<Boolean> manageProcessFile = processFileCurdService.manageProcessFile(processInfoListDto.getFileId(),
						activityManagerState);
				if (!manageProcessFile.isSuccess() || manageProcessFile.getSuccessValue() == null) {
					logger.error(LogMessageContants.SAVE_FAIL);
				}
				if (!manageProcessFile.getSuccessValue()) {
					logger.info(LogMessageContants.UPDATE_FAIL + "工作流管理状态更新失败");
				}
				// 工作流管理状态更新成功再修改工单流程管理状态
				// 流程信息封装
				ProcessInfo processInfo = new ProcessInfo();
				processInfo.setId(processInfoListDto.getId());
				processInfo.setManageState(OrderProcessContants.STATE_ENABLED);
				processInfo.setUpdateBy("system");
				processInfo.setUpdateTime(DateUtils.formatDate(new Date()));
				processInfo.setFileId(processInfoListDto.getFileId());
				// 数据库更新
				logger.info(String.format("[updateState()->updatePart方法,参数:%s]", processInfo));
				int updatePart = scheduledTasksCudRepository.updatePart(processInfo);
				if (updatePart > 0) {
					logger.info("[updateState()->success: " + MessageConstant.UPDATE_SUCCESS + "]");
				} else {
					logger.error(MessageConstant.UPDATE_FAIL);
				}
				
			}
			
		}
		
	}
	

}
