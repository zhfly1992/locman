package com.run.base.test.cases;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.run.locman.api.query.service.AlarmRuleQueryService;

public class PackageTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "spring-test-invoker.xml" });
			context.start();
			@SuppressWarnings("unused")
			AlarmRuleQueryService alarmRuleQueryService = (AlarmRuleQueryService) context.getBean("alarmRuleQueryService");
//			Map<String, String> map = new HashMap<>();
//			map.put("deviceTypeId","");
//			map.put("ruleName","");
//			map.put("accessSecret","");
//			RpcResponse<PageInfo<Map<String, Object>>>aMap =  alarmRuleQueryService.findAlarmRuleListByNameAndDeviceTypeId("", 0, 10, map);
//			System.err.println("数据为 : " + aMap.getSuccessValue().getList());

//			alarmRuleQueryService.queryAlarmRuleByDeviceId("", "")
			// gis地图设施查询
			// FacilitiesQueryService facilitiesQueryService =
			// (FacilitiesQueryService)context.getBean("facilitiesQueryService");
			// RpcResponse<List<Map<String, Object>>> list =
			// facilitiesQueryService.queryMapFacilities("1111", "", "", "",
			// "");
			// System.err.println(list.isSuccess());
			// System.err.println(list.getSuccessValue());
			// System.out.println("----------");
			// 设施类型查询
			// FacilitiesTypeQueryService facilitiesTypeQueryService
			// =(FacilitiesTypeQueryService)context.getBean("facilitiesTypeQueryService");
			// RpcResponse<List<FacilitiesType>>ftlist =
			// facilitiesTypeQueryService.findAllFacilities();
			// System.err.println("ftlist="+ftlist.getSuccessValue().get(0));
			// System.err.println(ftlist.isSuccess());
			// FacilitiesDataType分页查询
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
