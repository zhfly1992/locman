/*
 * File name: DeviceReportedCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年10月19日 ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.run.authz.api.base.util.ParamChecker;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.AlarmInfoCrudRepository;
import com.run.locman.api.crud.repository.DataCacheCrudRepository;
import com.run.locman.api.crud.repository.DeviceReportedCrudRepository;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.service.DeviceAlarmProduceService;
import com.run.locman.api.crud.service.DeviceJobCrudService;
import com.run.locman.api.crud.service.DeviceReportedCrudService;
import com.run.locman.api.crud.service.FactoryAppTagCrudService;
import com.run.locman.api.crud.service.FaultOrderProcessCudService;
import com.run.locman.api.crud.service.RemoteControlRecordCrudService;
import com.run.locman.api.crud.service.SchedulerCrudService;
import com.run.locman.api.crud.service.SwitchLockRecordCrudService;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.api.drools.service.AlarmRuleInvokInterface;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.Inspection;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.query.service.AlarmRuleQueryService;
import com.run.locman.api.query.service.DeviceJobQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.RemoteControlRecordQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.AlarmInfoConstants;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceContants;
import com.run.sms.api.JiguangService;

import entity.JiguangEntity;

/**
 * @Description:接受设备上报数据
 * @author: 王胜
 * @version: 1.0, 2018年10月19日
 */

@EnableScheduling
public class DeviceReportedCrudServiceImpl implements DeviceReportedCrudService {

	private static final Logger			logger			= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FactoryAppTagCrudService	factoryAppTagCrudService;

	@Autowired
	private DeviceJobQueryService		deviceJobQueryService;

	@Autowired
	private SchedulerCrudService		schedulerCrudService;

	@Autowired
	private DeviceJobCrudService		deviceJobCrudService;

	@Autowired
	private DeviceQueryService			deviceQueryService;

	@Autowired
	private SwitchLockRecordCrudService	switchLockRecordCrudService;

	@Autowired
	RemoteControlRecordQueryService		recordQueryService;

	@Autowired
	RemoteControlRecordCrudService		recordCrudService;

	@Autowired
	DeviceAlarmProduceService			deviceAlarmProduceService;

	@Autowired
	DeviceReportedCrudRepository		deviceReportedCrudRepository;

	@Autowired
	DataCacheCrudRepository				dataCacheCrudRepository;

	@Autowired
	AlarmInfoCrudRepository				alarmInfoCrudRepository;
	
	@Autowired
	private UserRoleBaseQueryService	userRoleQueryService;
	
	@Autowired
	private AlarmRuleInvokInterface		alarmRuleInvokInterface;
	
	@Autowired
	private AlarmRuleQueryService		alarmRuleQueryService;
	
	@Autowired
	private FaultOrderProcessCudService		faultOrderProcessCudService;
	
	@Autowired
	private FacilitiesCrudRepository	facilitiesCrudRepository;
	
	@Autowired
	private UpdateRedisCrudService		updateRedisCrudService;
	
	@Autowired
	private JiguangService				jiguangService;
	//专用变量，不能他用
	private static Object               proxy1;
	/**
	 * 专用变量,不能他用
	 */
	private Object						rollBackPoint;
	/** 时间戳长度 */
	public static int					LENGTH			= 10;

	private static ExecutorService		executorService	= Executors.newFixedThreadPool(5);

	private static ExecutorService		retry			= Executors.newFixedThreadPool(2);

	//用于存储点英文标识和中文名字对照关系
	private static Map<String, String> paraNameTransTable;
	
	

	/**
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#deviceReportedMessageReceive(java.lang.String)
	 */
	@Override
	public RpcResponse<String> messageReceive(String deviceReportedMessage) {
		logger.info(String.format("[messageReceive()->开始执行,设备上报数据:%s]", deviceReportedMessage));
		try {
			if (StringUtils.isBlank(deviceReportedMessage)) {
				logger.error("[messageReceive: invalid：参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			if (!ParamChecker.isNotMatchJson(deviceReportedMessage)) {
				JSONObject parseObject = JSONObject.parseObject(deviceReportedMessage);
				String timestamp = parseObject.getString("timestamp");
				String deviceId = UtilTool.getIotNewDeviceId(parseObject);
				String gatewayId = UtilTool.getIotNewGatewayId(parseObject);
				JSONObject reported = UtilTool.getIotNewRepoted(parseObject);
				// TODO 根据网关id和子设备id查询设备id集合,然后循环
				RpcResponse<List<String>> findDeviceId = deviceQueryService.findDeviceId(gatewayId, deviceId);
				if (!findDeviceId.isSuccess()) {
					logger.error(String.format("[deviceOperation()->查询设备id失败:%s]", findDeviceId.getMessage()));
					return RpcResponseBuilder.buildErrorRpcResp("查询设备id失败!");
				}
				List<String> locmanDeviceIds = findDeviceId.getSuccessValue();

				if (null == locmanDeviceIds) {
					logger.error("[deviceOperation()->查询设备id失败:设备id集合为null]");
					return RpcResponseBuilder.buildErrorRpcResp("查询设备id失败!");
				}
				logger.info(String.format("[deviceOperation()->查询获得设备id:%s]", locmanDeviceIds));
				for (String locmanDeviceId : locmanDeviceIds) {

					// 维护状态的设备不进行数据处理
					RpcResponse<Device> res = deviceQueryService.queryDeviceInfoById(locmanDeviceId);
					if (!res.isSuccess()) {
						logger.error(String.format("[deviceOperation()->error:%s]", res.getMessage()));
						return RpcResponseBuilder
								.buildErrorRpcResp(String.format("[deviceOperation()->error:%s]", res.getMessage()));
					}
					String deviceDefendState = res.getSuccessValue().getDeviceDefendState();
					if ("fault".equals(deviceDefendState)) {
						logger.info(String.format("[deviceOperation()->suc:设备 = %s,维护状态，数据不进行处理！]", locmanDeviceId));
						return RpcResponseBuilder.buildErrorRpcResp(
								String.format("[deviceOperation()->suc:设备 = %s,维护状态，数据不进行处理！]", locmanDeviceId));
					}
					logger.info(String.format("[deviceOperation()->success:%s]", "准备添加开关锁数据!"));
					switchLockRecordCrudService.insertSwitchLockInfo(locmanDeviceId, reported);
					/**成华区使用,设施开关记录表*/
					logger.info(String.format("[deviceOperation()->success:%s]", "准备添加设施开关数据记录!"));
					switchLockRecordCrudService.insertManholeSwitch(deviceId, reported, timestamp);
					
					logger.info(String.format("[deviceOperation()->success:%s]", "准备添加设备状态数据!"));
					// 查询此时设备开启关闭实时状态，以此判断删除定时器。
					RpcResponse<JSONObject> stateJson = deviceQueryService.queryDeviceLastState(locmanDeviceId);
					// 保存订阅到的设备信息
					factoryAppTagCrudService.addDeviceState(parseObject, gatewayId, locmanDeviceId);
					RpcResponse<String> checkExistFaultOrder = checkExistFaultOrder(locmanDeviceId, res);
					if (null != checkExistFaultOrder) {
						continue;
					}

					// 上报数据同规则比较，产生告警
					logger.info(DateUtils.formatDate(new Date()) + "***上报数据同规则比较，产生告警进入alarmProduce:"
							+ System.currentTimeMillis());
					deviceAlarmProduceService.alarmProduce(reported, locmanDeviceId, timestamp);

					// 此设备上报的状态是关闭，且存在定时器，则删除定时器。
					if (stateJson.isSuccess() && null != stateJson.getSuccessValue()) {
						this.deleteTimer(UtilTool.getIotNewRepoted(stateJson.getSuccessValue()), reported,
								locmanDeviceId);
					}
					// 修改下发命令记录状态
					this.updateControlRecord(reported, locmanDeviceId, timestamp);

				}
			} else {
				logger.error("[messageReceive: invalid：设备上报数据格式错误，保存设备上报数据失败!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备上报数据格式错误，保存设备上报数据失败!");
			}
			logger.info("[messageReceive: invalid：保存设备上报数据成功!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存设备上报数据成功!");
		} catch (Exception e) {
			logger.error("messageReceive()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private RpcResponse<String> checkExistFaultOrder(String locmanDeviceId, RpcResponse<Device> res) {
		// 通过设备id查询是否存在故障工单，如果存在未结束的故障工单不产生告警
		RpcResponse<String> rpcFaultOrder = deviceQueryService.findFaultOrderByDeviceId(locmanDeviceId);
		if (!rpcFaultOrder.isSuccess()) {
			logger.error(String.format("[deviceOperation()->error:%s]", res.getMessage()));
			return RpcResponseBuilder
					.buildErrorRpcResp(String.format("[deviceOperation()->error:%s]", res.getMessage()));
		}

		if (!StringUtils.isBlank(rpcFaultOrder.getSuccessValue())) {
			logger.error("[deviceOperation()->info:设备存在故障工单！不进行告警！]");
			return RpcResponseBuilder.buildErrorRpcResp("[deviceOperation()->info:设备存在故障工单！不进行告警！]");
		}
		return null;
	}



	/**
	 * 
	 * @Description:移除定时器
	 * @param thisState
	 * @param reportedState
	 * @param deviceId
	 */
	private void deleteTimer(JSONObject thisReported, JSONObject reported, String deviceId) {
		try {
			if (null == thisReported || null == reported || StringUtils.isBlank(deviceId)) {
				logger.error("{deleteTimer()}参数为空!");
				return;
			}

			Set<String> set = new HashSet<>();
			List<String> jobIdList = new ArrayList<>();
			int count = 0;
			Boolean tal = true;
			// 查看该设备之前是否已经存在超时未关定时器，若存在、且此时上报为关闭状态则移除超时未关定时器任务。
			RpcResponse<List<Map<String, Object>>> ids = deviceJobQueryService.getJobIdsByDeviceId(deviceId);
			if (ids.isSuccess() && null != ids.getSuccessValue() && ids.getSuccessValue().size() > 0) {
				List<Map<String, Object>> list = ids.getSuccessValue();
				for (Map<String, Object> map : list) {
					String item = map.get("item") + "";

					// 若key为ble，则为光交设备手机蓝牙下发命令，特殊处理
					if ("ble".equals(item)) {
						if (tal) {
							String[] itemKeyArray = new String[] { "ls1", "ls2", "ds1", "ds2" };
							for (String key : itemKeyArray) {
								// 此时设备上报的开启关闭状态
								String reportedCloseState = reported.getString(key);
								if (null != reportedCloseState && !reportedCloseState.equals("close")) {
									count += 1;
								}
							}
							// 锁全部为关闭
							if (count == 0) {
								set.add(item);
								// 需要删除定时器的id集合
								jobIdList.add(map.get("jobId") + "");
							}
							tal = false;
						} else {
							jobIdList.add(map.get("jobId") + "");
						}
						continue;
					}
					// 此时数据库设备开启关闭状态
					String thisCloseState = thisReported.getString(item);
					// 此时设备上报的开启关闭状态
					String reportedCloseState = reported.getString(item);
					if (null != thisCloseState && null != reportedCloseState && reportedCloseState.equals("close")
							&& !thisCloseState.equals(reportedCloseState)) {
						// 一个设备多个锁，上报状态为关的锁的key，且去重
						set.add(item);
						// 需要删除定时器的id集合
						jobIdList.add(map.get("jobId") + "");
					}
				}

				if (jobIdList.size() > 0) {
					for (String jobId : jobIdList) {
						RpcResponse<Boolean> del = schedulerCrudService.schedulerDelete(jobId);
						logger.info(String.format("[schedulerDelete()->success:%s]", del.getMessage()));
					}
				}
				if (set.size() > 0) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("deviceId", deviceId);
					map.put("keySet", set);
					// 删除设备与定时器关系
					RpcResponse<Integer> delete = deviceJobCrudService.deleteByDeviceId(map);
					if (!delete.isSuccess()) {
						logger.error(String.format("[deleteByDeviceId()->fail:%s]", delete.getMessage()));
					}
				}
			}
		} catch (Exception e) {
			logger.error("deleteTimer()->exception", e);
		}
	}



	/**
	 * 
	 * @Description:销毁下发命令记录
	 * @param reportedState
	 */
	public void updateControlRecord(JSONObject reported, String deviceId, String timestamp) {
		logger.info(String.format("[updateControlRecord()->:%s]", "准备销毁下发命令!"));
		try {
			if (null == reported) {
				logger.error("[updateControlRecord()->valid:参数为空!]");
				return;
			}
			if (StringUtils.isBlank(deviceId) || StringUtils.isBlank(timestamp)) {
				logger.error("[alarmProduce()->invalid：设备id为空或上报时间为空!]");
				return;
			}

			Set<String> idDelList = new HashSet<>();
			String time = "";
			if (timestamp.length() > LENGTH) {
				time = timestamp;
			} else {
				time = timestamp + "000";
			}
			// Boolean tal = false;
			RpcResponse<List<RemoteControlRecord>> control = recordQueryService.getControlByDeviceId(deviceId);
			// 该设备是否存在有效命令记录
			if (control.isSuccess() && null != control.getSuccessValue() && control.getSuccessValue().size() > 0) {
				List<RemoteControlRecord> controlList = control.getSuccessValue();
				logger.info(String.format("[updateControlRecord()->命令集合:%s]", controlList));
				// 下发命令key为ble则为光交设备蓝牙下发，特殊处理
				dealData(controlList);
				// 获取需销毁id命令集合
				getDeleteId(reported, idDelList, time, controlList);
			}

			if (idDelList.size() > 0) {

				// if (tal) {// 光交蓝牙下发命令key
				// keyList.add("ble");
				// }

				Map<String, Object> map = Maps.newHashMap();
				map.put("deviceId", deviceId);
				map.put("controlDestroyTime", UtilTool.timeStampToDate(Long.parseLong(timestamp)));
				map.put("idDelList", idDelList);
				// map.put("keyList", keyList);

				logger.info(String.format("[updateControlRecord()->需移销毁id命令集合:%s]", idDelList));

				RpcResponse<String> updateControlState = recordCrudService.updateControlState(map);
				if (null == updateControlState || !updateControlState.isSuccess()) {
					logger.error("[updateControlRecord()->fail:修改命令记录为无效状态失败!]");
				}
			}
		} catch (Exception e) {
			logger.error("updateControlRecord()->exception", e);
		}
	}



	/**
	 * 
	 * @Description: 获取需销毁id命令集合
	 * @param
	 * @return
	 */

	private void getDeleteId(JSONObject reported, Set<String> idDelList, String time,
			List<RemoteControlRecord> controlList) {
		for (RemoteControlRecord controlRecord : controlList) {
			long contorlTimastamp = DateUtils.toDate(controlRecord.getControlTime()).getTime();
			long nowTimastamp = DateUtils.toDate(DateUtils.stampToDate(time)).getTime();
			// 此上报状态在下发命令三分钟内
			if ((nowTimastamp - contorlTimastamp) <= CommonConstants.OVER_TIME) {
				String itemValue = reported.getString(controlRecord.getControlItem());
				String controlValue = controlRecord.getControlValue();
				if ("on".equals(controlValue)) {
					controlValue = "open";
				}
				// on是为了手机蓝牙下发命令统一
				if (null != itemValue && itemValue.equals(controlValue)
				// 上报锁状态与下发命令value值一致，且不为关闭
						&& !itemValue.equals("close")) {
					// keyList.add(controlRecord.getControlItem());//
					// 命令记录设置成无效的key集合
					idDelList.add(controlRecord.getId());
				}
				logger.info(String.format("[updateControlRecord()->需销毁id命令集合:%s]", idDelList));
			}
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void dealData(List<RemoteControlRecord> controlList) {
		for (int i = 0; i < controlList.size(); i++) {
			// 下发命令key为ble则为光交设备蓝牙下发，特殊处理
			if ("ble".equals(controlList.get(i).getControlItem())) {
				String[] itemKeyArray = new String[] { "ls1", "ls2", "ds1", "ds2" };
				for (String key : itemKeyArray) {
					RemoteControlRecord record = new RemoteControlRecord();
					record.setControlItem(key);
					record.setId(controlList.get(i).getId());
					record.setControlTime(controlList.get(i).getControlTime());
					record.setControlValue(controlList.get(i).getControlValue());
					controlList.add(record);
				}
				controlList.remove(i);
				logger.info(String.format("[updateControlRecord()->设备为光交-命令集合:%s]", controlList));
				break;
			}
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#saveDevice_RealReporte(java.util.Map)
	 */
	@Override
	public RpcResponse<String> saveDeviceRealReporte(Map<String, Object> paramMap) {
		try {
			if (null == paramMap || paramMap.isEmpty()) {
				logger.error("[saveDevice_RealReporte: invalid：参数为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数为空!");
			}
			if (!paramMap.containsKey(DeviceContants.DEVICEID) || null == paramMap.get(DeviceContants.DEVICEID)) {
				logger.error("[saveDevice_RealReporte: invalid：设备id不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空!");
			}
			paramMap.put("id", UtilTool.getUuId());
			int save = deviceReportedCrudRepository.saveDeviceRealReporte(paramMap);
			if (save > 0) {
				logger.info("[saveDevice_RealReporte:设备id不能为空!]");
				return RpcResponseBuilder.buildSuccessRpcResp("保存成功!", null);
			}
			logger.error("[saveDevice_RealReporte: invalid：保存失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("保存失败!");
		} catch (Exception e) {
			logger.error("saveDevice_RealReporte()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#updateDevice_RealReporte(java.util.Map)
	 */
	@Override
	public RpcResponse<Integer> updateDeviceRealReporte(Map<String, Object> paramMap) {
		try {
			if (null == paramMap || paramMap.isEmpty()) {
				logger.error("[updateDeviceRealReporte: invalid：参数为空!]");
				return RpcResponseBuilder.buildRpcResponse(false, "参数为空!", null, 10);
			}
			if (!paramMap.containsKey(DeviceContants.DEVICEID) || null == paramMap.get(DeviceContants.DEVICEID)) {
				logger.error("[updateDeviceRealReporte: invalid：设备id不能为空!]");
				return RpcResponseBuilder.buildRpcResponse(false, "设备id不能为空!", null, 10);
			}
			int update = deviceReportedCrudRepository.updateDeviceRealReporte(paramMap);
			if (update >= 0) {
				logger.info("[updateDeviceRealReporte:修改成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("修改成功!", update);
			}
			logger.error("[updateDeviceRealReporte: invalid：修改失败!]");
			return RpcResponseBuilder.buildRpcResponse(false, "修改失败!", null, update);
		} catch (Exception e) {
			logger.error("updateDeviceRealReporte()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 现在
	 * 
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#messageReceiveThreadPool(java.lang.String)
	 */

	@Override
	public RpcResponse<String> messageReceiveThreadPool(String deviceReportedMessage) {
		if (StringUtils.isBlank(deviceReportedMessage)) {
			logger.error("[messageReceiveThreadPool: invalid：参数不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
		}
		if (ParamChecker.isNotMatchJson(deviceReportedMessage)) {
			logger.error("[messageReceiveThreadPool: invalid：设备上报数据格式错误，保存设备上报数据失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("设备上报数据格式错误，保存设备上报数据失败!");
		}
		
		String id = UtilTool.getUuId();
		int saveCacheData = dataCacheCrudRepository.saveCacheData(id, deviceReportedMessage);
		if (saveCacheData < 1) {
			return RpcResponseBuilder.buildErrorRpcResp("数据添加失败");
		}
		proxy1 = AopContext.currentProxy();
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				messageDeal(id, deviceReportedMessage);

			}
		});
		return RpcResponseBuilder.buildSuccessRpcResp("数据已开始执行", "数据已开始执行");
	}



	@Override
	public void startRetryCacheData() {
		logger.info(DateUtils.formatDate(new Date()) + "开始检测未处理完成的上报信息");
		List<Map<String, Object>> findRetryCacheData = dataCacheCrudRepository.findRetryCacheData();
		for (Map<String, Object> map : findRetryCacheData) {
			Object id = map.get("id");
			if (null == id || StringUtils.isBlank(id + "")) {
				continue;
			}
			Object dataCache = map.get("dataCache");
			if (null == dataCache || StringUtils.isBlank("" + dataCache)) {
				dataCacheCrudRepository.deleteCacheDataById(id + "");
				continue;
			}

			proxy1 = AopContext.currentProxy();
			retry.execute(new Runnable() {

				@Override
				public void run() {
					messageDeal(id + "", dataCache + "");

				}
			});

		}
		logger.info(DateUtils.formatDate(new Date()) + "检测结束");
	}



	/**
	 * 
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private RpcResponse<String> messageDeal(String id, String deviceReportedMessage) {
		logger.info(String.format("[messageDeal()->开始执行,设备上报数据:%s]", deviceReportedMessage));
		try {

			JSONObject parseObject = JSONObject.parseObject(deviceReportedMessage);
			String timestamp = parseObject.getString("timestamp");
			String deviceId = UtilTool.getIotNewDeviceId(parseObject);
			String gatewayId = UtilTool.getIotNewGatewayId(parseObject);
			JSONObject reported = UtilTool.getIotNewRepoted(parseObject);
			// TODO 根据网关id和子设备id查询设备id集合,然后循环
			RpcResponse<List<String>> findDeviceId = deviceQueryService.findDeviceId(gatewayId, deviceId);
			if (!findDeviceId.isSuccess()) {
				logger.error(String.format("[messageReceiveThreadPool()->查询设备id失败:%s]", findDeviceId.getMessage()));
				throw new Exception("fail");
			}
			List<String> locmanDeviceIds = findDeviceId.getSuccessValue();

			if (null == locmanDeviceIds) {
				logger.error("[deviceOperation()->查询设备id失败:设备id集合为null]");
				throw new Exception("fail");
			}
			logger.info(String.format("[messageReceiveThreadPool()->查询获得设备id:%s]", locmanDeviceIds));
			for (String locmanDeviceId : locmanDeviceIds) {

				// 维护状态的设备不进行数据处理
				RpcResponse<Device> res = deviceQueryService.queryDeviceInfoById(locmanDeviceId);
				if (!res.isSuccess()) {
					logger.error(String.format("[messageReceiveThreadPool()->error:%s]", res.getMessage()));
					throw new Exception("fail");
				}
				String deviceDefendState = res.getSuccessValue().getDeviceDefendState();
				if ("fault".equals(deviceDefendState)) {
					logger.info(String.format("[deviceOperation()->suc:设备 = %s,维护状态，数据不进行处理！]", locmanDeviceId));
					/*
					 * return RpcResponseBuilder.buildErrorRpcResp( String.
					 * format("[messageReceiveThreadPool()->suc:设备 = %s,维护状态，数据不进行处理！]"
					 * , locmanDeviceId));
					 */
					// 故障工单维护态,不处理数据
					throw new Exception("success");
				}
				logger.info(String.format("[messageReceiveThreadPool()->success:%s]", "准备添加开关锁数据!"));
				RpcResponse<Boolean> insertSwitchLockInfo = switchLockRecordCrudService
						.insertSwitchLockInfo(locmanDeviceId, reported);
				if (!StringUtils.isBlank(insertSwitchLockInfo.getException())) {
					logger.error("添加开关锁数据异常!");
					throw new Exception("fail");
				}
				
				//成华区使用,设施开关记录表
				logger.info(String.format("[deviceOperation()->success:%s]", "准备添加设施开关数据记录!"));
				switchLockRecordCrudService.insertManholeSwitch(locmanDeviceId, reported, timestamp);
				
				logger.info(String.format("[messageReceiveThreadPool()->success:%s]", "准备添加设备状态数据!"));
				// 查询此时设备开启关闭实时状态，以此判断删除定时器。
				RpcResponse<JSONObject> stateJson = deviceQueryService.queryDeviceLastState(locmanDeviceId);
				// 插入R值（r=√x²+y²+z²）
				String ractv = reported.get("ractv") + "";
				List<Map> maps = JSONObject.parseArray(parseObject.get("attributeInfo") + "", Map.class);
				parseObject.remove("attributeInfo");
				Map<String, Object> jsonObject = new HashMap<String, Object>();

				jsonObject.put("attributeUnit", "");
				jsonObject.put("attributeType", "READ_ONLY");
				jsonObject.put("attributeAlias", "R值");
				jsonObject.put("attributeName", "ractv");
				jsonObject.put("attributeDataType", "STRING");
				jsonObject.put("attributeDefaultValue", "");
				jsonObject.put("attributeReportedValue", ractv);
				jsonObject.put("attributeReportedTime", timestamp);

				setXYZAvg(timestamp, locmanDeviceId, maps);

				maps.add(jsonObject);
				parseObject.put("attributeInfo", maps);
				// 保存订阅到的设备信息
				RpcResponse<String> addDeviceState = factoryAppTagCrudService.addDeviceState(parseObject, gatewayId,
						locmanDeviceId);
				if (!addDeviceState.isSuccess()) {
					logger.error("添加设备状态数据异常!");
					throw new Exception("fail");
				}
				//设备进入自动检测
				//设备故障工单状态处于检测中
				Map<String,Object> faultOrderInfo=deviceReportedCrudRepository.getDeviceFaultOrderState(locmanDeviceId);
				if(null !=faultOrderInfo &&faultOrderInfo.size()>0) {
					logger.info(String.format("进入inspectionMethods方法:%s",faultOrderInfo.toString()));
					try {
						inspectionMethods(timestamp, reported, locmanDeviceId, faultOrderInfo);
					}catch(Exception e) {
						logger.error("messageDeal()->inspectionMethods exception", e);
					}
				}
				RpcResponse<String> checkExistFaultOrder = checkExistFaultOrder(locmanDeviceId, res);
				if (null != checkExistFaultOrder) {
					continue;
				}
				
				// 若是xgiv为2且为rt为trigger，需要消除告警
				if (checkXgiv(parseObject)) {
					try {
						//dealAlarm(locmanDeviceId, parseObject);
						((DeviceReportedCrudService) proxy1).dealAlarm(locmanDeviceId, parseObject);
					} catch (Exception e) {
						System.out.println(e);
						logger.error("messageDeal()->dealAlarm exception", e);
						return RpcResponseBuilder.buildExceptionRpcResp(e);
					}

				}
				// 上报数据同规则比较，产生告警
				logger.info(DateUtils.formatDate(new Date()) + "***上报数据同规则比较，产生告警进入alarmProduce:"
						+ System.currentTimeMillis());
				RpcResponse<String> alarmProduce = deviceAlarmProduceService.alarmProduce(reported, locmanDeviceId,
						timestamp);
				if (StringUtils.isNotBlank(alarmProduce.getException())) {
					logger.error("告警规则匹配异常!");
					throw new Exception("fail");
				}
				// 此设备上报的状态是关闭，且存在定时器，则删除定时器。
				if (stateJson.isSuccess() && null != stateJson.getSuccessValue()) {
					this.deleteTimer(UtilTool.getIotNewRepoted(stateJson.getSuccessValue()), reported, locmanDeviceId);
				}
				// 修改下发命令记录状态
				this.updateControlRecord(reported, locmanDeviceId, timestamp);
				logger.info("循环完一次");
			}
			logger.info("即将删除已完成数据");
			dataCacheCrudRepository.deleteCacheDataById(id);
			logger.info("[messageReceiveThreadPool: invalid：保存设备上报数据成功!]");
			return RpcResponseBuilder.buildSuccessRpcResp("保存设备上报数据成功!", "保存设备上报数据成功!");
		} catch (Exception e) {
			if ("success".equals(e.getMessage())) {
				dataCacheCrudRepository.deleteCacheDataById(id);
				logger.info("[messageReceiveThreadPool: invalid：设备处于故障工单维护态,忽略此次上报信息!]");
				return RpcResponseBuilder.buildSuccessRpcResp("设备处于故障工单维护态,忽略此次上报信息!", "处理完毕");
			} else {
				dataCacheCrudRepository.updateDataCacheRetryCount(id);
				logger.error("messageReceiveThreadPool()->exception", e);
				return RpcResponseBuilder.buildExceptionRpcResp(e);
			}
		}
	}



	/**
	* @Description:
	* @param timestamp
	* @param reported
	* @param locmanDeviceId
	* @param faultOrderInfo
	 * @throws Exception
	*/
	
	private void inspectionMethods(String timestamp, JSONObject reported, String locmanDeviceId,
			Map<String, Object> faultOrderInfo) throws Exception {
		String faultOrderState=faultOrderInfo.get("processState")+"";
		String accessSecret=faultOrderInfo.get("accessSecret")+"";
		String updateTime =faultOrderInfo.get("updateTime")+"";
		if("4".equals(faultOrderState) && StringUtils.isNotBlank(updateTime)) {
			Long timeStampLong=Long.parseLong(timestamp);
			Long updateTimeStamp=Long.parseLong(DateUtils.dateToStamp(updateTime));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//第三天的23：59:59时间戳字符串endTimeStamp
			Long endTimeStamp=Long.parseLong(DateUtils.dateToStamp(DateUtils.formatDate(sdf.parse(updateTime), DateUtils.DATE_END)))+3*86400000L;
			logger.info("endTimeStamp"+endTimeStamp);
			//第七天的23：59:59时间戳字符串endTimeStamp
			Long SevenTimeStamp=Long.parseLong(DateUtils.dateToStamp(DateUtils.formatDate(sdf.parse(updateTime), DateUtils.DATE_END)))+7*86400000L;
			logger.info("SevenTimeStamp"+SevenTimeStamp);
			// 查询设备下的自定义告警规则
			RpcResponse<List<AlarmRule>> queryAlarmRuleByDeviceId = alarmRuleQueryService
					.queryAlarmRuleByDeviceId(locmanDeviceId, accessSecret); 
			if (!queryAlarmRuleByDeviceId.isSuccess()) {
				logger.warn("[inspectionMethods()->warn：根据设备ID查询告警规则失败！]");
			}
			List<AlarmRule> alarmRules = queryAlarmRuleByDeviceId.getSuccessValue();
			logger.info("alarmRules++++"+alarmRules.toString());
			// 设备上未绑定自定义规则
			if (alarmRules == null || alarmRules.size() == 0) {
				alarmRules = findAlarmRule(locmanDeviceId, accessSecret);
				}
			if (alarmRules == null) {
				logger.error("alarmProduce()-->没有查询到此设备类型的告警规则!!!");
				return;
				}
			logger.info("alarmRules----"+alarmRules.toString());
			// 封装完整规则数据
			List<String> rules = Lists.newArrayList(); 
			//
			List<Map<String,Object>> datas=Lists.newArrayList();
			//实时数据
			Map<String,Object> reportDate=new HashMap<String,Object>();
			// 组装数据
			for (AlarmRule alarmRule : alarmRules) {
				//dealData(reported, deviceId, timestamp, rules, datas, alarmRule);
				Set<String> keySet =reported.keySet();
				for(String propName:keySet) {
					reportDate.put(propName, reported.getString(propName));
				}
				rules.add(alarmRule.getRule());
				reportDate.put(AlarmInfoConstants.REPORTTIME, timestamp);
				reportDate.put("_rule", alarmRule);
				reportDate.put(AlarmInfoConstants.DEVICEID, locmanDeviceId);
				reportDate.put("_engine_check", new Boolean(false));
				logger.info("reportDate值："+reportDate.toString());
				datas.add(reportDate);
			}
			logger.info("inspectionMethods()->告警规则：" + rules);
			logger.info("inspectionMethods()->上报数据：" + datas);
			int ruleFiredCount = 0;
			if(datas != null && datas.size() > 0 && rules != null && rules.size() > 0) {
				for(int i=0;i<rules.size();i++) {
					Boolean invokAlarmCheck=alarmRuleInvokInterface.invokAlarmCheck(datas.get(i),rules.get(i));
					if(invokAlarmCheck) {
						ruleFiredCount++;
					}
				}
			}
			logger.info(String.format("[inspectionMethods()->ruleFiredCount:%s]",ruleFiredCount));
			//连续三天
			if(updateTimeStamp<timeStampLong && timeStampLong <= endTimeStamp) {
				
				if(ruleFiredCount == 0) {
					//数据存Inspection表 
					Inspection inspection=new Inspection();
					inspection.setId(UtilTool.getUuId());
					inspection.setDeviceId(locmanDeviceId);
					inspection.setReportedTime(DateUtils.stampToDate(timestamp));
					inspection.setInspectionTime(updateTime);
					logger.info("inspectionMethods()->inspection:"+inspection.toString());
					int insertInspection=deviceReportedCrudRepository.insertInspection(inspection);
					if(insertInspection >0) {
						logger.info("插入insertInspection数据成功！");
					}
					
				}
				Map<String,Object> inMap=Maps.newHashMap();
				inMap.put("deviceId",locmanDeviceId );
				inMap.put("endTime",DateUtils.stampToDate(endTimeStamp+""));
				inMap.put("updateTime", updateTime);
				int countByDeviceId=deviceReportedCrudRepository.countByDeviceId(inMap);
				logger.info(String.format("[inspectionMethods()->countByDeviceId:%s]", countByDeviceId));
				if(countByDeviceId >= 3) {
					//调用完成接口
					JSONObject paramsObject=new JSONObject();
					paramsObject.put("id", faultOrderInfo.get("id")+"");
					paramsObject.put("processId",faultOrderInfo.get("processId")+"");
					paramsObject.put("userId",faultOrderInfo.get("userId")+"");
					paramsObject.put("faultProcessType",faultOrderInfo.get("faultProcessType")+"");
					paramsObject.put("operationType","完成");
					paramsObject.put("detail","自动检测通过");
					paramsObject.put("accessSecret",accessSecret);
					faultOrderProcessCudService.updateFaultOrderState(paramsObject);
				}
				//3-7天
			}else if(endTimeStamp < timeStampLong && timeStampLong <= SevenTimeStamp) {
				if(ruleFiredCount==0) {
					//调用完成接口
					JSONObject paramsObject=new JSONObject();
					paramsObject.put("id", faultOrderInfo.get("id")+"");
					paramsObject.put("processId",faultOrderInfo.get("processId")+"");
					paramsObject.put("userId",faultOrderInfo.get("userId")+"");
					paramsObject.put("faultProcessType",faultOrderInfo.get("faultProcessType")+"");
					paramsObject.put("operationType","完成");
					paramsObject.put("detail","自动检测通过");
					paramsObject.put("accessSecret",accessSecret);
					faultOrderProcessCudService.updateFaultOrderState(paramsObject);
				}
			}else {
				//调用返厂接口
				JSONObject paramsObject=new JSONObject();
				paramsObject.put("id", faultOrderInfo.get("id")+"");
				paramsObject.put("processId",faultOrderInfo.get("processId")+"");
				paramsObject.put("userId",faultOrderInfo.get("userId")+"");
				paramsObject.put("faultProcessType",faultOrderInfo.get("faultProcessType")+"");
				paramsObject.put("operationType","返厂");
				paramsObject.put("detail","自动检测未通过");
				paramsObject.put("accessSecret",accessSecret);
				
				faultOrderProcessCudService.updateFaultOrderState(paramsObject);
			}
			
			}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	private void setXYZAvg(String timestamp, String locmanDeviceId, List<Map> maps) {
		JSONObject XYZAvgJson = deviceReportedCrudRepository.getXYZAvg(locmanDeviceId);
		if (null != XYZAvgJson && !XYZAvgJson.isEmpty()) {
			Set<Entry<String, Object>> entrySet = XYZAvgJson.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				entry.getKey();
				JSONObject attributeJson = new JSONObject();
				attributeJson.put("attributeUnit", "");
				attributeJson.put("attributeType", "READ_ONLY");
				attributeJson.put("attributeAlias", entry.getKey() + "平均值");
				attributeJson.put("attributeName", entry.getKey());
				attributeJson.put("attributeDataType", "STRING");
				attributeJson.put("attributeDefaultValue", "");
				attributeJson.put("attributeReportedValue", entry.getValue());
				attributeJson.put("attributeReportedTime", timestamp);
				maps.add(attributeJson);
			}
		}

	}



	/**
	 * 
	 * @Description:成华区业务需求，开井检查后马上关闭，通过xgiv和rt字段判断，上报数据若xgiv为2,且rt为timing,则说明井已关闭，返回true,则需要消除开井时产生的相应告警，否则不需要消除，返回false
	 * @param jsonObject
	 * @return
	 */
	private boolean checkXgiv(JSONObject jsonObject) {
		logger.info(String.format("[进入checkXgiv()->上报数据:%s]", jsonObject.toJSONString()));
		JSONArray attributeInfo = jsonObject.getJSONArray("attributeInfo");
		if (attributeInfo == null) {
			logger.info("[checkXgiv()->attributeInfo为null]");
			return false;
		}
		// 用来记录rt值
		String rt = "";
		// 记录xgiv值
		Integer xgiv = 100;
		for (int i = 0; i < attributeInfo.size(); i++) {
			JSONObject json = attributeInfo.getJSONObject(i);
			if (json.getString("attributeName").equals("rt")) {
				rt = json.getString("attributeReportedValue");
				continue;
			}
			if (json.getString("attributeName").equals("xgiv")) {
				xgiv = json.getInteger("attributeReportedValue");
			}
		}
		if (xgiv == 2 && rt.equals("trigger")) {
			logger.info("[checkXgiv()->xgiv值为2且rt为trigger，需要消除开启告警]");
			return true;
		} else {
			logger.info("[checkXgiv()->不消除告警]");
			return false;
		}
	}



	/**
	 * 
	 * @Description:成华区业务需求，处理开井检查产生的相应告警，更改告警短信状态
	 * @param jsonObject
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void dealAlarm(String deviceId, JSONObject jsonObject) {
		logger.info(String.format("[进入dealAlarm()->deviceId:%s,上报数据:%s]", deviceId, jsonObject.toJSONString()));
		 //井盖开井检查后关闭，将开启时产生的相应告警处理
		try {
			//TODO  需要测试问题
			// 事务回滚点,如果出现异常,据此回滚到指定点,避免次要数据异常导致主要数据全部回滚;
			rollBackPoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
		} catch (NoTransactionException e) {
			logger.info("dealAlarm()->回滚点设置异常");
			logger.error("dealAlarm()->exception", e);
		}
		try {
//			// 获得上报时间
//			long reportTime = Long.parseLong(jsonObject.get("timestamp").toString());
//			// 获取5分钟前的时间，用于处理之前的告警
//			String startTime = UtilTool.timeStampToDate(reportTime - 300000);
//			String endTime = UtilTool.timeStampToDate(reportTime);

			// 获取告警工单id
			String alarmOrderId = alarmInfoCrudRepository.getAlarmOrderId(deviceId);
			if (StringUtils.isBlank(alarmOrderId)) {
				logger.info("[dealAlarm()->没有产生相应工单，无需处理]");
			}

			// 将告警对应的短信记录状态置为2（取消发送）
			List<String> alarmSerialNum = alarmInfoCrudRepository.getAlarmSerialNumForCancelSend(deviceId);
			if (alarmSerialNum == null || alarmSerialNum.size() == 0) {
				logger.info(String.format("[dealAlarm()->无需要取消发送的告警短信,deviceId:%s]", deviceId));
			} else {
				int updateSmsRecordNum = alarmInfoCrudRepository.updateSmsRecordState(alarmSerialNum);
				logger.info(String.format("[dealAlarm()->已取消发送%s条短信]", updateSmsRecordNum));
			}

			// 处理告警信息
			int dealAlarmInfo = alarmInfoCrudRepository.dealAlarmInfoState(deviceId);
			logger.info(String.format("[dealAlarm()->已处理%s条告警]", dealAlarmInfo));
			
			Facilities facilities = facilitiesCrudRepository.getFacByDeviceId(deviceId);
			if (null != facilities) {
				// 更新redis缓存
				Map<String, Object> redisMap = new HashMap<String, Object>();
				redisMap.put("id", facilities.getId());
				redisMap.put("accessSecret", facilities.getAccessSecret());
				updateRedisCrudService.updateFacMapCache(redisMap);
			}
			
			
			// 获取该告警工单相关联的一般告警的id,用于判断是否完成该工单,因为该工单若是关联了一般告警，则不需要完成该工单
			List<String> alarmIdByAlarmOrderId = alarmInfoCrudRepository.getAlarmIdByAlarmOrderId(alarmOrderId);
			// 该工单没有关联一般告警，处理告警工单
			if (alarmIdByAlarmOrderId.size() == 0 || alarmIdByAlarmOrderId.isEmpty()) {
				int dealAlarmOrder = alarmInfoCrudRepository.dealAlarmOrder(alarmOrderId);
				logger.info(String.format("[dealAlarm()->已处理%s条告警工单]", dealAlarmOrder));
				if(dealAlarmOrder >0) {
					//app推送
					//查询处理告警的设施信息
					Map<String ,Object> facInfo=alarmInfoCrudRepository.getFacInfoByAlarmOrderId(alarmOrderId);
					String organizationId=facInfo.get("organizationId")+"";
					String facilitiesCode=facInfo.get("facilitiesCode")+"";
					String serialNum=facInfo.get("serialNum")+"";
					String address=facInfo.get("address")+"";
					String closeTime=DateUtils.stampToDate(jsonObject.getString("timestamp"));
					//查询推送userIds
					List<String> userIds = new ArrayList<>();
					JSONObject json = new JSONObject();
					json.put("organizeId", organizationId);
					json.put("receiveSms", "true");
		
					RpcResponse<List<Map>> userListByOrgId = userRoleQueryService.getAllUserListByOrgId(json);
					if (userListByOrgId.isSuccess()) {
						List<Map> userList = userListByOrgId.getSuccessValue();
						if (userList != null && userList.size() > 0) {
							for (Map mapUser : userList) {
								String userId = mapUser.get("_id") == null ? null : String.valueOf(mapUser.get("_id"));
								if (userId != null) {
									userIds.add(userId);
								}
							}
						}
					}
					//內容
					StringBuffer content = new StringBuffer();
					content.append(address);
					content.append("设施序列号为");
					content.append(facilitiesCode);
					content.append(",在");
					content.append(closeTime);
					content.append("已经关闭，且告警工单号为");
					content.append(serialNum);
					content.append("的告警工单已经自动处理！");
					
					JiguangEntity jiguangEntity = new JiguangEntity();
					jiguangEntity.setAliasIds(userIds);
					jiguangEntity.setMsgContent(content.toString());
					jiguangEntity.setNotificationTitle("井盖关闭");
					jiguangEntity.setMsgTitle("井盖关闭");
					RpcResponse<Object> jiguangPush = jiguangService.sendMessage(jiguangEntity);
					logger.info(DateUtils.formatDate(new Date()) + "***极光推送:" + System.currentTimeMillis());
					if (jiguangPush == null) {
						logger.error("dealAlarm()->error:极光推送失败,jiguangService.sendMessage()无返回信息");
					} else if (!jiguangPush.isSuccess()) {
						logger.error(jiguangPush.getMessage() + "dealAlarm()->error:极光推送失败");
					} else {
						logger.info(jiguangPush.getMessage() + "dealAlarm()->success:极光推送成功");
					}
				}
			}

		} catch (Exception e) {
			logger.error("[dealAlarm()->exception]", e);
			TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(rollBackPoint);
			return;
		}
	}
	private List<AlarmRule> findAlarmRule(String deviceId, String accessSecret) {
		List<AlarmRule> alarmRules;
		List<String> listDeviceId = new ArrayList<>();
		listDeviceId.add(deviceId);
		RpcResponse<List<Map<String, Object>>> deviceDetail = deviceQueryService
				.queryBatchDeviceInfoForDeviceIds(listDeviceId);
		List<Map<String, Object>> successValue = deviceDetail.getSuccessValue();
		if (successValue == null || !deviceDetail.isSuccess()) {
			logger.error("[alarmProduce()->invalid：设备类型获取失败]");
			return null;
		}
		Map<String, Object> map = null;
		if (successValue.size() > 0) {
			map = successValue.get(0);
		}
		String deviceTypeId = "";
		if (null != map) {
			deviceTypeId = map.get("deviceTypeId").toString();
		}
		// 获取该类型对应的告警规则
		RpcResponse<List<AlarmRule>> response = alarmRuleQueryService.getByDeviceTypeId(deviceTypeId,
				accessSecret);
		if (!response.isSuccess()) {
			logger.error("[alarmProduce()->invalid：获取设备类型id:" + deviceTypeId + "对应的告警规则失败！]");
			return null;
		}
		alarmRules = response.getSuccessValue();
		return alarmRules;
	}
	
	

	@Scheduled(fixedRate = 900 * 1000)
	protected void freshTransTable(){
		try {
			List<Map<String, Object>> tranTable = deviceReportedCrudRepository.getTranTable();
			paraNameTransTable  = new HashMap<>();
			for(Map<String, Object> map1 : tranTable){
				paraNameTransTable.put((String)map1.get("engKey"),(String) map1.get("chineseValue"));
			}
			logger.info("changeTran()->success");
		} catch (Exception e) {
			logger.error("[changeTran()->exception]", e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#getTransTable()
	 */
	@Override
	public Map<String, String> getTransTable() {
		// TODO Auto-generated method stub
		return paraNameTransTable;
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#commandResponseForWingsIot(java.lang.String)
	 */
	@Override
	public RpcResponse<String> commandResponseForWingsIot(String deviceId) {
		// TODO Auto-generated method stub
		if (null == deviceId) {
			logger.error("commandResponseForWingsIot()->error,设备id传入为空");
			return RpcResponseBuilder.buildErrorRpcResp("设备id为空");
		}
		try {
			int result = deviceReportedCrudRepository.updateCommandReceive(deviceId, "1");
			if (result > 0) {
				logger.info("commandResponseForWingsIot()->更新成功，deviceId" + deviceId);
				return RpcResponseBuilder.buildSuccessRpcResp("success", null);
			}
			else{
				logger.error("commandResponseForWingsIot()->更新失败，deviceId" + deviceId);
				return RpcResponseBuilder.buildErrorRpcResp("error");
			}
			
		} catch (Exception e) {
			logger.error("commandResponseForWingsIot()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
	
	
}
