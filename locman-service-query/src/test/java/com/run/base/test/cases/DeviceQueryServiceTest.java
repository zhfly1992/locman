/*
 * File name: DeviceQueryServiceTest.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年12月18日
 * ... ... ...
 *
 ***************************************************/

package com.run.base.test.cases;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.query.repository.DeviceQueryRepository;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.query.service.DeviceQueryServiceImpl;

/**
 * @Description: 设备列表查询单元测试
 * @author: 张贺
 * @version: 1.0, 2018年12月18日
 */
@RunWith(PowerMockRunner.class)
public class DeviceQueryServiceTest {
	@InjectMocks
	private DeviceQueryService		deviceQueryService	= new DeviceQueryServiceImpl();
	@Mock
	private DeviceQueryRepository	deviceQueryRepository;
	@Mock
	private MongoTemplate			mongoTemplate;



	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void mockBeforeTest() {
		try {
			MockitoAnnotations.initMocks(this);
			List<Map> list = new ArrayList<>();
			Map mapInfo = new HashMap<>(16);
			mapInfo.put("deviceId", "7zaCGkqfrVql3YWXG4oB");
			mapInfo.put("deviceName", "智能条形锁9");
			mapInfo.put("appTag", "5F4B29190862413E82DFEF6185EA87A7");
			mapInfo.put("deviceKey", "Y7c0l46AnVfVuqMaWQF5CzRA");
			mapInfo.put("manageState", "enabled");
			mapInfo.put("deviceTypeId", "ca604b088b5440e88e7d24424380bf9c");
			mapInfo.put("openProtocols", "enabled");
			mapInfo.put("protocolType", "MQTT");
			mapInfo.put("lastReportTime", null);
			mapInfo.put("deviceOnlineStatus", null);
			mapInfo.put("deviceDefendState", "normal");
			mapInfo.put("hardwareId", null);
			mapInfo.put("deviceTypeName", "智能条形锁");
			mapInfo.put("accessSecret", "a8f5d1bb1f8b8780");
			mapInfo.put("facilityId", "2d5d5c0da6aa11e893dc002324deca66");
			mapInfo.put("facilitiesCode", "007");
			mapInfo.put("factoryName", "四方条形锁");
			mapInfo.put("bingStatus", "bound");
			list.add(mapInfo);
			when(deviceQueryRepository.queryDeviceInfoForCondition(anyMap())).thenReturn(list);

			List<Map> list2 = new ArrayList<>();
			Map map2 = new HashMap<>(16);
			map2.put("deviceId", "7zaCGkqfrVql3YWXG4oB");
			map2.put("firstOnlineTime", "12312312");
			list2.add(map2);

			when(mongoTemplate.find(any(), eq(Map.class), anyString())).thenReturn(list2);

		} catch (Exception e) {
			System.out.println(e);
		}
	}



	@Test
	public void getDeviceInfoTest() {
		try {
			RpcResponse<List<Map<String, Object>>> response = deviceQueryService.queryDeviceInfoForCondition(1, 3,
					"access", "acc", "acc", "acc", "acc", "acc", "acc", "acc", "acc", "acc");

			System.out.println("[getDeviceInfoTest()->info:]" + response.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	@Test
	public void getDeviceInfoNoAccessSecretTest() {
		try {
			RpcResponse<List<Map<String, Object>>> response = deviceQueryService.queryDeviceInfoForCondition(1, 3,
					"", "acc", "acc", "acc", "acc", "acc", "acc", "acc", "acc", "acc");
			System.out.println("[getDeviceInfoNoAccessSecretTest()->info:]" + response.getMessage());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
