package com.run.locman.api.query.repository;

import com.run.locman.api.entity.AlarmRule;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:规则引擎数据库管理类
 * @author: lkc
 * @version: 1.0, 2017年09月15日
 */
public interface AlarmRuleQueryRepository extends BaseQueryRepository<String, String> {
	/**
	 * @param map
	 * @return
	 * @Description:查询数据库中某字段的最大值
	 */
	long getMaxValueBySecrete(Map<String, String> map);



	/**
	 * @param deviceTypeId
	 *            设备类型id
	 * @return
	 * @Description: 根据设备类型Id查询数据点集合
	 */
	List<Map<String, Object>> findDataPointByDeviceTypeId(Map<String, String> params);



	/**
	 * @param params
	 * @return
	 * @Description: 根据规则名称和设备类型查询告警列表
	 */
	List<Map<String, Object>> findAlarmRuleListByNameAndDeviceTypeId(Map<String, String> params);



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
	public List<AlarmRule> getByDeviceTypeId(@Param("deviceTypeId") String deviceTypeId,
			@Param("accessSecret") String accessSecret);



	/**
	 * @param id
	 * @return
	 * @Description: 根据告警规则id查询规则信息
	 */
	AlarmRule findByRuleId(String id);



	/**
	 * @param deviceId
	 *            设备id和接入方秘钥
	 * @param accessSecret
	 *            接入方秘钥
	 * @return
	 * @Description: 根据设备id查询自定义规则
	 */
	List<AlarmRule> queryAlarmRuleByDeviceId(@Param("deviceId") String deviceId,
			@Param("accessSecret") String accessSecret);



	/**
	 * 
	 * @Description:校验该接入方是否有告警规则(用于同步)
	 * @param accessSecret
	 * @return
	 */
	List<AlarmRule> getAllAlarmRule(String accessSecret);



	/**
	 * 
	 * @Description:查询所有的基础通用告警规则(用于同步)
	 * @return
	 */
	List<Map<String, Object>> getAllBasicAlarmRule();
}
