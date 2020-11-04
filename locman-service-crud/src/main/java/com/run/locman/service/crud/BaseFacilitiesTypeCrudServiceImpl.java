/*
 * File name: BaseFacilitiesTypeCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年4月24日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.BaseDataSynchronousStateCrudRepository;
import com.run.locman.api.crud.repository.BaseFacilitiesTypeCrudRepository;
import com.run.locman.api.crud.service.BaseFacilitiesTypeCrudService;
import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.BasicDataConstants;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年4月24日
 */
@Transactional(rollbackFor = Exception.class)
public class BaseFacilitiesTypeCrudServiceImpl implements BaseFacilitiesTypeCrudService {

	private Logger									logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private BaseFacilitiesTypeCrudRepository		baseFacilitiesTypeCrudRepository;

	@Autowired
	private BaseDataSynchronousStateCrudRepository	baseDataSynchronousStateCrudRepository;


	/**
	 * @see com.run.locman.api.crud.service.BaseFacilitiesTypeCrudService#basicFacilitiesSynchronous()
	 */
	@Override
	public RpcResponse<Boolean> basicFacilitiesSynchronous(String accessSecret, String userId) {
		try {
			logger.info("basicFacilitiesSynchronous()-->进入方法,参数:accessSecret:" + accessSecret + ",userId:" + userId);
			Map<String, Object> synchronousInfoMap = Maps.newHashMap();
			synchronousInfoMap.put(BasicDataConstants.USC_ACCESS_SECRET, accessSecret);
			synchronousInfoMap.put(BasicDataConstants.USER_ID, userId);
			//同步基础设施类型
			/*Boolean synchronousFacilitiesTypeBase = baseFacilitiesTypeCrudRepository.synchronousFacilitiesTypeBase(userId);
			if (synchronousFacilitiesTypeBase) {
				logger.info(String.format("[basicFacilitiesSynchronous()success:-->%s]", "基础设施类型同步成功"));
			} else {
				logger.error(String.format("[basicFacilitiesSynchronous()-->%s]", "基础设施类型同步失败!"));
				return RpcResponseBuilder.buildErrorRpcResp("基础设施类型数据同步失败");
			}*/
			logger.info(String.format("[basicFacilitiesSynchronous()->info:同步状态参数:%s]",synchronousInfoMap));
			Boolean synchronous = baseFacilitiesTypeCrudRepository.synchronous(synchronousInfoMap);
			if (synchronous) {
				BaseDataSynchronousState updateInfo = new BaseDataSynchronousState();
				updateInfo.setAccessSecret(accessSecret);
				updateInfo.setBaseFacilitiesType(false);
				// 失败的情况再考虑
				baseDataSynchronousStateCrudRepository.updatePart(updateInfo);
				logger.info(String.format("[basicFacilitiesSynchronous()success:-->设施类型数据同步成功,%s]", updateInfo));
				return RpcResponseBuilder.buildSuccessRpcResp("设施类型数据同步成功", synchronous);
			} else {
				logger.error("[basicFacilitiesSynchronous()-->设施类型数据同步失败!");
				return RpcResponseBuilder.buildErrorRpcResp("设施类型数据同步失败");
			}

		} catch (Exception e) {
			logger.error("basicFacilitiesSynchronous()-->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@Override
	public RpcResponse<BaseDataSynchronousState> getSynchronousStateByAS(String accessSecret) {
		try {
			logger.info("getSynchronousStateByAS()-->进入方法,参数:accessSecret:" + accessSecret);
			BaseDataSynchronousState baseDataSynchronousState = baseDataSynchronousStateCrudRepository
					.getSynchronousStateByAS(accessSecret);
			if (baseDataSynchronousState == null) {
				// 没有则初始化该接入方的同步信息
				BaseDataSynchronousState newBaseDataSynchronousState = new BaseDataSynchronousState();
				newBaseDataSynchronousState.setAccessSecret(accessSecret);
				newBaseDataSynchronousState.setBaseAlarmRule(true);
				newBaseDataSynchronousState.setBaseDeviceInfoConvert(true);
				newBaseDataSynchronousState.setBaseDeviceTypeTemplate(true);
				newBaseDataSynchronousState.setBaseFacilitiesType(true);
				newBaseDataSynchronousState.setId(UtilTool.getUuId());

				int insertModel = baseDataSynchronousStateCrudRepository
						.addSynchronousStateinfo(newBaseDataSynchronousState);
				// 数据插入成功
				if (insertModel > 0) {
					logger.info(String.format("[getSynchronousStateByAS()success:-->此接入方未同步过数据,自动生成接入方同步信息,accessSecret:%s]", accessSecret));
					return RpcResponseBuilder.buildSuccessRpcResp("此接入方未同步过数据,自动生成接入方同步信息", newBaseDataSynchronousState);
				} else {
					logger.error("[getSynchronousStateByAS()-->查询失败!]");
					return RpcResponseBuilder.buildErrorRpcResp("查询失败");
				}
			} else {
				logger.info(String.format("[getSynchronousStateByAS()success:-->此接入方同步数据信息查询成功:%s]", baseDataSynchronousState));
				return RpcResponseBuilder.buildSuccessRpcResp("此接入方同步数据信息查询成功", baseDataSynchronousState);
			}
		} catch (Exception e) {
			logger.error("getSynchronousStateByAS()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.BaseFacilitiesTypeCrudService#getBFSStateByAS(java.lang.String)
	 */
	@Override
	public RpcResponse<Boolean> getBFSStateByAS(String accessSecret) {
		logger.info(String.format("[getBFSStateByAS()->进入方法,参数:%s]", accessSecret));
		List<String> facilitiesTypeIds = baseFacilitiesTypeCrudRepository.getBFSStateByAS(accessSecret);
		if (!facilitiesTypeIds.isEmpty()) {
			logger.info("[getBFSStateByAS()-->此接入方设施类型存在同步数据,不可同步]");
			return RpcResponseBuilder.buildSuccessRpcResp("此接入方设施类型存在同步数据,不可同步", false);
		} else {
			logger.info("[getBFSStateByAS()-->此接入方设施类型没有同步数据,可以同步]");
			return RpcResponseBuilder.buildSuccessRpcResp("此接入方设施类型没有同步数据,可以同步", true);
		}
		
	}

}
