/*
* File name: BaseFacilitiesTypeCrudService.java								
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
* 1.0			guofeilong		2018年4月24日
* ...			...			...
*
***************************************************/

package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.BaseDataSynchronousState;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年4月24日
*/

public interface BaseFacilitiesTypeCrudService {
	
	/**
	 * 
	 * @param  synchronousInfo { userId 用户id : accessSecret 密钥}
	 * @Description: 设施类型基础数据同步
	 * @return 成功状态
	 */
	
	public RpcResponse<Boolean> basicFacilitiesSynchronous(String accessSecret ,String userId);

	/**
	  * 
	  * @Description:	查询接入方基础数据同步状态信息
	  * @param accessSecret:密钥
	  * @return BaseDataSynchronousState
	  */
	
	public RpcResponse<BaseDataSynchronousState> getSynchronousStateByAS(String accessSecret);
	
	/**
	  * 
	  * @Description:	查询是否存在该接入方设施类型同步数据(根据remark 和接入方密钥判断)
	  * @param accessSecret:密钥
	  * @return BaseDataSynchronousState
	  */
	
	public RpcResponse<Boolean> getBFSStateByAS(String accessSecret);

}
