/*
* File name: NewFaultOrderCrudServiceImpl.java								
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
* 1.0			Administrator		2019年12月7日
* ...			...			...
*
***************************************************/

package com.run.locman.service.crud;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.crud.repository.NewFaultOrderRepository;
import com.run.locman.api.crud.service.NewFaultOrderCrudService;
import com.run.locman.api.entity.NewFaultOrder;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.NewFaultOrderConstants;

/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2019年12月7日
*/

public class NewFaultOrderCrudServiceImpl implements NewFaultOrderCrudService {
	
	
	
	@Autowired
	NewFaultOrderRepository newFaultOrderRepository;
	
	
	private static final Logger					logger	= Logger.getLogger(CommonConstants.LOGKEY);

	/**
	 * @see com.run.locman.api.crud.service.NewFaultOrderCrudService#createNewFaultOrder(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<NewFaultOrder> createNewFaultOrder(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * @see com.run.locman.api.crud.service.NewFaultOrderCrudService#changeOrderState(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Object> changeOrderState(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		String orderId = "";
		String operate = "";
		//获取工单
		NewFaultOrder newFaultOrder = newFaultOrderRepository.getNewFaultOrder(orderId);
		//获取工单现在的状态
		String nowState = newFaultOrder.getOrderState();
		//如果工单状态是待处理
		if (nowState.equals(NewFaultOrderConstants.NotDeal)) {
			
		}
		return null;
	}
	
	
	private void receive() {
		
	}
	
	private void completeDeal() {
		
	}
	
	private void changeUser() {
		
	}
	
	private void autoDectPass() {
		
	}
	
	private void autoDectReject() {
		
	}
	
	private void changePass() {
		
	}
	
	private void changeReject() {
		
	}
	
	
	
	
	

}
