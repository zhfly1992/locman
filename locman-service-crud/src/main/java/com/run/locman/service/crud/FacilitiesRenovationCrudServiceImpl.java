/*
 * File name: FacilitiesRenovationCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2019年12月5日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.FacilitiesCrudRepository;
import com.run.locman.api.crud.repository.FacilityRenovationCrudRepository;
import com.run.locman.api.crud.service.FacilitiesRenovationCrudService;
import com.run.locman.api.crud.service.UpdateRedisCrudService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2019年12月5日
 */

public class FacilitiesRenovationCrudServiceImpl implements FacilitiesRenovationCrudService {
	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FacilityRenovationCrudRepository	facilitiesRenovationCrudRepository;

	@Autowired
	private FacilitiesCrudRepository			facilitiesCrudRepository;
	
	@Autowired
	private UpdateRedisCrudService				updateRedisCrudService;



	/**
	 * @see com.run.locman.api.crud.service.FacilitiesRenovationCrudService#addFacilitiesRenovation(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public RpcResponse<Integer> addFacilitiesRenovation(JSONObject json) {
		try {
			logger.info(String.format("[addFacilitiesRenovation()->进入方法,JSONObject--%s]", json));

			String facilityId = json.getString(FacilitiesContants.FACILITY_ID);
			String accessSecret = json.getString(CommonConstants.ACCESS_SECRET);
			String defenseState = json.getString("defenseState");
			String userId = json.getString("userId");
			List<String> facIds = Lists.newArrayList(facilityId);

			int addFacilitiesRenovation = facilitiesRenovationCrudRepository.addFacilitiesRenovation(UtilTool.getUuId(),
					facilityId, json.toJSONString(), userId);
			if (addFacilitiesRenovation > 0) {
				logger.info("[addFacilitiesRenovation->待整治申请信息保存成功]");
				int facRes = facilitiesCrudRepository.updateFacilitiesDefenseState(facIds, null, defenseState,
						accessSecret);
				if (facRes > 0) {
					//Redis缓存
					updateRedisCrudService.batchUpdateFacMapCache(facIds, accessSecret);
					logger.info("[addFacilitiesRenovation->设施状态更改为待整治审批中成功]");
					return RpcResponseBuilder.buildSuccessRpcResp("设施状态更改为待整治审批中成功", addFacilitiesRenovation);
				} else {
					logger.error("[addFacilitiesRenovation->设施状态更改为待整治审批中失败]");
					return RpcResponseBuilder.buildErrorRpcResp("设施状态更改为待整治审批中失败");

				}

			} else {
				logger.error("[addFacilitiesRenovation->待整治申请信息保存失败]");
				return RpcResponseBuilder.buildErrorRpcResp("待整治申请信息保存失败");

			}
		} catch (Exception e) {
			logger.error("[addFacilitiesRenovation()->exception]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}

	}



	@Override
	public RpcResponse<Integer> updateFacRenovationManageState(Map<String, Object> params) {
		try {
			logger.info(String.format("[updateFacRenovationManageState()->进入方法,JSONObject--%s]", params));

			int addFacilitiesRenovation = facilitiesRenovationCrudRepository.updateFacRenovationManageState(params);
			if (addFacilitiesRenovation > 0) {

				logger.info("[updateFacRenovationManageState->设施待整治信息状态更改成功]");
				return RpcResponseBuilder.buildSuccessRpcResp("设施待整治信息状态更改成功", addFacilitiesRenovation);

			} else {
				logger.error("[updateFacRenovationManageState->设施待整治信息状态更改失败]");
				return RpcResponseBuilder.buildErrorRpcResp("设施待整治信息状态更改失败");

			}
		} catch (Exception e) {
			logger.error("[updateFacRenovationManageState()->exception]", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);

		}

	};

}
