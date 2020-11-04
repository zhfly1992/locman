/*
 * File name: WingIotCommand.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2020年4月27日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.api.wingiot.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2020年4月27日
 */

public class WingIotCommand {

	/**
	 * 
	 * @Description:发送命令到单个设备 格式 { "content": { "params": { "status": 1,
                       "temperature": 26 }, "serviceIdentifier": "eeeeee"
                        }, "deviceId": "string", "operator": "string",
                        "productId": 0, "ttl": 7200, "deviceGroupId": 100,
                       "level": 1 }
	 *                        接口详情https://www.ctwing.cn/openPlatform.html#/apiDetail/10256/219
	 * 
	 * @param deviceId
	 * @param productId
	 * @param order
	 *            命令数据点和对应的值 eg:{"status":1,"temperature":26}
	 * @return
	 */
	public static RpcResponse<JSONObject> sendCommandToSingleDeviceForTest(String deviceId, String productId,
			JSONObject order) {

		if (!WintIotConStants.product.containsKey(productId)) {
			return RpcResponseBuilder.buildErrorRpcResp("查询不到产品id对应的Master-APIkey");
		}
		AepDeviceCommandClient client = AepDeviceCommandClient.newClient().appKey(WintIotConStants.application)
				.appSecret(WintIotConStants.secret).build();
		// 获取Master-APIkey
		String masterApikey = WintIotConStants.product.get(productId);
		CreateCommandRequest request = new CreateCommandRequest();
		request.setParamMasterKey(masterApikey);
		JSONObject commandBody = new JSONObject();
		JSONObject content = new JSONObject();
		// serviceIdentifier,服务定义时的服务标识
		content.put("serviceIdentifier", "Set_Lock");
		content.put("params", order);
		commandBody.put("deviceId", deviceId);
		commandBody.put("operator", "locman");
		commandBody.put("productId", productId);
		commandBody.put("ttl", 7200);
		commandBody.put("deviceGroupId", null);
		commandBody.put("level", 1);
		commandBody.put("content", content);
		request.setBody(commandBody.toJSONString().getBytes());
		try {
			CreateCommandResponse createCommand = client.CreateCommand(request);
			if (createCommand.getStatusCode() != 200) {
				String string = new String(createCommand.getBody());
				JSONObject parseObject = JSONObject.parseObject(string);
				return RpcResponseBuilder.buildErrorRpcResp(parseObject.getString("msg"));
			}
			// 获取相应内容
			JSONObject responseBody = JSONObject.parseObject(new String(createCommand.getBody()));

			if (responseBody.getIntValue("code") != 0) {
				return RpcResponseBuilder.buildErrorRpcResp(
						"错误码:" + responseBody.getIntValue("code") + "--------" + responseBody.getString("msg"));
			}
			return RpcResponseBuilder.buildSuccessRpcResp("指令发送成功", responseBody.getJSONObject("result"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		} finally {
			System.out.println("测试");
			client.shutdown();
		}
	}
	
	/**
	 * 
	 * @Description:发送命令到单个设备 格式 { "content": { "params": { "status": 1,
                       "temperature": 26 }, "serviceIdentifier": "eeeeee"
                        }, "deviceId": "string", "operator": "string",
                        "productId": 0, "ttl": 7200, "deviceGroupId": 100,
                       "level": 1 }
	 *                        接口详情https://www.ctwing.cn/openPlatform.html#/apiDetail/10256/219
	 * 
	 * @param deviceId
	 * @param productId
	 * @param order
	 *            命令数据点和对应的值 eg:{"status":1,"temperature":26}
	 * @return
	 */
	public static RpcResponse<JSONObject> sendCommandToSingleDevice(String deviceId, String productId,
			JSONObject order,Map<String, String> productTable,String serviceIdentifier) throws Exception{

		if (!productTable.containsKey(productId)) {
			return RpcResponseBuilder.buildErrorRpcResp("查询不到产品id对应的Master-APIkey");
		}
		AepDeviceCommandClient client = AepDeviceCommandClient.newClient().appKey(WintIotConStants.application)
				.appSecret(WintIotConStants.secret).build();
		// 获取Master-APIkey
		String masterApikey = productTable.get(productId);
		CreateCommandRequest request = new CreateCommandRequest();
		request.setParamMasterKey(masterApikey);
		JSONObject commandBody = new JSONObject();
		JSONObject content = new JSONObject();
		// serviceIdentifier,服务定义时的服务标识
		content.put("serviceIdentifier", serviceIdentifier);
		content.put("params", order);
		commandBody.put("deviceId", deviceId);
		commandBody.put("operator", "locman");
		commandBody.put("productId", productId);
		commandBody.put("ttl", 7200);
		commandBody.put("deviceGroupId", null);
		commandBody.put("level", 1);
		commandBody.put("content", content);
		request.setBody(commandBody.toJSONString().getBytes());
		try {
			CreateCommandResponse createCommand = client.CreateCommand(request);
			if (createCommand.getStatusCode() != 200) {
				String info = new String(createCommand.getBody());
				JSONObject parseObject = JSONObject.parseObject(info);
				return RpcResponseBuilder.buildErrorRpcResp(parseObject.getString("msg"));
			}
			// 获取相应内容
			JSONObject responseBody = JSONObject.parseObject(new String(createCommand.getBody()));

			if (responseBody.getIntValue("code") != 0) {
				return RpcResponseBuilder.buildErrorRpcResp(
						"错误码:" + responseBody.getIntValue("code") + "--------" + responseBody.getString("msg"));
			}
			return RpcResponseBuilder.buildSuccessRpcResp("指令发送成功", responseBody.getJSONObject("result"));

		}  finally {
			client.shutdown();
		}
	}
}
