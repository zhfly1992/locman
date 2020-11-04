/*
* File name: OpenRecordQueryRepository.java								
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
* 1.0			Administrator		2018年7月18日
* ...			...			...
*
***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;
import com.run.locman.api.dto.OpenRecordQueryDto;


/**
* @Description:	查询开锁记录
* @author: 张贺
* @version: 1.0, 2018年7月18日
*/

public interface OpenRecordQueryRepository {
	
	List<OpenRecordQueryDto> getOpenRecord(Map<String, Object> map);

}
