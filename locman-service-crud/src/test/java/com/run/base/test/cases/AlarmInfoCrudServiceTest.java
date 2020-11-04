/*
 * File name: AlarmInfoCrudServiceTest.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.base.test.cases;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.activity.api.query.service.ProcessFileQueryService;
import com.run.authz.base.query.UserRoleBaseQueryService;
import com.run.base.test.DemoProvider;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.AlarmInfoCrudRepository;
import com.run.locman.api.crud.repository.AlarmOrderCrudRepository;
import com.run.locman.api.crud.repository.BalanceSwitchStateRecordCurdRepository;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.repository.RemoteControlRecordCrudRepository;
import com.run.locman.api.crud.repository.SmsRegistCrudRepository;
import com.run.locman.api.crud.service.AlarmInfoCrudService;
import com.run.locman.api.crud.service.AlarmOrderCrudService;
import com.run.locman.api.crud.service.SmsRegistService;
import com.run.locman.api.entity.AlarmInfo;
import com.run.locman.api.entity.AlarmOrder;
import com.run.locman.api.entity.AlarmRule;
import com.run.locman.api.entity.Facilities;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.BalanceSwitchStateRecordQueryService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.query.service.SmsQueryService;
import com.run.locman.api.query.service.SwitchLockRecordQueryService;
import com.run.locman.api.util.SendSms;
import com.run.locman.constants.DeviceContants;
import com.run.locman.service.crud.AlarmInfoCrudServiceImpl;
import com.run.rabbit.activity.server.RabbitMqCrudService;
import com.run.sms.api.JiguangService;
import com.sefon.cud.ShadowCudService;

import entity.JiguangEntity;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月7日
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@RunWith(PowerMockRunner.class)
@SpringBootTest(classes = DemoProvider.class)
@PrepareForTest({ SendSms.class })
public class AlarmInfoCrudServiceTest {

	@InjectMocks
	private AlarmInfoCrudService					alarmInfoCrudService	= new AlarmInfoCrudServiceImpl();

	@Mock
	private AlarmInfoCrudRepository					deviceAlarmInfoCrudRepository;
	@Mock
	private AlarmOrderCrudRepository				alarmOrderCrudRepository;
	@Mock
	private FacilitiesCrudRepository				faciRespon;
	@Mock
	private BalanceSwitchStateRecordCurdRepository	balanceRespon;
	@Mock
	private RemoteControlRecordCrudRepository		remoteRespon;
	@Mock
	private UserRoleBaseQueryService				userRoleQueryService;
	@Mock
	private AlarmOrderCrudService					alarmOrderCrudService;
	@Mock
	private OrderProcessQueryService				orderProcessQueryService;
	@Mock
	private ProcessFileQueryService					processFileQueryService;
	@Mock
	private BalanceSwitchStateRecordQueryService	balanceService;
	@Mock
	private SwitchLockRecordQueryService			lockService;
	@Mock
	private SmsQueryService							smsQueryService;
	@Mock
	private SmsRegistService						smsRegistService;
	@Mock
	private JiguangService							jiguangService;
	@Mock
	private SmsRegistCrudRepository					smsRegistCrudRepository;
	@Mock
	private ShadowCudService						shadowCudService;
	@Mock
	private AlarmInfoQueryService					alarmInfoQueryService;
	@Mock
	private FactoryQueryService						factoryQueryService;
	@Mock
	private RabbitMqCrudService						rabbitMqSendClient;

	private String									alarmOrderId;
	private Boolean									isMatchOrder			= true;



	/**
	  * 
	  * @Description:当前没有工单,且自动生成工单时 isMatchOrder : true && alarmOrderId : null(或者"")
	  * @param 
	  * @return
	  */
	@Test
	public void testAdd1() {

		HashMap<String, Object> map = Maps.newHashMap();
		AlarmRule alarmRule = new AlarmRule();
		setAlarmRule(alarmRule);
		setMap(map, alarmRule);
		RpcResponse<AlarmInfo> add = alarmInfoCrudService.add(map);
		System.out.println(add);

	}
	
	/**
	  * 
	  * @Description:有工单,且自动生成工单时,更新工单和告警信息的关系表 isMatchOrder : true || alarmOrderId : "任意不太长的字符串"
	  * @param 
	  * @return
	  */
	@Test
	public void testAdd2() {
		alarmOrderId = "123";
		HashMap<String, Object> map = Maps.newHashMap();
		AlarmRule alarmRule = new AlarmRule();
		setAlarmRule(alarmRule);
		setMap(map, alarmRule);
		PowerMockito.when(alarmOrderCrudService.updateOrderAlarmId(any(Map.class)))
		.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("***同一个设备同一个规则的告警，仅更新告警ID", alarmOrderId));
		RpcResponse<AlarmInfo> add = alarmInfoCrudService.add(map);
		System.out.println(add);

	}	
	
	/**
	  * 
	  * @Description: 没有工单。规则配置不产生工单,发送短信和推送到app isMatchOrder : false && alarmOrderId : null(或者"")
	  * @param 
	  * @return
	  */
	@Test
	public void testAdd3() {
		isMatchOrder = false;
		alarmOrderId = null;
		HashMap<String, Object> map = Maps.newHashMap();
		AlarmRule alarmRule = new AlarmRule();
		setAlarmRule(alarmRule);
		setMap(map, alarmRule);
		PowerMockito.when(alarmOrderCrudService.updateOrderAlarmId(any(Map.class)))
		.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("***同一个设备同一个规则的告警，仅更新告警ID", alarmOrderId));
		RpcResponse<AlarmInfo> add = alarmInfoCrudService.add(map);
		System.out.println(add);

	}

	/**
	 * 
	 * @Description: 静态类的mock方法,配置之后不调用静态类中的相关方法
	 * @param
	 * @return
	 */
	/*
	 * @BeforeClass
	 * 
	 * @PrepareForTest({SendSms.class,TransactionAspectSupport.class}) public
	 * static void beforeClass()
	 */
	{
		// 短信发送
		PowerMockito.mockStatic(SendSms.class);
		PowerMockito.when(SendSms.sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("短信发送成功", "短信发送成功"));
	}



	@Before
	public void setUp() {
		try {
			MockitoAnnotations.initMocks(this);
			HashMap<String, Object> map = Maps.newHashMap();
			AlarmRule alarmRule = new AlarmRule();
			setAlarmRule(alarmRule);
			setMap(map, alarmRule);

			/**
			 * 通过修改下面参数的值控制测试方向: 1.当前没有工单,且自动生成工单时 isMatchOrder : true &&
			 * alarmOrderId : null(或者"") 2.有工单,且自动生成工单时,更新工单和告警信息的关系表
			 * isMatchOrder : true || alarmOrderId : "任意不太长的字符串"
			 * 3.没有工单。规则配置不产生工单,发送短信和推送到app isMatchOrder : false && alarmOrderId
			 * : null(或者"")
			 */
			isMatchOrder = true;
			alarmRule.setIsMatchOrder(isMatchOrder);
			alarmOrderId = "";

			Facilities facilities = new Facilities();
			// 设施id不为空视为已绑定设施
			facilities.setId("设施id不为空");
			facilities.setAddress("地址:江海市");
			PowerMockito.when(faciRespon.getFacByDeviceId(anyString())).thenReturn(facilities);
			// @1校验是否是一体化人井
			PowerMockito.when(lockService.checkLock(anyString(), anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("校验是否是一体化人井", DeviceContants.OPEN));
			// @2检查平衡开关,平衡开关为开(DeviceContants.OPEN)则不产生告警
			PowerMockito.when(balanceRespon.getBancanceStateByDevId(any(Map.class))).thenReturn("close");
			// @3判断命令开启是否合法(3分钟之内为合法,不告警)
			RemoteControlRecord remoteControlRecord = new RemoteControlRecord();
			remoteControlRecord.setControlTime("2018-06-06 21:43:53");
			PowerMockito.when(remoteRespon.getRemoteRecordByDevId(anyString())).thenReturn(remoteControlRecord);
			// @4判断是否在点击关闭按钮后三分钟内有上报，若是，则不触发告警。如果超过三分钟。则告警
			PowerMockito.when(balanceService.getLatestCloseTime(anyString(), anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("三分钟内没有上报", "2018-06-06 21:43:53"));
			// 超过三分钟，撤回命令
			// @5其他方式（应急钥匙、强开）生成告警记录
			// 返回值大于0,告警信息保存成功!
			PowerMockito.when(deviceAlarmInfoCrudRepository.insertModel(any(AlarmInfo.class))).thenReturn(1);
			// 校验当前是否存在该设备该规则的工单 update操作成功且返回的告警工单id不为null不为空说明已经有告警工单
			// 通过对这次操作的返回值和isMatchOrder的值控制来判断是新生成工单告警工单或是保存告警工单和告警信息关系
			PowerMockito.when(alarmOrderCrudService.updateOrderAlarmId(any(Map.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("***同一个设备同一个规则的告警，仅更新告警ID", alarmOrderId));

			// ***进入createOrderAndSendRed方法
			/**
			 * 当前没有工单,且自动生成工单时====
			 */
			// 如果是自动生成工单。并且数据库里面没有该工单，则自动创建工单,isMatchOrder是否生成工单,true生成false不生成
			// 保存告警工单
			PowerMockito.when(alarmOrderCrudService.saveAlarmOrder(any(AlarmOrder.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("告警工单保存成功", "告警工单保存成功"));
			// 保存告警信息和告警工单的关系
			PowerMockito.when(alarmOrderCrudRepository.saveAlarmOrderAndInfo(any(List.class))).thenReturn(1);

			// 告警工单保存成功，推送消息到队列中
			// 查询告警工单流程图id
			JSONObject processInfo = new JSONObject();
			processInfo.put("processSign", "alarmProcess");
			processInfo.put("manageState", "enabled");
			processInfo.put("accessSecret", "accessSecret");
			PowerMockito.when(orderProcessQueryService.findBpmnId(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("推送消息到队列成功", "推送消息到队列成功"));
			// 通过文件id获取发起人节点key(流程图上配置)
			PowerMockito.when(processFileQueryService.queryStartTaskKey(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("查询发起人节点key成功", "发起人节点key"));

			// 通过节点key查询相应的人
			List<String> startUserList = Lists.newArrayList();
			startUserList.add("人1");
			startUserList.add("人2");
			PowerMockito.when(orderProcessQueryService.findStartUsers(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("通过节点key查询相应的人", startUserList));
			// createOrderAndSendRed方法结束***

			// ***进入sendMessageAndPush方法
			// 获取告警流水号 alarmSerialNum
			PowerMockito.when(deviceAlarmInfoCrudRepository.getSerialNumBySecrete(anyString())).thenReturn(333L);
			// 根据组织id查询该组织下能或不能接收短信的用户
			JSONObject json = new JSONObject();
			json.put("organizeId", "organizationId");
			json.put("receiveSms", "true");
			List<Map> userListByOrgId = Lists.newArrayList();
			Map<String, Object> userInfo = Maps.newHashMap();
			userInfo.put("mobile", "18312345678");
			userInfo.put("_id", "123123");
			userInfo.put("userName", "测试用userName");
			userListByOrgId.add(userInfo);
			// 不测试短信发送功能时,list为空即可
			PowerMockito.when(userRoleQueryService.getUserListByOrgId(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("短信发送", userListByOrgId));

			// 查询该接入方的短信授权接口地址 该接入方下无授权返回null或者"",
			PowerMockito.when(smsQueryService.getSmsUrl(anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("查询短信授权接口地址成功", "短信授权接口地址"));

			// 创建短信授权
			PowerMockito.when(smsRegistService.smsRegist(anyString(), anyString(), anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("创建查询短信授权接口地址成功", "创建短信授权接口地址"));

			// 短信发送,静态方法内容,单独mock
			// 极光推送内容
			List<String> userIds = Lists.newArrayList();
			JiguangEntity jiguangEntity = new JiguangEntity();
			jiguangEntity.setAliasIds(userIds);
			jiguangEntity.setMsgContent("content.toString()");
			jiguangEntity.setNotificationTitle("您有一条新的告警信息");
			jiguangEntity.setMsgTitle("ruleName");
			PowerMockito.when(jiguangService.sendMessage(any(JiguangEntity.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("极光推送成功", userListByOrgId));

			// 保存短信记录
			Map<String, Object> smsRecord = Maps.newHashMap();
			smsRecord.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
			smsRecord.put("alarmSerialNum", "alarmSerialNum");
			smsRecord.put("userName", "userNameAndMobile.get(mobile)");
			smsRecord.put("phoneNumber", "13912345678");
			smsRecord.put("smsContent", "content.toString()");
			smsRecord.put("sendTime", DateUtils.formatDate(new Date()));

			PowerMockito.when(smsRegistCrudRepository.addSmsRecord(any(Map.class))).thenReturn(1);
			// webMapPush方法结束***

			// ***进入sendMessageAndPush方法
			// 地图推送到前端是查询告警信息
			AlarmInfo alarmInfo = new AlarmInfo();
			PowerMockito.when(alarmInfoQueryService.findById(anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("极光推送成功", alarmInfo));
			// 查询接入方秘钥
			PowerMockito.when(factoryQueryService.queryAccessSecretByDeviceId(anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("极光推送成功", "accessSecrets"));
			// 调用接口向前端推送实时告警信息(调用的方法无返回值,但是必须将rabbitMqSendClient对象实例化)
			// webMapPush方法结束***
			/**
			 * ====当前没有工单,且自动生成工单时
			 */

			/**
			 * 有工单,且自动生成工单时,更新工单和告警信息的关系表====
			 */

			// ***进入updateAlarmInfo方法,保存告警工单和告警信息关系
			// 保存告警工单和告警信息关系,上文已存在方法(
			// PowerMockito.when(alarmOrderCrudService.updateOrderAlarmId(any(Map.class))).thenReturn(RpcResponseBuilder.buildSuccessRpcResp("***同一个设备同一个规则的告警，仅更新告警ID",
			// "告警ID不为空"));
			// )
			// ,使返回值不为null或者""即可
			//
			// ***updateAlarmInfo方法结束

			/**
			 * ====有工单,且自动生成工单时,更新工单和告警信息的关系表
			 */

			/**
			 * 没有工单。规则配置不产生工单,发送短信和推送到app====
			 */

			// ***进入sendMessageAndPush方法,
			// 上文已存在方法
			// ***sendMessageAndPush方法结束

			/**
			 * ====没有工单。规则配置不产生工单,发送短信和推送到app
			 */

		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void setMap(HashMap<String, Object> map, AlarmRule alarmRule) {
		map.put("_deviceId", "BmHyrSQmNgIRVg6tOey0_00001");
		map.put("_engine_check", "");
		map.put("ics", "");
		map.put("_reportTime", "2018-10-31 10:13:34");
		map.put("_rule", alarmRule);
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void setAlarmRule(AlarmRule alarmRule) {
		alarmRule.setAccessSecret("");
		alarmRule.setId("");
		alarmRule.setDeviceTypeId("");
		// 告警等级,1为紧急告警
		alarmRule.setAlarmLevel(1);
		alarmRule.setCrateTime("");
		// alarmRule.setDeviceCount(deviceCount);
		alarmRule.setIsDelete("valid");
		alarmRule.setIsMatchOrder(isMatchOrder);
		alarmRule.setManageState("valid");
		alarmRule.setMatchOrder(isMatchOrder);
		alarmRule.setOderNum(1);
		alarmRule.setPublishState("invalid");
		alarmRule.setRemark("");
		alarmRule.setRule("空规则");
		JSONObject json = new JSONObject();
		json.put("condition", "!=");
		json.put("value", "close");
		json.put("key", "ics");
		alarmRule.setRuleContent(json.toJSONString());
		alarmRule.setRuleName("测试专用规则名");
		// 规则类型
		alarmRule.setRuleType("2");
		alarmRule.setUpdateTime("");
		alarmRule.setUserId("");
	}

}
