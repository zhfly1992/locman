/*
 * File name: SimpleOrderCrudServiceTest.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年12月13日
 * ... ... ...
 *
 ***************************************************/

package com.run.base.test.cases;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.activity.api.constans.ActivityConstans;
import com.run.activity.api.crud.ActivityProgressCrud;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.base.test.DemoProvider;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacInfoOrderCrudRepository;
import com.run.locman.api.crud.repository.SimpleOrderCrudRepository;
import com.run.locman.api.crud.repository.SimpleOrderDeviceCrudRepository;
import com.run.locman.api.crud.service.SimpleOrderCrudService;
import com.run.locman.api.entity.SimpleOrderDevice;
import com.run.locman.api.entity.SimpleOrderProcess;
import com.run.locman.api.model.FacilitiesModel;
import com.run.locman.api.query.service.FacilitiesQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.query.service.OrderProcessQueryService;
import com.run.locman.api.query.service.SimpleOrderQueryService;
import com.run.locman.constants.MessageConstant;
import com.run.locman.service.crud.SimpleOrderCrudServiceImpl;

/**
 * @Description: 一般工单测试类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年12月13日
 */
@SpringBootTest(classes = DemoProvider.class)
public class SimpleOrderCrudServiceTest {

	@InjectMocks
	private SimpleOrderCrudService			simpleOrderCrudService	= new SimpleOrderCrudServiceImpl();

	@Mock
	private FacilitiesQueryService			facilitiesQueryService;

	@Mock
	private OrderProcessQueryService		orderProcessQueryService;

	@Mock
	private SimpleOrderQueryService			simpleOrderQueryService;

	@Mock
	private FacilityDeviceQueryService		facilityDeviceQueryService;

	@Mock
	private FacInfoOrderCrudRepository		facInfoOrderCrudRepository;

	@Mock
	private SimpleOrderDeviceCrudRepository	orderDeviceCrudRepository;

	@Mock
	private ActivityProgressCrud			activityProgressCrud;

	@Mock
	private ActivityProgressQuery			activityProgressQuery;

	@Mock
	private SimpleOrderCrudRepository		simpleOrderRepository;



	@SuppressWarnings("unchecked")
	@Before
	public void mockBeforeTest() {
		try {
			MockitoAnnotations.initMocks(this);
			// 通过设施id查询是否有停用的设施
			when(facilitiesQueryService.getFacilityMangerStateById(any(List.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("其中不存在停用的设施！", Boolean.TRUE));

			// 根据组织id查询用户节点信息
			JSONArray jsonArray = new JSONArray();
			jsonArray.add("test");
			when(orderProcessQueryService.queryNodeInfoForActivity(any(Map.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("查询用户节点信息成功", jsonArray));

			// 查询工单流水号-> 便于工作流调用激光推送
			when(simpleOrderQueryService.findOrderNumber(any(String.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("查询成功！", "10001"));

			// 通过设施id集合查询这些设施下的所有设备
			List<String> findDeviceByFacIds = Lists.newArrayList();
			findDeviceByFacIds.add("testDeviceId");
			when(facilityDeviceQueryService.findDeviceByFacIds(any(List.class))).thenReturn(
					RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.SEARCH_SUCCESS, findDeviceByFacIds));

			// 插入工单与设施的关系
			when(facInfoOrderCrudRepository.insertModel(any(FacilitiesModel.class))).thenReturn(1);

			// 插入工单与设备的关系
			when(orderDeviceCrudRepository.insertModel(any(SimpleOrderDevice.class))).thenReturn(1);

			// 启动工作流程
			Map<String, Object> map = new HashMap<String, Object>(1);
			map.put("processId", "10001");
			when(activityProgressCrud.startProgress(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp(ActivityConstans.ACTIVITY_DATA, map));

			// 完成发起人节点
			when(activityProgressCrud.acceptProgress(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp(ActivityConstans.ACTIVITY_DATA, map));

			// 通过流程id查询当前流程的状态
			map.put("status", "testStatus");
			when(activityProgressQuery.getProcessStatus(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp(ActivityConstans.ACTIVITY_DATA, map));

			// 保存工单信息
			when(simpleOrderRepository.insertModel(any(SimpleOrderProcess.class))).thenReturn(1);

		} catch (Exception e) {
			System.out.println(e);
		}

	}



	/**
	 * 
	 * @Description:测试新增工单 正向流程
	 */
	@Test
	public void testSimpleOrderAdd() {
		try {
			// 测试新增作业工单
			RpcResponse<String> maps = simpleOrderCrudService.simpleOrderAdd(beanJSON());
			System.out.println("testSimpleOrderAdd()->info: " + maps.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:不存在type字段
	 */

	@Test
	public void testSimpleOrderAddNotType() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("type");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotType()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:不存在organizeId字段
	 */

	@Test
	public void testSimpleOrderAddNotOrganizeId() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("organizeId");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotOrganizeId()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:不存在facilitiesList字段
	 */

	@Test
	public void testSimpleOrderAddNotFacilitiesList() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			// 不存在的情况
			beanJSON.remove("facilitiesList");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotFacilitiesList()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:设施停用if进入
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleOrderAddNotFacilitiesListBlock() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			// 1.接口失败情况 参数返回调用
			when(facilitiesQueryService.getFacilityMangerStateById(any(List.class))).thenReturn(null);
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotFacilitiesListBlock()->info: " + simpleOrderAdd.getMessage());

			// 2.设施被停用情况
			when(facilitiesQueryService.getFacilityMangerStateById(any(List.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("其中存在停用的设施！", Boolean.FALSE));
			simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotFacilitiesListBlock()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少orderName
	 */

	@Test
	public void testSimpleOrderAddNotOrderName() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("orderName");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotOrderName()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少constructBy
	 */

	@Test
	public void testSimpleOrderAddNotconstructBy() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("constructBy");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotconstructBy()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少manager
	 */

	@Test
	public void testSimpleOrderAddNotmanager() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("manager");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotmanager()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少phone
	 */

	@Test
	public void testSimpleOrderAddNotphone() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("phone");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotphone()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少processStartTime
	 */

	@Test
	public void testSimpleOrderAddNotprocessStartTime() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("processStartTime");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotprocessStartTime()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少processEndTime
	 */

	@Test
	public void testSimpleOrderAddNotprocessEndTime() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("processEndTime");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotprocessEndTime()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少mark
	 */

	@Test
	public void testSimpleOrderAddNotmark() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("mark");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotmark()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少orderImg
	 */

	@Test
	public void testSimpleOrderAddNotorderImg() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("orderImg");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotorderImg()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少userId
	 */

	@Test
	public void testSimpleOrderAddNotuserId() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("userId");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotuserId()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:缺少accessSecret
	 */

	@Test
	public void testSimpleOrderAddNotaccessSecret() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			beanJSON.remove("accessSecret");
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:流程缺少权限
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleOrderAddNotAuthz() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			JSONArray jsonArray = new JSONArray();
			when(orderProcessQueryService.queryNodeInfoForActivity(any(Map.class)))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("查询用户节点信息成功", jsonArray));
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:无法通过设施id查询设备id
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleOrderAddNotDeviceIds() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			when(facilityDeviceQueryService.findDeviceByFacIds(any(List.class)))
					.thenReturn(RpcResponseBuilder.buildErrorRpcResp("查询失败！"));
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:创建启动工作流失败！
	 */

	@Test
	public void testSimpleOrderAddNotstartActivity() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			when(activityProgressCrud.startProgress(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildErrorRpcResp("启动失败!"));
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:完成发起人节点失败
	 */

	@Test
	public void testSimpleOrderAddNotacceptProgress() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			when(activityProgressCrud.acceptProgress(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildErrorRpcResp("完成任务节点失败!"));
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:通过流程id查询流程状态失败
	 */

	@Test
	public void testSimpleOrderAddNotgetProcessStatus() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			when(activityProgressQuery.getProcessStatus(any(JSONObject.class)))
					.thenReturn(RpcResponseBuilder.buildErrorRpcResp("完成任务节点失败!"));
			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:保存工单信息失败
	 */

	@Test
	public void testSimpleOrderAddNotinsertModel() {
		try {
			// 测试新增作业工单
			JSONObject beanJSON = beanJSON();
			// 保存工单信息
			when(simpleOrderRepository.insertModel(any(SimpleOrderProcess.class))).thenReturn(0);

			// 模拟删除(删除失败) 测试不在意是否删除成功
			when(activityProgressCrud.deleteProcess(any(String.class)))
					.thenReturn(RpcResponseBuilder.buildErrorRpcResp("test"));

			RpcResponse<String> simpleOrderAdd = simpleOrderCrudService.simpleOrderAdd(beanJSON);
			System.out.println("testSimpleOrderAddNotaccessSecret()->info: " + simpleOrderAdd.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:封装生成工单的参数 1. 参数完全的情况下的发起
	 * @return
	 */
	private JSONObject beanJSON() {
		JSONObject json = new JSONObject();
		json.put("orderName", "作业工单发起");
		json.put("constructBy", "sefon");
		json.put("orderType", "3");
		json.put("processStartTime", "2018-12-13 14:09:22");
		json.put("processEndTime", "2018-12-27 14:09:24");
		json.put("manager", "赵伟志");
		json.put("deviceCount", "1");
		json.put("phone", "13222222222");
		json.put("mark", "sefon");
		List<String> orderImg = Lists.newArrayList();
		orderImg.add("http://193.168.0.90:8090/groupA/M00/00/00/wagAWlwR92CIKJafAACIqPzzwxkAAAABQN7wEIAAIjA642.jpg");
		json.put("orderImg", orderImg);
		json.put("userId", "659d343b78224a6997d6f1cc0b0d48fb");
		json.put("accessSecret", "4410ac92-fb62-43e4-856c-f331ca027cd5");
		json.put("id", "");
		json.put("type", "add");
		json.put("processId", "");
		json.put("organizeId", "5897e30d99f5440b8d22a264573d7261");
		List<String> facilitiesList = Lists.newArrayList();
		facilitiesList.add("0ca0d390fe7811e88dfe002324deca66");
		json.put("facilitiesList", facilitiesList);
		return json;
	}

}
