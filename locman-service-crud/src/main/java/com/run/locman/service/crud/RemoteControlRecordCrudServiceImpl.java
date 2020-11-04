/*
 * File name: RemoteControlRecordCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年12月8日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.RemoteControlRecordCrudRepository;
import com.run.locman.api.crud.service.RemoteControlRecordCrudService;
import com.run.locman.api.entity.RemoteControlRecord;
import com.run.locman.api.wingiot.service.WingIotCommand;
import com.run.locman.constants.CommonConstants;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @Description:
 * @author: qulong
 * @version: 1.0, 2017年12月8日
 */
@Transactional(rollbackFor = Exception.class)
public class RemoteControlRecordCrudServiceImpl implements RemoteControlRecordCrudService {

	@Autowired
	private RemoteControlRecordCrudRepository	remoteControlRecordCrudRepository;

	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);



	@Override
	public RpcResponse<String> saveRemoteControlRecord(RemoteControlRecord remoteControlRecord) {
		if (remoteControlRecord == null) {
			logger.error("[RemoteControlRecordCrudServiceImpl -> saveRemoteControlRecord -> invalid: 远控记录不能为空]");
			return RpcResponseBuilder.buildErrorRpcResp("远控记录不能为空");
		}
		try {
			remoteControlRecord.setControlDestroyTime("");
			logger.info(String.format("[saveRemoteControlRecord()->进入方法,参数:%s]", remoteControlRecord));
			int result = remoteControlRecordCrudRepository.insertModel(remoteControlRecord);
			if (result > 0) {
				logger.info("[RemoteControlRecordCrudServiceImpl -> saveRemoteControlRecord -> success: 远控记录保存成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("远控记录保存成功", remoteControlRecord.getId());
			}
			logger.error("[RemoteControlRecordCrudServiceImpl -> saveRemoteControlRecord -> fail: 远控记录保存失败]");
			return RpcResponseBuilder.buildErrorRpcResp("远控记录保存失败");
		} catch (Exception e) {
			logger.error("RemoteControlRecordCrudServiceImpl()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.RemoteControlRecordCrudService#updateControlState(java.util.Map)
	 */
	@Override
	public RpcResponse<String> updateControlState(Map<String, Object> map) {
		try {
			if (null == map || map.isEmpty()) {
				logger.error("[updateControlState -> invalid: 参数不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数不能为空!");
			}
			logger.info(String.format("[updateControlState()->进入方法,参数:%s]", map));
			Integer count = remoteControlRecordCrudRepository.updateControlState(map);
			if (count > 0) {
				logger.info("[updateControlState -> success: 修改命令记录为无效成功!]");
				return RpcResponseBuilder.buildSuccessRpcResp("修改命令记录为无效成功!", null);
			}
			logger.error("[updateControlState -> success: 修改命令记录为无效失败!]");
			return RpcResponseBuilder.buildErrorRpcResp("修改命令记录为无效失败!");
		} catch (Exception e) {
			logger.error("RemoteControlRecordCrudServiceImpl()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.RemoteControlRecordCrudService#sendCommandForWings(java.lang.String, java.lang.String, com.alibaba.fastjson.JSONObject, java.util.Map, java.lang.String)
	 */
	@Override
	public RpcResponse<String> sendCommandForWings(String deviceId, String productId, JSONObject order,
			Map<String, String> productTable, String serviceIdentifier) {
		// TODO Auto-generated method stub
		logger.info("[sendCommandForWings()->access methos]");
		try {
			RpcResponse<JSONObject> sendCommandToSingleDevice = WingIotCommand.sendCommandToSingleDevice(deviceId,
					productId, order, productTable, "Set_Lock");
			if (!sendCommandToSingleDevice.isSuccess()) {
				logger.error(String.format("sendCommandForWings()->error:deviceId:%s,errorMsg:%s", deviceId,
						sendCommandToSingleDevice.getMessage()));
				
				
			
				return RpcResponseBuilder.buildErrorRpcResp("命令发送失败，" + sendCommandToSingleDevice.getMessage());
			} else {
				logger.info(String.format("wingsIotOpenLock()->success：发送指令成功设备Id：%s", deviceId));
				
				return RpcResponseBuilder.buildSuccessRpcResp("命令发送成功", null);
			}
		} catch (Exception e) {
			logger.error("operateLock()->excpetion", e);

			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
