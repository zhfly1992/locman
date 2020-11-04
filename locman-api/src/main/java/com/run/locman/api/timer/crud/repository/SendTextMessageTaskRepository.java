/*
* File name: SendTextMessageTaskRepository.java								
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
* 1.0			Administrator		2019年10月21日
* ...			...			...
*
***************************************************/

package com.run.locman.api.timer.crud.repository;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2019年10月21日
*/

public interface SendTextMessageTaskRepository {
      
	
	List<Map<String, Object>> getAlarmTextMessageForSend();
	
	int updateTextMessageRecordState(@Param ("id")String id,@Param("state")int state,@Param("sendTime")String sendTime,@Param("failReason")String failReason);
		
}
