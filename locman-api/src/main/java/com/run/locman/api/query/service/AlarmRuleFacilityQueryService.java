package com.run.locman.api.query.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;

/**
 * @Description:自定义告警规则配置接口类
 * @author: dingrunkang
 * @version: 3.0, 2017年12月4日
 */

public interface AlarmRuleFacilityQueryService {
	/**
	 * 
	 * @Description: 依据绑定条件 ,告警设施序列号,设备编号,设备类型,设施类型 ,区域,地址 查询告警设施设备信息
	 * @param alarmInfo
	 * @return
	 */
	// 此接口没有被调用过	2018年3月13日16:06:41
	/*RpcResponse<List<Map<String, Object>>> getAlarmRuleFacilityBindingStatus(JSONObject parseObject);
	*/
	  /**
	   * 
	   * @Description:添加告警通用规则获取设施设备列表
	   * @param paramInfo
	   * @return RpcResponse<PageInfo<Map<String, Object>>>
	   * @throws Exception
	   */
	  RpcResponse<PageInfo<Map<String, Object>>> getFacilityDeviceList(JSONObject paramInfo) throws Exception;
}
