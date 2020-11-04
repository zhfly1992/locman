///*
//* File name: TestController.java								
//*
//* Purpose:
//*
//* Functions used and called:	
//* Name			Purpose
//* ...			...
//*
//* Additional Information:
//*
//* Development History:
//* Revision No.	Author		Date
//* 1.0			李康诚		2017年11月20日
//* ...			...			...
//*
//***************************************************/
//package com.run.locman.drools.demo;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.log4j.Logger;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.run.data.center.api.es.query.DeviceEsQueryService;
//import com.run.entity.common.RpcResponse;
//import com.run.entity.tool.DateUtils;
//import com.run.entity.tool.ResultBuilder;
//import com.run.http.client.util.HttpClientUtil;
//import com.run.locman.api.crud.service.DeviceInfoCudService;
//import com.run.locman.api.drools.service.AlarmRuleInvokInterface;
//import com.run.locman.api.drools.service.TestInterface;
//import com.run.locman.api.entity.AlarmRule;
//import com.run.locman.api.entity.Device;
//import com.run.locman.api.entity.Facilities;
//import com.run.locman.api.entity.RemoteControlRecord;
//import com.run.locman.api.query.service.AlarmRuleQueryService;
//import com.run.locman.api.query.service.FacilitiesQueryService;
//import com.run.locman.api.query.service.FacilitiesTypeQueryService;
//import com.run.locman.api.query.service.FacilityDeviceQueryService;
//import com.run.locman.constants.AlarmInfoConstants;
//import com.run.locman.kafaka.KafkaConsumerReceiver;
//import com.run.usc.base.query.UserBaseQueryService;
//
///**
// * @Description: 测试用
// * @author: 李康诚
// * @version: 1.0, 2017年11月20日
// */
//@RequestMapping("/test")
//@Controller
//@CrossOrigin(value = "*")
//public class TestController {
//	@Autowired
//	private DeviceEsQueryService		deviceEsQueryService;
//	@Autowired
//	private FacilityDeviceQueryService	facilityDeviceQueryService;
//
//	@Autowired
//	private AlarmRuleQueryService		alarmRuleQueryService;
//
//	@Autowired
//	private AlarmRuleInvokInterface		alarmRuleInvokInterface;
//	@Autowired
//	private FacilitiesQueryService		facilitiesQueryService;
//
//	@Autowired
//	private TestInterface				testInterface;
//
//	@Autowired
//	private FacilitiesTypeQueryService	facilitiesTypeQueryService;
//	
//	@Autowired
//	private DeviceInfoCudService		deviceInfoCudService;
//
//	@Autowired
//	private UserBaseQueryService		userQueryRpcService;
//
//	private static final Logger			logger	= Logger.getLogger(KafkaConsumerReceiver.class);
///*
//	@Autowired
//	private KafkaTemplate<String, String> kafkaTemplate;
//*/
////	@Test
//	@GetMapping(value = "/testData")
//	public void testHttpClient(){
//		JSONObject josn = new JSONObject();
//		josn.put("appTags", "a78c08576c5344009aeb0b35eaf997a0");
//		String result = HttpClientUtil.getInstance().doPost("http://localhost:8017/v3/appTag/devices", JSONObject.toJSONString(josn), "");
//		JSONObject resultJson = JSON.parseObject(result);
//		JSONObject resultStatus = resultJson.getJSONObject("resultStatus");
//		if ("0000".equals(resultStatus.getString("resultCode"))) {
//			JSONArray datas = resultJson.getJSONObject("value").getJSONArray("deviceInfoList");
//			if (datas != null && !datas.isEmpty()) {
//				List<Device> devices = new ArrayList<>();
//				for (Object dataObj : datas) {
//					JSONObject data = JSON.parseObject(dataObj.toString());
//					Device device = new Device();
//					device.setId(data.getString("deviceId"));
//					device.setDeviceName(data.getString("deviceName"));
//					device.setDeviceKey(data.getString("deviceKey"));
//					device.setProtocolType(data.getString("protocolType"));
//					device.setOpenProtocols(data.getString("openProtocols"));
//					device.setDeviceType(data.getString("deviceTypeId"));
//					device.setFirstOnlineTime(data.getString("firstOnlineTime"));
//					device.setLastReportTime(data.getString("lastReportTime"));
//					device.setOnlineState(data.getString("onlineState"));
//					
//					devices.add(device);
//				}
//				RpcResponse<Integer> saveDevicesResult = deviceInfoCudService.saveDevices(devices);
//				System.out.println(saveDevicesResult);
//			}
//		}
//	}
//	
//	
//	@Test
//	public void testKafka(){
//		// String zkStr =
//				// "131.10.10.201:9092,131.10.10.202:9092,131.10.10.203:9092";
//				// String groupId = "test-consumer-group";
//				// String topics = "StatusTopic,ThingsConsoleTopics";
//				// int threads = 1;
//
//				Properties props = new Properties();
//			props.put("bootstrap.servers", "193.168.0.90:9092,193.168.0.91:9092,193.168.0.93:9092");
//				//	props.put("bootstrap.servers", "131.10.10.202:9092");
//				props.put("group.id", "00000");
//				props.put("enable.auto.commit", "false");
//				props.put("auto.commit.interval.ms", "1000");
//				props.put("session.timeout.ms", "30000");
//				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//				props.put("auto.offset.reset", "earliest");
//				//props.put("auto.offset.reset", "latest");
//				KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//				try {
//					consumer.subscribe(Arrays.asList("ThingTopic"));
//					while (true) {
//						ConsumerRecords<String, String> records = consumer.poll(100);
//						for (ConsumerRecord<String, String> record : records)
//									record.value();
//						//System.in.read();
//					}
//				} catch (Exception e) {
//					consumer.close();
//				}
//	}
//	
//	@Test
//	public void testJson(){
//		RemoteControlRecord remoteControlRecord = new RemoteControlRecord();
//		remoteControlRecord.setControlItem("aaa");
//		remoteControlRecord.setControlValue("bbb");
//		String desired = "{\"state\":{\"desired\":{\""+remoteControlRecord.getControlItem()+"\":\""+remoteControlRecord.getControlValue()+"\"}}}";
//		JSONObject json = JSON.parseObject(desired);
//	}
//	
//	
//	@GetMapping(value = "/testEs")
//	public void testEs(){
//		try {
//			RpcResponse<Map<String,Object>> queryDeviceDetail = deviceEsQueryService.queryDeviceDetail("01vDhHSWxv21NJNrZ9kl");
//			System.err.println(queryDeviceDetail.getSuccessValue());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@SuppressWarnings("rawtypes")
//	@ResponseBody
//
//	@RequestMapping("/address")
//	public void test() {
//		try {
//			RpcResponse userByUserId = userQueryRpcService.getUserByUserId("c6c9acd29c6c40038711c4d194f9a175");
//			// export.testExport();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//
//
//	@ResponseBody
//
//	@RequestMapping("/reload")
//	public String reload() throws Exception {
//		testInterface.reload();
//		return "ok";
//	}
//
//
///*	@PostMapping(value = "/testDesired")
//	public void testDesired(){
//		kafkaTemplate.send("ThingTopic", "{abc}");
//	}*/
//	
//	
//	public static void main(String[] args) {
//		Map<String, Object> data = new HashMap<>();
//		data.put("facilitiesCode", "a");
//		Object json = JSONObject.toJSON(data);
//	}
//
//	/**
//	 * 设备上报，告警测试
//	 * 
//	 * @Description:
//	 * @param param
//	 */
//	@PostMapping(value = "/testReport")
//	public void testReport(@RequestBody String param) {
//		try {
//			JSONObject objects = JSONObject.parseObject(param);
//			String deviceId = objects.getString("key");
//			JSONObject object = objects.getJSONObject("value");
//			RpcResponse<String> deviceBindingState = facilityDeviceQueryService.queryDeviceBindingState(deviceId);
//			if (!deviceBindingState.isSuccess()) {
//				return;
//			}
//			if ("".equals(deviceBindingState.getSuccessValue()) || deviceBindingState.getSuccessValue() == null) {// 设备未绑定
//				logger.error("[KafkaConsumerReceiver->listen()->invalid：设备：" + deviceId + "未绑定设施！]");
//				return;
//			}
//			// 获取所绑定设施
//			String facilityId = deviceBindingState.getSuccessValue();
//
//			// 获取设施类型ID及接入方秘钥
//			RpcResponse<Facilities> findById = facilitiesQueryService.findById(facilityId);
//			if (!findById.isSuccess()) {
//				logger.error("[KafkaConsumerReceiver->listen()->invalid：设施详情查询失败！]");
//				return;
//			}
//			Facilities facilities = findById.getSuccessValue();
//			// 设施停用
//			if ("disable".equals(facilities.getManageState())) {
//				logger.warn("[KafkaConsumerReceiver->listen()->warn：设施已停用！]");
//				return;
//			}
//
//			String accessSecret = facilities.getAccessSecret();
//			RpcResponse<List<AlarmRule>> queryAlarmRuleByDeviceId = alarmRuleQueryService.queryAlarmRuleByDeviceId(deviceId, accessSecret);	//查询设备下的告警规则
//			if (!queryAlarmRuleByDeviceId.isSuccess()) {
//				logger.warn("[KafkaConsumerReceiver->listen()->warn：根据设备ID查询告警规则失败！]");
//				return;
//			}
//			
//			List<AlarmRule>  alarmRules = queryAlarmRuleByDeviceId.getSuccessValue();
//			if (alarmRules == null || alarmRules.size() == 0) {	//设备上未绑定自定义规则
//				// 获取设备类型
//				RpcResponse<Map<String, Object>> deviceDetail = deviceEsQueryService.queryDeviceDetail(deviceId);
//				if (!deviceDetail.isSuccess()) {
//					logger.error("[KafkaConsumerReceiver->listen()->invalid：设备类型获取失败]");
//					return;
//				}
//				// 获取该类型对应的告警规则
//				Map<String, Object> successValue = deviceDetail.getSuccessValue();
//				
//				String deviceTypeId = successValue.get(AlarmInfoConstants.DEVICE_TYPE_ID).toString();
//				
//				RpcResponse<List<AlarmRule>> response = alarmRuleQueryService.getByDeviceTypeId(deviceTypeId,
//						accessSecret);
//				if (!response.isSuccess()) {
//					logger.error("[KafkaConsumerReceiver->listen()->invalid：获取设备类型id:" + deviceTypeId + "对应的告警规则失败！]");
//					return;
//				}
//				alarmRules = response.getSuccessValue();
//			}
//
//			String reportTime = DateUtils.stampToDate(object.getString(AlarmInfoConstants.TIMESTAMP) + "000"); // 设备上报时间
//			JSONObject state = object.getJSONObject(AlarmInfoConstants.STATE);
//			JSONObject reported = state.getJSONObject(AlarmInfoConstants.REPORTED); // 获取上报数据
//
//			List<String> rules = new ArrayList<>(); // 封装完整规则数据
//			List<Map<String, Object>> datas = new ArrayList<>(); // 封装上报数据和设备设施数据
//			// 组装数据
//			for (AlarmRule alarmRule : alarmRules) {
//				Map<String, Object> deviceRealState = new HashMap<>();
//				/** 设备上报数据 */
//				if (reported != null) { // 解析上报数据
//					Set<String> keySet = reported.keySet();
//					for (String propName : keySet) {
//						deviceRealState.put(propName, reported.get(propName));
//					}
//				}
//				rules.add(alarmRule.getRule()); // 封装完整规则数据
//				/** 告警规则相关数据 */
//				deviceRealState.put(AlarmInfoConstants._REPORTTIME, reportTime);
//				deviceRealState.put("_rule", alarmRule);
//				deviceRealState.put(AlarmInfoConstants._DEVICEID, deviceId);
//				deviceRealState.put("_engine_check", new Boolean(false));
//				datas.add(deviceRealState);
//			}
//			logger.info("告警规则：" + rules);
//			logger.info("上报数据：" + datas);
//
//			alarmRuleInvokInterface.invokAlarm(datas, rules); // 调用规则引擎方法，datas：部分数据作为中转传递到工单生成的方法
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
