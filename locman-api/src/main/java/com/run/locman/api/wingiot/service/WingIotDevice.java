/*
 * File name: WingIotDevice.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2020年4月2日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.wingiot.service;




import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctg.ag.sdk.biz.AepDeviceManagementClient;
import com.ctg.ag.sdk.biz.AepDeviceModelClient;
import com.ctg.ag.sdk.biz.AepProductManagementClient;
import com.ctg.ag.sdk.biz.aep_device_management.*;
import com.ctg.ag.sdk.biz.aep_device_model.QueryServiceListRequest;
import com.ctg.ag.sdk.biz.aep_device_model.QueryServiceListResponse;
import com.ctg.ag.sdk.biz.aep_product_management.QueryProductRequest;
import com.ctg.ag.sdk.biz.aep_product_management.QueryProductResponse;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;



/**
 * @Description: 调用电信iot设备相关接口
 * @author: 张贺
 * 
 * @version: 1.0, 2020年4月2日
 */

public class WingIotDevice {
	/**
	 * 
	 * @Description:根据productId查询设备列表
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static RpcResponse<JSONObject> getDeviceListByProductId(String productId,Map<String, String> productTable) throws Exception {
		if (!productTable.containsKey(productId)) {
			return RpcResponseBuilder.buildErrorRpcResp("查询不到产品id对应的Master-APIkey");
		}
		System.out.println("进入getDeviceListByProductId方法");
		AepDeviceManagementClient client = AepDeviceManagementClient.newClient().appKey(WintIotConStants.application)
				.appSecret(WintIotConStants.secret).build();

		QueryDeviceListRequest request = new QueryDeviceListRequest();
		// set your request params here
		request.setParamMasterKey(productTable.get(productId)); // single value
		// request.addParamMasterKey(MasterKey); // or multi values
		request.setParamProductId(productId); // single value
		// request.addParamProductId(productId); // or multi values
		// request.setParamSearchValue(searchValue); // single value
		// request.addParamSearchValue(searchValue); // or multi values
		// request.setParamPageNow(pageNow); // single value
		// request.addParamPageNow(pageNow); // or multi values
		 request.setParamPageSize(100); // single value
		// request.addParamPageSize(pageSize); // or multi values
		
		
	//	System.out.println(client.QueryDeviceList(request));
		 System.out.println("========productId:" + productId);
		QueryDeviceListResponse queryDeviceList = client.QueryDeviceList(request);
		System.out.println(queryDeviceList.getMessage() + "============" + queryDeviceList.getStatusCode());
		if (queryDeviceList.getStatusCode() != 200) {
			System.out.println(queryDeviceList.getMessage());
			client.shutdown();
			return RpcResponseBuilder.buildErrorRpcResp(queryDeviceList.getMessage());
		} else {
			JSONObject jsonObject = JSONObject.parseObject(new String(queryDeviceList.getBody()));
			JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("list");
			System.out.println(jsonArray.size());

			if (jsonObject.getInteger("code") != 0) {
	//			System.out.println(jsonObject.getString("msg"));
				client.shutdown();
				return RpcResponseBuilder.buildErrorRpcResp(jsonObject.getInteger("code") + "-----" +jsonObject.getString("msg"));
			} else {
	//			System.out.println(jsonObject.getJSONObject("result"));
				// 封装数据
				JSONObject result = new JSONObject();
				result.put("code", jsonObject.getInteger("code"));
				result.put("total", jsonObject.getJSONObject("result").getInteger("total"));
				result.put("list", jsonObject.getJSONObject("result").getJSONArray("list"));
				client.shutdown();
				return RpcResponseBuilder.buildSuccessRpcResp("success", result);
			}
		}
	}
	
	public static void getSendCommondServiceList() throws Exception{
		AepDeviceModelClient client = AepDeviceModelClient.newClient()
				  .appKey(WintIotConStants.application).appSecret(WintIotConStants.secret)
				  .build();

				QueryServiceListRequest request = new QueryServiceListRequest();
				// set your request params here
				request.setParamMasterKey("614dd732688f451888f7156b8915954b");	// single value
				// request.addParamMasterKey(MasterKey);	// or multi values
				request.setParamProductId("10053083");	// single value
				// request.addParamProductId(productId);	// or multi values
				// request.setParamSearchValue(searchValue);	// single value
				// request.addParamSearchValue(searchValue);	// or multi values
				// request.setParamServiceType(serviceType);	// single value
				// request.addParamServiceType(serviceType);	// or multi values
				// request.setParamPageNow(pageNow);	// single value
				// request.addParamPageNow(pageNow);	// or multi values
				// request.setParamPageSize(pageSize);	// single value
				// request.addParamPageSize(pageSize);	// or multi values
				QueryServiceListResponse queryServiceListResponse = client.QueryServiceList(request);
				System.out.println(queryServiceListResponse);
				if (queryServiceListResponse.getStatusCode() == 200) {
					JSONObject jsonObject = JSONObject.parseObject(new String(queryServiceListResponse.getBody()));
					if (jsonObject.getIntValue("code") != 0) {
						System.out.println(jsonObject.getString("msg"));
					}
					else{
					//	JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("list");
							List<JSONObject> parseArray = JSONObject.parseArray(jsonObject.getJSONObject("result").getString("list"), JSONObject.class);
							for(JSONObject jsonObject2 : parseArray){
								if (jsonObject2.getIntValue("serviceType") == 6) {
									System.out.println("服务标识: " + jsonObject2.getString("serviceFlag"));
									List<JSONObject> parseArray2 = JSONObject.parseArray(jsonObject2.getString("properties"), JSONObject.class);
									for(JSONObject jsonObject3 : parseArray2){
										System.out.println("属性点： " + jsonObject3.getString("propertyFlag"));
									}
								}
						}
					}
				}
				else{
					System.out.println(queryServiceListResponse.getMessage());
				}

				// more requests

				client.shutdown();
	}
	
	
	public static void  getProductInfo(String productId) throws Exception{
		AepProductManagementClient  client = AepProductManagementClient .newClient()
				  .appKey(WintIotConStants.application).appSecret(WintIotConStants.secret)
				  .build();
		QueryProductRequest request = new QueryProductRequest();
		request.setParamProductId(productId);
		QueryProductResponse queryProduct = client.QueryProduct(request);
		JSONObject jsonObject = JSONObject.parseObject(new String(queryProduct.getBody()));
		JSONObject jsonObject2 = jsonObject.getJSONObject("result");
		String string = jsonObject2.getString("apiKey");
		String s = jsonObject2.getString("deviceCount");
		System.out.println(string + "--------" + s);
		System.out.println(jsonObject.toJSONString());
	}
	
}