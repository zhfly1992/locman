/*
 * File name: SwitchLockRecordQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.run.locman.api.entity.SwitchLockRecord;

/**
 * @Description: 开关锁记录查询dao
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月7日
 */

public interface SwitchLockRecordQueryRepository {

	/**
	 * 
	 * @Description:dao 分页查询开关锁记录
	 * @param switchLockRecord
	 * @return
	 */
	List<Map<String, String>> listSwitchLockPage(SwitchLockRecord switchLockRecord);



	/**
	 * 
	 * @Description:一体化智能人井校验最近一条开关记录是否为开
	 * @param deviceId
	 * @param accessSecret
	 * @return String
	 */
	String checkLock(@Param("deviceId") String deviceId, @Param("accessSecret") String accessSecret);
	
	
	List<Map<String,Object>> listManholeCoverSwitch(Map<String,Object> map);
	
	List<Map<String,Object>> getManholeCoverSwitchInfo(Map<String,Object> map);
}
