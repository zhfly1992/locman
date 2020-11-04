package com.run.locman.api.query.service;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.AlarmRule;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年10月31日
 */
public interface AlarmRuleQueryService {
	/**
	 * @param deviceTypeId
	 *            设备类型id
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 * @Description: 根据设备类型Id查询状态属性集合
	 */
	RpcResponse<List<Map<String, Object>>> findDataPointByDeviceTypeId(String deviceTypeId, String accessSecret);



	/**
	 * 根据设备类型ID获取告警规则
	 *
	 * @param deviceTypeId
	 *            设备类型ID
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 * @Description:
	 */
	RpcResponse<List<AlarmRule>> getByDeviceTypeId(String deviceTypeId, String accessSecret);



	/**
	 * @param accessSecret
	 * @param pageNum
	 * @param pageSize
	 * @param searchParam
	 * @return
	 * @Description: 根据规则名称和设备类型查询告警列表
	 */
	RpcResponse<PageInfo<Map<String, Object>>> findAlarmRuleListByNameAndDeviceTypeId(String accessSecret, int pageNum,
			int pageSize, Map<String, String> searchParam);



	/**
	 * @param id
	 * @return
	 * @Description: 根据告警规则id查询规则信息
	 */
	RpcResponse<AlarmRule> findByRuleId(String id);



	/**
	 * @param deviceId
	 *            设备id
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 * @Description: 根据设备id查询自定义规则
	 */
	RpcResponse<List<AlarmRule>> queryAlarmRuleByDeviceId(String deviceId, String accessSecret);



	/**
	 * 
	 * @Description:校验该接入方是否有告警规则(用于同步)
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<List<AlarmRule>> getAllAlarmRule(String accessSecret);



	/**
	 * 
	 * @Description:查询所有的基础通用告警规则(用于同步)
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> getAllBasicAlarmRule();
}
