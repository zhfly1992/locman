package com.run.base.test.cases;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.crud.service.FacilitiesDataTypeCudService;
import com.run.locman.api.entity.FacilitiesDataType;


/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年2月22日
*/
public class PackageTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "spring-test-invoker.xml" });
			context.start();

			FacilitiesDataTypeCudService facilitiesDataTypeCudService = (FacilitiesDataTypeCudService) context.getBean("facilitiesDataTypeCudService");
			FacilitiesDataType facilitiesDataType = new FacilitiesDataType();
			facilitiesDataType.setId("dsasd123bfa");
			facilitiesDataType.setFacilitiesTypeId("3f1f2b441fbc4dc2b5ad2b12c3290d60");
			facilitiesDataType.setName("再来一瓶");
			RpcResponse<FacilitiesDataType> result = facilitiesDataTypeCudService.updateFacilitiesDataType(facilitiesDataType);
			System.err.println("修改后的数据为 :" + result.getSuccessValue());
			System.err.println("成功 :" + result.getSuccessValue());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
