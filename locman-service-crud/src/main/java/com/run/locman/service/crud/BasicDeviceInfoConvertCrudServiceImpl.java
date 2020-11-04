/*
* File name: DeviceDataStorageCudServiceImpl.java								
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
* 1.0			guofeilong		2018年11月26日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.service.BasicDeviceInfoConvertCrudService;
import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.constants.CommonConstants;
import com.run.locman.api.crud.repository.BaseDataSynchronousStateCrudRepository;
import com.run.locman.api.crud.repository.BasicDeviceInfoConvertCrudRepository;
/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年2月22日
*/
@Transactional(rollbackFor = Exception.class)
public class BasicDeviceInfoConvertCrudServiceImpl implements BasicDeviceInfoConvertCrudService {

	private Logger logger = Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private BasicDeviceInfoConvertCrudRepository basicDeviceInfoConvertCrudRepository;

	@Autowired
	private BaseDataSynchronousStateCrudRepository baseDataSynchronousStateCrudRepository;

	@Override
	public RpcResponse<Boolean> basicDeviceInfoConvertadd(String accessSecret) {
		try {
			logger.info(String.format("[basicDeviceInfoConvertadd()->info:进入方法,accessSecret:%s]", accessSecret));
			Boolean result = basicDeviceInfoConvertCrudRepository.basicDeviceInfoConvertadd(accessSecret);
			if (result) {
				// 设置为已同步过
				BaseDataSynchronousState updateInfo = new BaseDataSynchronousState();
				updateInfo.setAccessSecret(accessSecret);
				updateInfo.setBaseDeviceInfoConvert(false);
				logger.info(String.format("[basicDeviceInfoConvertadd()->info:添加参数:%s]", updateInfo));
				baseDataSynchronousStateCrudRepository.updatePart(updateInfo);
				logger.info("basicDeviceInfoConvertadd-->success,特殊值转换同步成功");
				return RpcResponseBuilder.buildSuccessRpcResp("特殊值转换成功", result);
			}
			logger.error("basicDeviceInfoConvertadd-->error,特殊值转换同步失败");
			return RpcResponseBuilder.buildErrorRpcResp("特殊值转换同步失败");
		} catch (Exception e) {
			logger.error("basicDeviceInfoConvertadd-->exception");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}
}
