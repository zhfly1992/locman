/*
* File name: Query4ExcelRestQueryService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2019年8月30日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.Query4ExcelService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.filetool.FastDfsUtil;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年8月30日
*/

@Service
public class Query4ExcelRestQueryService {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private Query4ExcelService	query4ExcelService;
	/**
	  * 
	  * @Description:
	  * @param 
	  * @return
	  */
	
	public Result<List<JSONObject>> deviceStateInfo4Excel(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		logger.info(String.format("[deviceStateInfo4Excel->进入方法，参数:%s]", jsonObject.toString()));
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				RpcResponse<List<JSONObject>> resultJson = query4ExcelService.deviceStateInfo4Excel(jsonObject);
				if (resultJson.isSuccess()) {
					
					byte[] infoBytesFromObject = getInfoBytesFromObject(resultJson.getSuccessValue());
					try {
						if (infoBytesFromObject == null || infoBytesFromObject.length == 0) {
							logger.error("数据异常");
						}
						String fileName = FastDfsUtil.uploadBase64File(infoBytesFromObject, UUIDUtil.getUUID()+".txt");
						query4ExcelService.saveFilePath(fileName);
					} catch (IOException e) {
						logger.error("deviceStateInfo4Excel()->exception",e);
						
					} catch (MyException e) {
						logger.error("deviceStateInfo4Excel()->exception",e);
						
					}
				}else {
					 logger.error("deviceStateInfo4Excel()->" + resultJson.getMessage());
				}
			}
		}).start();
		logger.info("执行开启");
		return ResultBuilder.successResult(null, "执行开启");
	}
	
	public Result<List<JSONObject>> deviceStateInfoCount(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		logger.info(String.format("[deviceStateInfoCount->进入方法，参数:%s]", jsonObject.toString()));
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				RpcResponse<List<JSONObject>> resultJson = query4ExcelService.deviceStateInfoCount(jsonObject);
				if (resultJson.isSuccess()) {
					
					byte[] infoBytesFromObject = getInfoBytesFromObject(resultJson.getSuccessValue());
					try {
						if (infoBytesFromObject == null || infoBytesFromObject.length == 0) {
							logger.error("数据异常");
						}
						String fileName = FastDfsUtil.uploadBase64File(infoBytesFromObject, UUIDUtil.getUUID()+".txt");
						logger.error("地址url" + fileName);						
						query4ExcelService.saveFilePath(fileName);
						
					} catch (IOException e) {
						logger.error("deviceStateInfoCount()->exception",e);
						
					} catch (MyException e) {
						logger.error("deviceStateInfoCount()->exception",e);
						
					}
				}
			}
		}).start();
		logger.info("执行开启");
		return ResultBuilder.successResult(null, "执行开启");
			
	}
	
	public Result<JSONObject> changeDeviceStateFv(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		logger.info(String.format("[changeDeviceStateFv->进入方法，参数:%s]", jsonObject.toString()));
		RpcResponse<JSONObject> resultJson = query4ExcelService.changeDeviceStateFv(jsonObject);
		if (resultJson.isSuccess()) {
			return ResultBuilder.successResult(resultJson.getSuccessValue(), "操作成功");
			
		} else {
			return ResultBuilder.failResult("操作失败!!!" + resultJson.getMessage());
		}
	}
	
	
	
	private byte[] getInfoBytesFromObject(List<JSONObject> list) {
			JSONArray jsonArray = new JSONArray();
			if(CollectionUtils.isNotEmpty(list)){
				for (JSONObject object : list) {
					jsonArray.add(object);
				}
			}
			String jsonString = jsonArray.toJSONString();
	        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();  
	        try {  
	            ObjectOutputStream objectOutputStream = new ObjectOutputStream(  
	                    arrayOutputStream);  
	            objectOutputStream.writeObject(jsonString);  
	            objectOutputStream.flush();  
	            byte[] data = arrayOutputStream.toByteArray();  
	            objectOutputStream.close();  
	            arrayOutputStream.close();  
	            return data;  
	        } catch (Exception e) {  
	           logger.error(e);
	        }  
	        return null;  

	 }
	
}
