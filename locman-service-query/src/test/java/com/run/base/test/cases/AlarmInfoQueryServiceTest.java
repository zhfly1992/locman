/*
 * File name: AlarmInfoQueryServiceTest.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 王胜 2018年12月17日 ... ... ...
 *
 ***************************************************/

package com.run.base.test.cases;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.entity.Pages;
import com.run.locman.api.query.repository.AlarmInfoQueryRepository;
import com.run.locman.api.query.service.AlarmInfoQueryService;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.api.query.service.FacilityDeviceQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.query.service.AlarmInfoQueryServiceImpl;

/**
 * @Description:状态列表单元测试
 * @author: 王胜
 * @version: 1.0, 2018年12月17日
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ InterGatewayUtil.class })
public class AlarmInfoQueryServiceTest {
	@InjectMocks
	private AlarmInfoQueryService		alarmInfoQueryService	= new AlarmInfoQueryServiceImpl();

	@Mock
	private AlarmInfoQueryRepository	alarmInfoQueryRepository;

	@Mock
	private FacilityDeviceQueryService	facilityDeviceQueryService;

	@Mock
	private DeviceQueryService			deviceQueryService;



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void mockTest() {
		try {
			MockitoAnnotations.initMocks(this);

			List<Map> list = new ArrayList();
			Map map1 = new HashMap<>(16);
			map1.put("_id", "324923fce63344e3984974fef824c4a0");
			map1.put("sourceName", "科技部");
			Map map2 = new HashMap<>(16);
			map2.put("_id", "cc72d423c3024108b1f518c477ca2860");
			map2.put("sourceName", "研发部");
			Map map3 = new HashMap<>(16);
			map3.put("_id", "897895e59d924f40af046cf14cf83afc");
			map3.put("sourceName", "研发1部");
			Map map4 = new HashMap<>(16);
			map4.put("_id", "07f04008367a4534a1c847fa807b9940");
			map4.put("sourceName", "研发2部");
			list.add(map1);
			list.add(map2);
			list.add(map3);
			list.add(map4);
			// 查询组织id
			PowerMockito.mockStatic(InterGatewayUtil.class);
			PowerMockito.when(
					InterGatewayUtil.getHttpValueByGet(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
					.thenReturn(JSONObject.toJSONString(list.toArray()));

			// 查询总条数
			when(alarmInfoQueryRepository.countTotaleOfStateList(any(Map.class))).thenReturn(3);

			List<Map> lsit2 = new ArrayList<>();
			Map mapInfo = new HashMap<>(16);
			mapInfo.put("id", "9a32880ad7fd11e893dc002324deca66");
			mapInfo.put("deviceId", "rE08PtdZVsjSYObX3f1c_001");
			mapInfo.put("facilitiesCode", "0101");
			mapInfo.put("address", "高新区");
			mapInfo.put("latitude", "29.15454444");
			mapInfo.put("longitude", "104.1545445");
			mapInfo.put("organizationId", "cc72d423c3024108b1f518c477ca2860");
			mapInfo.put("completeAddress", "四川省成都市高新南区高新区");
			mapInfo.put("facilityTypeAlias", "通讯人井");
			mapInfo.put("alarmLevel", 1);
			mapInfo.put("areaId", "510000,510100,510186");
			mapInfo.put("facilitiesTypeId", "cc7f50c1acc311e893dc002324deca66");
			mapInfo.put("facilityTypeName", "通讯人井");
			mapInfo.put("alarmTime", "2018-11-13 18:02:47");

			lsit2.add(mapInfo);
			// 查询告警信息
			when(alarmInfoQueryRepository.getAlarmInfoByAccessSercret(any(Map.class))).thenReturn(lsit2);

			List<String> listId = new ArrayList<>();
			listId.add("rE08PtdZVsjSYObX3f1c_001");
			listId.add("rE08PtdZVsjSYObX3f1c_123");
			// 根据设施id查询设备成功！
			when(facilityDeviceQueryService.queryFacilityBindingState(anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("根据设施id查询设备成功！", listId));

			JSONObject json = new JSONObject();
			json.put("timestamp", "2018-10-25 10:24:47");

			// 查询最近一次上报的时间
			when(deviceQueryService.queryDeviceLastState(anyString()))
					.thenReturn(RpcResponseBuilder.buildSuccessRpcResp("根据设施id查询设备成功！", json));

			JSONObject jsonOrg = new JSONObject();
			jsonOrg.put("cc72d423c3024108b1f518c477ca2860", "研发部");
			jsonOrg.put("07f04008367a4534a1c847fa807b9940", "研发2部");
			jsonOrg.put("897895e59d924f40af046cf14cf83afc", "研发1部");
			// 查询组织id及名称
			PowerMockito.mockStatic(InterGatewayUtil.class);
			PowerMockito.when(InterGatewayUtil.getHttpValueByPost(Mockito.anyString(), any(JSONObject.class),
					Mockito.anyString(), Mockito.anyString())).thenReturn(jsonOrg.toJSONString());

		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:测试状态列表查询
	 */
	@Test
	public void getAlarmInfo() {
		try {
			// 测试状态列表查询
			RpcResponse<Pages<Map<String, Object>>> alarmInfoBypage = alarmInfoQueryService
					.getAlarmInfoBypage(getJsonParam());
			System.out.println("getAlarmInfoBypage()->info: " + alarmInfoBypage.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	/**
	 * 
	 * @Description:封装查询状态列表需要的参数
	 * @return
	 */
	private JSONObject getJsonParam() {
		JSONObject json = new JSONObject();
		json.put("accessSecret", "a0203063-f2b0-406a-8481-18ff31ca50b2");
		json.put("addressKey", "高新");
		json.put("alarmLevel", 1);
		json.put("facilitiesCode", 0101);
		json.put("facilitiesTypeId", "cc7f50c1acc311e893dc002324deca66");
		json.put("organizationId", "324923fce63344e3984974fef824c4a0");
		json.put("pageNo", 1);
		json.put("pageSize", 10);
		// json.put("ip", "http://193.168.0.94");
		// json.put("Token", "token-65cb2ae161c64d83a30a4700fa0c29ef");
		return json;
	}
}
