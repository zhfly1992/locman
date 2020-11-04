///*
// * File name: KafkaConsumerReceiver.java
// *
// * Purpose:
// *
// * Functions used and called: Name Purpose ... ...
// *
// * Additional Information:
// *
// * Development History: Revision No. Author Date 1.0 田明 2017年11月20日 ... ... ...
// *
// ***************************************************/
//package com.run.locman.kafaka;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import com.alibaba.fastjson.JSONObject;
//import com.run.data.center.api.es.query.DeviceEsQueryService;
//import com.run.entity.common.RpcResponse;
//import com.run.entity.tool.DateUtils;
//import com.run.entity.tool.ResultBuilder;
//import com.run.locman.api.crud.service.DeviceStateHistoryCrudService;
//import com.run.locman.api.drools.service.AlarmRuleInvokInterface;
//import com.run.locman.api.entity.AlarmRule;
//import com.run.locman.api.entity.DeviceStateHistory;
//import com.run.locman.api.entity.Facilities;
//import com.run.locman.api.query.service.AlarmRuleQueryService;
//import com.run.locman.api.query.service.DeviceQueryService;
//import com.run.locman.api.query.service.FacilitiesQueryService;
//import com.run.locman.api.query.service.FacilityDeviceQueryService;
//import com.run.locman.api.util.UtilTool;
//import com.run.locman.constants.AlarmInfoConstants;
//
///**
// * @Description: Kafka
// * @author: 田明
// * @version: 1.0, 2017年11月20日
// */
//@Component
//public class KafkaConsumerReceiver {
//
//	@Autowired
//	private FacilityDeviceQueryService		facilityDeviceQueryService;
//
//	@Autowired
//	private AlarmRuleQueryService			alarmRuleQueryService;
//
//	@Autowired
//	private AlarmRuleInvokInterface			alarmRuleInvokInterface;
//
//	@Autowired
//	private FacilitiesQueryService			facilitiesQueryService;
//
//	@Autowired
//	private DeviceStateHistoryCrudService	deviceStateHistoryCrudService;
//
//	@Autowired
//	DeviceQueryService						deviceQueryService;
//
//	private static final Logger				logger	= Logger.getLogger(KafkaConsumerReceiver.class);
//
//
//
//	@KafkaListener(topics = "StatusTopic")
//	public void lisenStatu(ConsumerRecord<String, String> record) {
//		System.err.println(record);
//	}
//
//
//
//	@KafkaListener(topics = AlarmInfoConstants.THINGTOPIC)
//	public void listen(ConsumerRecord<String, String> record) {
//		try {
//			Optional<String> kafkaValue = Optional.ofNullable(record.value());
//			Optional<String> kafkaKey = Optional.ofNullable(record.key());
//			if (kafkaKey.isPresent() && kafkaValue.isPresent()) { // 设备ID和上报数据存在
//				String deviceId = kafkaKey.get();
//				JSONObject object = JSONObject.parseObject(kafkaValue.get());
//				JSONObject state = object.getJSONObject(AlarmInfoConstants.STATE);
//				JSONObject reported = state.getJSONObject(AlarmInfoConstants.REPORTED); // 获取上报数据
//				String reportTime = DateUtils.stampToDate(object.getString(AlarmInfoConstants.TIMESTAMP) + "000"); // 设备上报时间
//
//				// 保存上报历史数据
//				DeviceStateHistory deviceStateHistory = new DeviceStateHistory();
//				deviceStateHistory.setId(UtilTool.getUuId());
//				deviceStateHistory.setDeviceId(deviceId);
//				deviceStateHistory.setDatas(JSONObject.toJSONString(reported));
//				deviceStateHistory.setReportTime(reportTime);
//				deviceStateHistoryCrudService.saveDeviceStateHistory(deviceStateHistory);
//
//				RpcResponse<String> deviceBindingState = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
//				if (!deviceBindingState.isSuccess()) {
//					return;
//				}
//				if ("".equals(deviceBindingState.getSuccessValue()) || deviceBindingState.getSuccessValue() == null) {// 设备未绑定
//					logger.error("[KafkaConsumerReceiver->listen()->invalid：设备：" + deviceId + "未绑定设施！]");
//					return;
//				}
//				// 获取所绑定设施
//				String facilityId = deviceBindingState.getSuccessValue();
//
//				// 获取设施类型ID及接入方秘钥
//				RpcResponse<Facilities> findById = facilitiesQueryService.findById(facilityId);
//				if (!findById.isSuccess()) {
//					logger.error("[KafkaConsumerReceiver->listen()->invalid：设施详情查询失败！]");
//					return;
//				}
//				Facilities facilities = findById.getSuccessValue();
//				// 设施停用
//				if ("disable".equals(facilities.getManageState())) {
//					logger.warn("[KafkaConsumerReceiver->listen()->warn：设施已停用！]");
//					return;
//				}
//
//				String accessSecret = facilities.getAccessSecret();
//
//				RpcResponse<List<AlarmRule>> queryAlarmRuleByDeviceId = alarmRuleQueryService
//						.queryAlarmRuleByDeviceId(deviceId, accessSecret); // 查询设备下的告警规则
//				if (!queryAlarmRuleByDeviceId.isSuccess()) {
//					logger.warn("[KafkaConsumerReceiver->listen()->warn：根据设备ID查询告警规则失败！]");
//					return;
//				}
//
//				List<AlarmRule> alarmRules = queryAlarmRuleByDeviceId.getSuccessValue();
//				if (alarmRules == null || alarmRules.size() == 0) { // 设备上未绑定自定义规则
//
//					List<String> listDeviceId = new ArrayList<>();
//					listDeviceId.add(deviceId);
//					// 获取设备类型
//					// RpcResponse<Map<String, Object>> deviceDetail =
//					// deviceEsQueryService.queryDeviceDetail(deviceId);
//					RpcResponse<List<Map<String, Object>>> deviceDetail = deviceQueryService
//							.queryBatchDeviceInfoForDeviceIds(listDeviceId);
//					List<Map<String, Object>> successValue = deviceDetail.getSuccessValue();
//					if (successValue == null || !deviceDetail.isSuccess()) {
//						logger.error("[KafkaConsumerReceiver->listen()->invalid：设备类型获取失败]");
//						return;
//					}
//					Map<String, Object> map = null;
//					if (successValue.size() > 0) {
//						map = successValue.get(0);
//					}
//					String deviceTypeId = "";
//					if (null != map) {
//						deviceTypeId = map.get("deviceTypeId").toString();
//					}
//
//					// 获取该类型对应的告警规则
//					RpcResponse<List<AlarmRule>> response = alarmRuleQueryService.getByDeviceTypeId(deviceTypeId,
//							accessSecret);
//					if (!response.isSuccess()) {
//						logger.error(
//								"[KafkaConsumerReceiver->listen()->invalid：获取设备类型id:" + deviceTypeId + "对应的告警规则失败！]");
//						return;
//					}
//					alarmRules = response.getSuccessValue();
//				}
//
//				List<String> rules = new ArrayList<>(); // 封装完整规则数据
//				List<Map<String, Object>> datas = new ArrayList<>(); // 封装上报数据和设备设施数据
//				// 组装数据
//				for (AlarmRule alarmRule : alarmRules) {
//					Map<String, Object> deviceRealState = new HashMap<>();
//					/** 设备上报数据 */
//					if (reported != null) { // 解析上报数据
//						Set<String> keySet = reported.keySet();
//						for (String propName : keySet) {
//							deviceRealState.put(propName, reported.get(propName));
//						}
//					}
//					rules.add(alarmRule.getRule()); // 封装完整规则数据
//					/** 告警规则相关数据 */
//					deviceRealState.put(AlarmInfoConstants._REPORTTIME, reportTime);
//					deviceRealState.put("_rule", alarmRule);
//					deviceRealState.put(AlarmInfoConstants._DEVICEID, deviceId);
//					deviceRealState.put("_engine_check", new Boolean(false));
//					datas.add(deviceRealState);
//				}
//				logger.info("告警规则：" + rules);
//				logger.info("上报数据：" + datas);
//
//				alarmRuleInvokInterface.invokAlarm(datas, rules); // 调用规则引擎方法，datas：部分数据作为中转传递到工单生成的方法
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			e.printStackTrace();
//		}
//	}
//
//}
