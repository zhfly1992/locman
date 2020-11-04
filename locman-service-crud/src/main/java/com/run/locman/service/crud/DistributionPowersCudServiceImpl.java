/*
* File name: DistributionPowersCudServiceImpl.java
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
* 1.0			qulong		2018年1月18日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DistributionPowersCudRepository;
import com.run.locman.api.crud.service.DistributionPowersCudService;
import com.run.locman.api.entity.DistributionPowers;
import com.run.locman.constants.CommonConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Map;

/**
 * @Description: 分权分域cud实现类
 * @author: qulong
 * @version: 1.0, 2017年9月15日
 */
@Transactional(rollbackFor = Exception.class)
public class DistributionPowersCudServiceImpl implements DistributionPowersCudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DistributionPowersCudRepository	distributionPowersCudRepository;



	@Override
	public RpcResponse<String> saveDistributionPowers(DistributionPowers distributionPowers) {
		if (distributionPowers == null) {
			logger.error("分权分域对象为空");
			return RpcResponseBuilder.buildErrorRpcResp("分权分域配置对象不能为空");
		}
		try {
			logger.info(String.format("[saveDistributionPowers()->进入方法,参数%s]", distributionPowers));
			int insertModelResult = distributionPowersCudRepository.insertModel(distributionPowers);
			if (insertModelResult > 0) {
				logger.debug("分权分域配置保存成功");
				return RpcResponseBuilder.buildSuccessRpcResp("分权分域配置保存成功", distributionPowers.getId());
			}else {
				logger.error("分权分域配置保存失败，数据库异常");
				return RpcResponseBuilder.buildErrorRpcResp("分权分域配置保存失败，数据库异常");
			}
		} catch (Exception e) {
			logger.error("saveDistributionPowers()->exception",e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<String> editDistributionPowers(Map<String, String> params) {
		if (params == null) {
			logger.error("修改参数为空");
			return RpcResponseBuilder.buildErrorRpcResp("修改参数不能为空");
		}
		if (params.get(CommonConstants.ID) == null) {
			logger.error("主键ID为空");
			return RpcResponseBuilder.buildErrorRpcResp("主键ID不能为空");
		}
		try {
			logger.info(String.format("[editDistributionPowers()->进入方法,参数%s]", params));
			int updatePartResult = distributionPowersCudRepository.updatePart(params);
			if (updatePartResult > 0) {
				logger.debug("分权分域配置修改成功");
				return RpcResponseBuilder.buildSuccessRpcResp("分权分域配置修改成功", String.valueOf(params.get("id")));
			}
			logger.error("分权分域配置修改失败，数据库异常");
			return RpcResponseBuilder.buildErrorRpcResp("分权分域配置修改失败，数据库异常");
		} catch (Exception e) {
			logger.error("分权分域配置修改失败", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<Boolean> deleteDistributionPowers(String id) {
		if (id == null || "".equals(id)) {
			logger.error("主键ID为空");
			return RpcResponseBuilder.buildErrorRpcResp("主键ID不能为空");
		}
		try {
			logger.info(String.format("[editDistributionPowers()->进入方法,参数:id:%s]", id));
			int deleteByIdResult = distributionPowersCudRepository.deleteById(id);
			if (deleteByIdResult > 0) {
				logger.debug("分权分域配置删除成功");
				return RpcResponseBuilder.buildSuccessRpcResp("分权分域配置删除成功", true);
			}
			logger.error("分权分域配置删除失败，数据库异常");
			return RpcResponseBuilder.buildErrorRpcResp("分权分域配置删除失败，数据库异常");
		} catch (Exception e) {
			logger.error("分权分域配置删除失败", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
