package com.run.base.test.cases;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PackageTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "spring-test-invoker.xml" });
			context.start();
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
